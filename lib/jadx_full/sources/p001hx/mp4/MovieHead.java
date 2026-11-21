package p001hx.mp4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import p001hx.mp4.Mp4Boxes;

/* loaded from: classes3.dex */
public final class MovieHead {
    private static final long MILLIS_PER_SECOND = 1000;
    private static final int REFERENCE_DURATION_AT = 4;
    private static final int REFERENCE_SAP_AT = 8;
    private static final int REFERENCE_SIZE_AT = 0;
    private static final int SAP_FLAGS = -1879048192;
    private static final int SIDX_EARLIEST_TIME = 12;
    private static final int SIDX_FIRST_OFFSET = 16;
    private static final int SIDX_FIXED_BYTES = 24;
    private static final int SIDX_REFERENCE_BYTES = 12;
    private static final int SIDX_REFERENCE_COUNT = 22;
    private static final int SIDX_REFERENCE_ID = 4;
    private static final int SIDX_TIMESCALE = 8;
    private static final int TKHD_V0_DURATION = 28;
    private static final int TKHD_V0_TRACK_ID = 20;
    private static final int TKHD_V1_DURATION = 36;
    private static final int TKHD_V1_TRACK_ID = 28;
    private static final int V0_DURATION = 24;
    private static final int V0_TIMESCALE = 20;
    private static final int V1_DURATION = 32;
    private static final int V1_TIMESCALE = 28;

    private MovieHead() {
    }

    public static byte[] build(Track track, Track track2, InterleavedTracks interleavedTracks) throws IOException {
        return Mp4Boxes.concat(Mp4Boxes.slice(track.init, Mp4Boxes.child(track.init, REFERENCE_SIZE_AT, track.init.length, "ftyp")), moov(track.init, track2.init, track2.f2id, track.durationMillis()), sidxBoxFor(track, interleavedTracks, r6.length), sidxBoxFor(track2, interleavedTracks, 0L));
    }

    public static int trackId(byte[] bArr) throws IOException {
        Mp4Boxes.Box childOf = Mp4Boxes.childOf(bArr, trak(bArr), "tkhd");
        return (int) Mp4Boxes.u32(bArr, childOf.offset + trackIdOffset(bArr, childOf.offset));
    }

    static long mediaTimescale(byte[] bArr) throws IOException {
        Mp4Boxes.Box mdhd = mdhd(bArr, trak(bArr));
        return Mp4Boxes.u32(bArr, mdhd.offset + timescaleOffset(bArr, mdhd.offset));
    }

    static byte[] sidx(int i, long j, long j2, long j3, List<Reference> list) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[24];
        Mp4Boxes.putU32(bArr, 4, i);
        Mp4Boxes.putU32(bArr, 8, j);
        Mp4Boxes.putU32(bArr, 12, j2);
        Mp4Boxes.putU32(bArr, SIDX_FIRST_OFFSET, j3);
        Mp4Boxes.putU16(bArr, SIDX_REFERENCE_COUNT, list.size());
        byteArrayOutputStream.write(bArr, REFERENCE_SIZE_AT, 24);
        byte[] bArr2 = new byte[12];
        for (Reference reference : list) {
            Mp4Boxes.putU32(bArr2, REFERENCE_SIZE_AT, reference.byteLength);
            Mp4Boxes.putU32(bArr2, 4, reference.duration);
            Mp4Boxes.putU32(bArr2, 8, -1879048192L);
            byteArrayOutputStream.write(bArr2, REFERENCE_SIZE_AT, 12);
        }
        return Mp4Boxes.box("sidx", byteArrayOutputStream.toByteArray());
    }

    private static byte[] sidxBoxFor(Track track, InterleavedTracks interleavedTracks, long j) throws IOException {
        long mediaTimescale = mediaTimescale(track.init);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        long j2 = 0;
        for (int i = REFERENCE_SIZE_AT; i < interleavedTracks.segments.size(); i++) {
            Segment segment = interleavedTracks.segments.get(i);
            if (interleavedTracks.trackIds.get(i).intValue() == track.f2id) {
                arrayList.add(segment);
                arrayList2.add(Long.valueOf(j2));
            }
            j2 += segment.payloadBytes();
        }
        long j3 = ((Segment) arrayList.get(REFERENCE_SIZE_AT)).startTime;
        long j4 = (j3 * mediaTimescale) / track.templateTimescale;
        long j5 = j3;
        ArrayList arrayList3 = new ArrayList();
        int i2 = REFERENCE_SIZE_AT;
        long j6 = j4;
        while (i2 < arrayList.size()) {
            int i3 = i2 + 1;
            long longValue = i3 < arrayList2.size() ? ((Long) arrayList2.get(i3)).longValue() : j2;
            long j7 = mediaTimescale;
            j5 += ((Segment) arrayList.get(i2)).duration;
            long j8 = (j5 * j7) / track.templateTimescale;
            arrayList3.add(new Reference(longValue - ((Long) arrayList2.get(i2)).longValue(), j8 - j6));
            i2 = i3;
            j6 = j8;
            mediaTimescale = j7;
            arrayList = arrayList;
        }
        return sidx(track.f2id, mediaTimescale, j4, j + ((Long) arrayList2.get(REFERENCE_SIZE_AT)).longValue(), arrayList3);
    }

    private static byte[] moov(byte[] bArr, byte[] bArr2, int i, long j) throws IOException {
        Mp4Boxes.Box child = Mp4Boxes.child(bArr, REFERENCE_SIZE_AT, bArr.length, "moov");
        Mp4Boxes.Box child2 = Mp4Boxes.child(bArr2, REFERENCE_SIZE_AT, bArr2.length, "moov");
        byte[] slice = Mp4Boxes.slice(bArr, Mp4Boxes.childOf(bArr, child, "mvhd"));
        long u32 = Mp4Boxes.u32(slice, timescaleOffset(slice, REFERENCE_SIZE_AT));
        putDuration(slice, REFERENCE_SIZE_AT, (j * u32) / MILLIS_PER_SECOND);
        Mp4Boxes.putU32(slice, slice.length - 4, i + 1);
        byte[] withDurations = withDurations(Mp4Boxes.slice(bArr, trak(bArr)), u32, j);
        byte[] withDurations2 = withDurations(Mp4Boxes.slice(bArr2, trak(bArr2)), u32, j);
        Mp4Boxes.Box child3 = Mp4Boxes.child(withDurations2, 8, withDurations2.length, "tkhd");
        long j2 = i;
        Mp4Boxes.putU32(withDurations2, child3.offset + trackIdOffset(withDurations2, child3.offset), j2);
        byte[] trex = trex(bArr, child);
        byte[] trex2 = trex(bArr2, child2);
        Mp4Boxes.putU32(trex2, 12, j2);
        return Mp4Boxes.box("moov", Mp4Boxes.concat(slice, withDurations, withDurations2, Mp4Boxes.box("mvex", Mp4Boxes.concat(trex, trex2))));
    }

    private static Mp4Boxes.Box trak(byte[] bArr) throws IOException {
        return Mp4Boxes.childOf(bArr, Mp4Boxes.child(bArr, REFERENCE_SIZE_AT, bArr.length, "moov"), "trak");
    }

    private static Mp4Boxes.Box mdhd(byte[] bArr, Mp4Boxes.Box box) throws IOException {
        return Mp4Boxes.childOf(bArr, Mp4Boxes.childOf(bArr, box, "mdia"), "mdhd");
    }

    private static byte[] trex(byte[] bArr, Mp4Boxes.Box box) throws IOException {
        return Mp4Boxes.slice(bArr, Mp4Boxes.childOf(bArr, Mp4Boxes.childOf(bArr, box, "mvex"), "trex"));
    }

    private static byte[] withDurations(byte[] bArr, long j, long j2) throws IOException {
        Mp4Boxes.Box child = Mp4Boxes.child(bArr, 8, bArr.length, "tkhd");
        int versionAt = Mp4Boxes.versionAt(bArr, child.offset);
        Mp4Boxes.putDuration(bArr, child.offset + (versionAt == 1 ? TKHD_V1_DURATION : 28), versionAt, (j * j2) / MILLIS_PER_SECOND);
        Mp4Boxes.Box childOf = Mp4Boxes.childOf(bArr, Mp4Boxes.child(bArr, 8, bArr.length, "mdia"), "mdhd");
        putDuration(bArr, childOf.offset, (j2 * Mp4Boxes.u32(bArr, childOf.offset + timescaleOffset(bArr, childOf.offset))) / MILLIS_PER_SECOND);
        return bArr;
    }

    private static int timescaleOffset(byte[] bArr, int i) {
        return Mp4Boxes.versionAt(bArr, i) == 1 ? 28 : 20;
    }

    private static int trackIdOffset(byte[] bArr, int i) {
        return Mp4Boxes.versionAt(bArr, i) == 1 ? 28 : 20;
    }

    private static void putDuration(byte[] bArr, int i, long j) {
        int versionAt = Mp4Boxes.versionAt(bArr, i);
        Mp4Boxes.putDuration(bArr, i + (versionAt == 1 ? V1_DURATION : 24), versionAt, j);
    }

    static final class Reference {
        final long byteLength;
        final long duration;

        Reference(long j, long j2) {
            this.byteLength = j;
            this.duration = j2;
        }
    }
}
