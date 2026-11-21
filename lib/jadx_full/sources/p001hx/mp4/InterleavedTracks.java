package p001hx.mp4;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes3.dex */
public final class InterleavedTracks {
    public final List<Segment> segments = new ArrayList();
    public final List<Integer> trackIds = new ArrayList();

    public static InterleavedTracks interleave(Track track, Track track2) {
        InterleavedTracks interleavedTracks = new InterleavedTracks();
        double d = track.templateTimescale / track2.templateTimescale;
        int i = 0;
        int i2 = 0;
        while (i < track.segments.size()) {
            interleavedTracks.add(track.segments.get(i), track.f2id);
            i++;
            boolean z = i == track.segments.size();
            long j = z ? Long.MAX_VALUE : track.segments.get(i).startTime;
            while (i2 < track2.segments.size() && (z || track2.segments.get(i2).startTime * d < j)) {
                interleavedTracks.add(track2.segments.get(i2), track2.f2id);
                i2++;
            }
        }
        return interleavedTracks;
    }

    private void add(Segment segment, int i) {
        this.segments.add(segment);
        this.trackIds.add(Integer.valueOf(i));
    }
}
