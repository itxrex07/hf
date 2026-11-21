package p001hx.dash;

import android.util.Xml;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import p001hx.dash.DashManifest;

/* loaded from: classes3.dex */
final class DashManifestParser {
    private static final Pattern ISO_DURATION = Pattern.compile("P(?:(\\d+)D)?(?:T(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+(?:\\.\\d+)?)S)?)?");
    private static final int MAX_DEPTH = 32;
    private static final long SECONDS_PER_DAY = 86400;
    private static final long SECONDS_PER_HOUR = 3600;
    private static final long SECONDS_PER_MINUTE = 60;
    private DashManifest.Representation representation;
    private DashManifest.Template setTemplate;
    private String setType;
    private DashManifest.Template template;
    private final DashManifest manifest = new DashManifest();
    private final String[] baseUrls = new String[MAX_DEPTH];
    private final XmlPullParser parser = Xml.newPullParser();

    private DashManifestParser(byte[] bArr, String str) throws XmlPullParserException {
        this.parser.setInput(new ByteArrayInputStream(bArr), null);
        this.baseUrls[0] = str.substring(0, str.lastIndexOf(47) + 1);
    }

    static DashManifest parse(byte[] bArr, String str) throws IOException {
        try {
            return new DashManifestParser(bArr, str).read();
        } catch (NumberFormatException | MalformedURLException | XmlPullParserException e) {
            throw new IOException("cannot parse manifest " + str, e);
        }
    }

    private static double parseDurationSeconds(String str) {
        if (str == null) {
            return 0.0d;
        }
        Matcher matcher = ISO_DURATION.matcher(str);
        if (matcher.matches()) {
            double parseLong = matcher.group(1) != null ? 0.0d + (Long.parseLong(matcher.group(1)) * SECONDS_PER_DAY) : 0.0d;
            if (matcher.group(2) != null) {
                parseLong += Long.parseLong(matcher.group(2)) * SECONDS_PER_HOUR;
            }
            if (matcher.group(3) != null) {
                parseLong += Long.parseLong(matcher.group(3)) * SECONDS_PER_MINUTE;
            }
            if (matcher.group(4) != null) {
                return parseLong + Double.parseDouble(matcher.group(4));
            }
            return parseLong;
        }
        throw new NumberFormatException("duration " + str);
    }

    private DashManifest read() throws XmlPullParserException, IOException {
        int eventType = this.parser.getEventType();
        while (eventType != 1) {
            int depth = this.parser.getDepth();
            if (depth >= MAX_DEPTH) {
                throw new IOException("manifest nested deeper than 32");
            }
            if (eventType == 2) {
                startTag(this.parser.getName(), depth);
            } else if (eventType == 3) {
                endTag(this.parser.getName(), depth);
            }
            eventType = this.parser.next();
        }
        return this.manifest;
    }

    private void startTag(String str, int i) throws XmlPullParserException, IOException {
        this.baseUrls[i] = this.baseUrls[i - 1];
        if ("MPD".equals(str)) {
            this.manifest.presentationSeconds = parseDurationSeconds(attribute("mediaPresentationDuration"));
            return;
        }
        if ("BaseURL".equals(str)) {
            readBaseUrl(i);
            return;
        }
        if ("AdaptationSet".equals(str)) {
            this.setType = readContentType();
            this.setTemplate = null;
        } else {
            if ("Representation".equals(str)) {
                startRepresentation();
                return;
            }
            if ("SegmentTemplate".equals(str)) {
                startTemplate();
            } else if ("S".equals(str) && this.template != null) {
                this.template.timeline.add(new DashManifest.Interval(longAttribute("t", -1L), longAttribute("d", 0L), longAttribute("r", 0L)));
            }
        }
    }

    private void endTag(String str, int i) {
        if ("Representation".equals(str) && this.representation != null) {
            finishRepresentation(i);
        } else if ("SegmentTemplate".equals(str)) {
            this.template = null;
        }
    }

    private void readBaseUrl(int i) throws XmlPullParserException, IOException {
        int i2 = i - 1;
        this.baseUrls[i2] = new URL(new URL(this.baseUrls[i2]), this.parser.nextText().trim()).toString();
        this.baseUrls[i] = this.baseUrls[i2];
    }

    private void startRepresentation() {
        this.representation = new DashManifest.Representation();
        this.representation.f1id = attribute("id");
        this.representation.height = intAttribute("height", 0);
        this.representation.bandwidth = intAttribute("bandwidth", 0);
        String readContentType = readContentType();
        DashManifest.Representation representation = this.representation;
        if (readContentType != null) {
            representation.type = readContentType;
        } else {
            representation.type = this.representation.height > 0 ? "video" : this.setType;
        }
    }

    private void startTemplate() {
        this.template = new DashManifest.Template();
        this.template.timescale = intAttribute("timescale", 1);
        this.template.duration = longAttribute("duration", 0L);
        this.template.initialization = attribute("initialization");
        this.template.media = attribute("media");
        this.template.startNumber = intAttribute("startNumber", 1);
        if (this.representation == null) {
            this.setTemplate = this.template;
        } else {
            this.representation.template = this.template;
        }
    }

    private void finishRepresentation(int i) {
        if (this.representation.template == null) {
            this.representation.template = this.setTemplate;
        }
        this.representation.baseUrl = this.baseUrls[i];
        if (this.representation.template != null && this.representation.template.media != null) {
            this.manifest.representations.add(this.representation);
        }
        this.representation = null;
    }

    private String readContentType() {
        String attribute = attribute("contentType");
        if (attribute != null) {
            return attribute;
        }
        String attribute2 = attribute("mimeType");
        if (attribute2 != null) {
            return attribute2.substring(0, Math.max(attribute2.indexOf(47), 0));
        }
        return null;
    }

    private String attribute(String str) {
        return this.parser.getAttributeValue(null, str);
    }

    private int intAttribute(String str, int i) {
        String attribute = attribute(str);
        return attribute != null ? Integer.parseInt(attribute) : i;
    }

    private long longAttribute(String str, long j) {
        String attribute = attribute(str);
        return attribute != null ? Long.parseLong(attribute) : j;
    }
}
