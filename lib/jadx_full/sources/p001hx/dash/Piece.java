package p001hx.dash;

import p001hx.mp4.Segment;

/* loaded from: classes3.dex */
final class Piece {
    private static final int TRACK_ID_BYTES = 4;
    final long length;
    final long offset;
    final int prefixBytes;
    private final int trackId;
    private final int trackIdOffset;
    final String url;

    Piece(long j, Segment segment, int i) {
        this.offset = j;
        this.length = segment.payloadBytes();
        this.url = segment.url;
        this.prefixBytes = segment.prefixBytes;
        this.trackIdOffset = segment.trackIdOffset - segment.prefixBytes;
        this.trackId = i;
    }

    void patchTrackId(byte[] bArr, long j) {
        for (int i = 0; i < 4; i++) {
            long j2 = (this.trackIdOffset + i) - j;
            if (j2 >= 0 && j2 < bArr.length) {
                bArr[(int) j2] = (byte) (this.trackId >>> ((3 - i) * 8));
            }
        }
    }
}
