package p001hx.dash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import p001hx.mp4.Segment;

/* loaded from: classes3.dex */
final class DashManifest {
    private static final Pattern NUMBER = Pattern.compile("\\$Number(?:%0(\\d+)d)?\\$");
    private static final String REPRESENTATION_ID = "$RepresentationID$";
    double presentationSeconds;
    final List<Representation> representations = new ArrayList();

    DashManifest() {
    }

    Representation select(String str, int i) {
        Representation representation = null;
        Representation representation2 = null;
        for (Representation representation3 : this.representations) {
            if (str.equals(representation3.type)) {
                if (representation2 == null || representation3.height < representation2.height) {
                    representation2 = representation3;
                }
                if (representation3.height <= i && isBetter(representation3, representation)) {
                    representation = representation3;
                }
            }
        }
        return representation != null ? representation : representation2;
    }

    List<Segment> segmentsOf(Representation representation) throws IOException {
        Template template = representation.template;
        long j = 0;
        if (template.timescale <= 0) {
            throw new IOException("representation " + representation.f1id + " has no timescale");
        }
        List<Interval> timelineOf = timelineOf(template);
        if (timelineOf.isEmpty()) {
            throw new IOException("manifest lacks a segment timeline or segment duration");
        }
        ArrayList arrayList = new ArrayList();
        int i = template.startNumber;
        long j2 = 0;
        for (Interval interval : timelineOf) {
            if (interval.duration <= j || interval.repeats < j) {
                throw new IOException("representation " + representation.f1id + " has an invalid segment timeline");
            }
            if (interval.startTime >= j) {
                j2 = interval.startTime;
            }
            long j3 = j2;
            long j4 = j;
            while (j4 <= interval.repeats) {
                arrayList.add(new Segment(representation.baseUrl + resolveMediaUrl(template.media, representation.f1id, i), j3, interval.duration));
                j3 += interval.duration;
                j4++;
                i++;
            }
            j2 = j3;
            j = 0;
        }
        return arrayList;
    }

    private static String resolveMediaUrl(String str, String str2, int i) {
        String num;
        Matcher matcher = NUMBER.matcher(str.replace(REPRESENTATION_ID, str2));
        StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()) {
            String group = matcher.group(1);
            if (group != null) {
                num = String.format(Locale.US, "%0" + group + "d", Integer.valueOf(i));
            } else {
                num = Integer.toString(i);
            }
            matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(num));
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private static boolean isBetter(Representation representation, Representation representation2) {
        return representation2 == null || representation.height > representation2.height || (representation.height == representation2.height && representation.bandwidth > representation2.bandwidth);
    }

    private List<Interval> timelineOf(Template template) {
        if (!template.timeline.isEmpty() || template.duration <= 0 || this.presentationSeconds <= 0.0d) {
            return template.timeline;
        }
        long round = Math.round(this.presentationSeconds * template.timescale);
        long j = (round - 1) / template.duration;
        ArrayList arrayList = new ArrayList();
        if (j > 0) {
            arrayList.add(new Interval(0L, template.duration, j - 1));
        }
        arrayList.add(new Interval(j * template.duration, round - (j * template.duration), 0L));
        return arrayList;
    }

    static final class Representation {
        int bandwidth;
        String baseUrl;
        int height;

        /* renamed from: id */
        String f1id;
        Template template;
        String type;

        Representation() {
        }

        String initializationUrl() throws IOException {
            String str = this.template.initialization;
            if (str == null) {
                throw new IOException("representation " + this.f1id + " has no initialization");
            }
            return this.baseUrl + str.replace(DashManifest.REPRESENTATION_ID, this.f1id);
        }
    }

    static final class Template {
        long duration;
        String initialization;
        String media;
        final List<Interval> timeline = new ArrayList();
        long timescale = 1;
        int startNumber = 1;

        Template() {
        }
    }

    static final class Interval {
        final long duration;
        final long repeats;
        final long startTime;

        Interval(long j, long j2, long j3) {
            this.startTime = j;
            this.duration = j2;
            this.repeats = j3;
        }
    }
}
