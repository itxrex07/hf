package p001hx.dash;

import android.net.Uri;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import p001hx.dash.DashManifest;
import p001hx.mp4.InterleavedTracks;
import p001hx.mp4.MovieHead;
import p001hx.mp4.Segment;
import p001hx.mp4.Track;
import p001hx.util.Logs;
import p001hx.util.Tasks;

/* loaded from: classes3.dex */
public final class DashFile {
    private static final int BYTES_PER_KIB = 1024;
    private static final double BYTES_PER_MB = 1000000.0d;
    private static final double NANOS_PER_SECOND = 1.0E9d;
    private static final int PACED_PARALLEL_READS = 16;
    private static final int PACED_READ_CHUNK_BYTES = 49152;
    private static final int READ_AHEAD_BYTES = 3145728;
    private static final String UNPACED_CDN_HOST = "sacdn.hakunaymatata.com";
    private static final int UNPACED_PARALLEL_READS = 8;
    private static final int UNPACED_READ_CHUNK_BYTES = 262144;
    private final CdnCookies cookies;
    private final byte[] head;
    public final long length;
    private final int parallelReads;
    private final Piece[] pieces;
    private final int readChunkBytes;

    private DashFile(CdnCookies cdnCookies, byte[] bArr, Piece[] pieceArr, boolean z) {
        this.cookies = cdnCookies;
        this.head = bArr;
        this.pieces = pieceArr;
        this.readChunkBytes = z ? UNPACED_READ_CHUNK_BYTES : PACED_READ_CHUNK_BYTES;
        this.parallelReads = z ? 8 : PACED_PARALLEL_READS;
        Piece piece = pieceArr[pieceArr.length - 1];
        this.length = piece.offset + piece.length;
    }

    public static DashFile open(final String str, int i, final CdnCookies cdnCookies) throws IOException {
        DashManifest parse = DashManifestParser.parse((byte[]) CdnClient.withRetries(new CdnRequest() { // from class: hx.dash.DashFile.0
            @Override // p001hx.dash.CdnRequest
            public final Object send() {
                return CdnClient.fetch(str, cdnCookies);
            }
        }), str);
        DashManifest.Representation select = parse.select("video", i);
        DashManifest.Representation select2 = parse.select("audio", Integer.MAX_VALUE);
        if (select == null || select2 == null) {
            throw new IOException("manifest lacks a video or audio track");
        }
        List<Segment> segmentsOf = parse.segmentsOf(select);
        List<Segment> segmentsOf2 = parse.segmentsOf(select2);
        SegmentProbe.size(segmentsOf, segmentsOf2, cdnCookies);
        byte[] fetchInitialization = fetchInitialization(select, cdnCookies);
        byte[] fetchInitialization2 = fetchInitialization(select2, cdnCookies);
        int trackId = MovieHead.trackId(fetchInitialization);
        Track track = new Track(fetchInitialization, trackId, select.template.timescale, segmentsOf);
        Track track2 = new Track(fetchInitialization2, trackId + 1, select2.template.timescale, segmentsOf2);
        InterleavedTracks interleave = InterleavedTracks.interleave(track, track2);
        return new DashFile(cdnCookies, MovieHead.build(track, track2, interleave), layOut(r3.length, interleave), isUnpacedCdn(str));
    }

    public static boolean isUnpacedCdn(String str) {
        return UNPACED_CDN_HOST.equals(Uri.parse(str).getHost());
    }

    public String readPattern() {
        return this.parallelReads + " x " + (this.readChunkBytes / BYTES_PER_KIB) + " KiB reads";
    }

    public void write(OutputStream outputStream, long j, long j2) throws IOException {
        RangeTransfer rangeTransfer = new RangeTransfer(j, j2);
        TransferLog.SHARED.rangeStarted();
        try {
            rangeTransfer.run(outputStream);
        } finally {
            rangeTransfer.logEnd(TransferLog.SHARED.rangeEnded());
        }
    }

    private static byte[] fetchInitialization(final DashManifest.Representation representation, final CdnCookies cdnCookies) throws IOException {
        return (byte[]) CdnClient.withRetries(new CdnRequest() { // from class: hx.dash.DashFile.1
            @Override // p001hx.dash.CdnRequest
            public final Object send() {
                return DashFile.lambda$fetchInitialization$1(DashManifest.Representation.this, cdnCookies);
            }
        });
    }

    static /* synthetic */ byte[] lambda$fetchInitialization$1(DashManifest.Representation representation, CdnCookies cdnCookies) throws IOException {
        return CdnClient.fetch(representation.initializationUrl(), cdnCookies);
    }

    private static Piece[] layOut(long j, InterleavedTracks interleavedTracks) {
        int size = interleavedTracks.segments.size();
        Piece[] pieceArr = new Piece[size];
        for (int i = 0; i < size; i++) {
            pieceArr[i] = new Piece(j, interleavedTracks.segments.get(i), interleavedTracks.trackIds.get(i).intValue());
            j += pieceArr[i].length;
        }
        return pieceArr;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int pieceAt(long j) {
        int length = this.pieces.length - 1;
        int i = 0;
        while (i < length) {
            int i2 = (i + length) >>> 1;
            if (this.pieces[i2].offset + this.pieces[i2].length <= j) {
                i = i2 + 1;
            } else {
                length = i2;
            }
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public byte[] readPiece(Piece piece, long j, long j2, ReadGroup readGroup) throws IOException {
        byte[] readRange = readRange(piece, piece.prefixBytes + j, piece.prefixBytes + j2, readGroup);
        piece.patchTrackId(readRange, j);
        return readRange;
    }

    /* JADX WARN: Removed duplicated region for block: B:39:0x00a9  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private byte[] readRange(Piece piece, long j, long j2, ReadGroup readGroup) throws IOException {
        int i = (int) ((j2 - j) + 1);
        byte[] bArr = new byte[i];
        long nanoTime = System.nanoTime();
        HttpURLConnection httpURLConnection = null;
        try {
            try {
                try {
                    HttpURLConnection connect = CdnClient.connect(piece.url, "bytes=" + j + "-" + j2, this.cookies, readGroup);
                    requireContentRange(connect, piece.url, j, j2);
                    long nanoTime2 = System.nanoTime();
                    InputStream inputStream = connect.getInputStream();
                    try {
                        readFully(inputStream, bArr, piece.url);
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (connect != null) {
                            readGroup.remove(connect);
                        }
                        TransferLog.SHARED.read(i, nanoTime2 - nanoTime, System.nanoTime() - nanoTime2);
                        return bArr;
                    } finally {
                    }
                } catch (IOException e) {
                    e = e;
                    if (readGroup.isClosed()) {
                        throw new InterruptedIOException("read cancelled: " + piece.url);
                    }
                    if (0 != 0) {
                        httpURLConnection.disconnect();
                    }
                    throw e;
                }
            } catch (Throwable th) {
                th = th;
                if (0 != 0) {
                    readGroup.remove(null);
                }
                throw th;
            }
        } catch (IOException e2) {
            e = e2;
        } catch (Throwable th2) {
            th = th2;
            if (0 != 0) {
            }
            throw th;
        }
    }

    private static void requireContentRange(HttpURLConnection httpURLConnection, String str, long j, long j2) throws IOException {
        String headerField = httpURLConnection.getHeaderField("Content-Range");
        if (httpURLConnection.getResponseCode() != 206 || headerField == null || !headerField.startsWith("bytes " + j + "-" + j2 + "/")) {
            throw new IOException("expected bytes " + j + "-" + j2 + ", got " + headerField + " for " + str);
        }
    }

    private static void readFully(InputStream inputStream, byte[] bArr, String str) throws IOException {
        int i = 0;
        while (i < bArr.length) {
            int read = inputStream.read(bArr, i, bArr.length - i);
            if (read < 0) {
                throw new IOException("segment ended early: " + str);
            }
            i += read;
        }
    }

    private final class RangeTransfer {

        /* renamed from: at */
        private long f0at;
        private final long end;
        private int index;
        private ExecutorService pool;
        private ReadGroup reads;
        private long served;
        private final long start;
        private final long begun = System.nanoTime();
        private final ArrayDeque<Future<byte[]>> pending = new ArrayDeque<>();

        RangeTransfer(long j, long j2) {
            this.start = j;
            this.end = j2;
        }

        void run(OutputStream outputStream) throws IOException {
            this.f0at = this.start;
            writeHead(outputStream);
            outputStream.flush();
            this.pool = Executors.newFixedThreadPool(DashFile.this.parallelReads);
            this.reads = new ReadGroup();
            try {
                this.index = DashFile.this.pieceAt(this.f0at);
                streamPieces(outputStream);
            } finally {
                this.reads.close();
                this.pool.shutdownNow();
            }
        }

        void logEnd(int i) {
            double nanoTime = (System.nanoTime() - this.begun) / DashFile.NANOS_PER_SECOND;
            Log.i(Logs.TAG, String.format(Locale.ROOT, "DASH range %d-%d: %.1f MB in %.1f s = %.1f MB/s, %d ranges still open", Long.valueOf(this.start), Long.valueOf(this.end), Double.valueOf(this.served / DashFile.BYTES_PER_MB), Double.valueOf(nanoTime), Double.valueOf((this.served / DashFile.BYTES_PER_MB) / nanoTime), Integer.valueOf(i)));
        }

        private void writeHead(OutputStream outputStream) throws IOException {
            if (this.f0at < DashFile.this.head.length) {
                int min = (int) (Math.min(this.end + 1, DashFile.this.head.length) - this.f0at);
                outputStream.write(DashFile.this.head, (int) this.f0at, min);
                long j = min;
                this.f0at += j;
                this.served += j;
            }
        }

        private void streamPieces(OutputStream outputStream) throws IOException {
            int max = Math.max(DashFile.this.parallelReads, DashFile.READ_AHEAD_BYTES / DashFile.this.readChunkBytes);
            while (true) {
                scheduleReads(max);
                Future<byte[]> poll = this.pending.poll();
                if (poll == null) {
                    return;
                }
                outputStream.write((byte[]) Tasks.await(poll));
                this.served += r1.length;
                TransferLog.SHARED.served(r1.length);
            }
        }

        private void scheduleReads(int i) {
            while (this.pending.size() < i && this.f0at <= this.end && this.index < DashFile.this.pieces.length) {
                final Piece piece = DashFile.this.pieces[this.index];
                final long j = this.f0at - piece.offset;
                final long min = Math.min(Math.min(this.end - piece.offset, piece.length - 1), (DashFile.this.readChunkBytes + j) - 1);
                final ReadGroup readGroup = this.reads;
                this.pending.add(this.pool.submit(new Callable() { // from class: hx.dash.DashFile.RangeTransfer.0
                    @Override // java.util.concurrent.Callable
                    public final Object call() {
                        return RangeTransfer.this.m21lambda$scheduleReads$1$hxdashDashFile$RangeTransfer(piece, j, min, readGroup);
                    }
                }));
                this.f0at = piece.offset + min + 1;
                if (min == piece.length - 1) {
                    this.index++;
                }
            }
        }

        /* renamed from: lambda$scheduleReads$0$hx-dash-DashFile$RangeTransfer, reason: not valid java name */
        /* synthetic */ byte[] m20lambda$scheduleReads$0$hxdashDashFile$RangeTransfer(Piece piece, long j, long j2, ReadGroup readGroup) throws IOException {
            return DashFile.this.readPiece(piece, j, j2, readGroup);
        }

        /* renamed from: lambda$scheduleReads$1$hx-dash-DashFile$RangeTransfer, reason: not valid java name */
        /* synthetic */ byte[] m21lambda$scheduleReads$1$hxdashDashFile$RangeTransfer(final Piece piece, final long j, final long j2, final ReadGroup readGroup) throws Exception {
            return (byte[]) CdnClient.withRetries(new CdnRequest() { // from class: hx.dash.DashFile.RangeTransfer.1
                @Override // p001hx.dash.CdnRequest
                public final Object send() {
                    return RangeTransfer.this.m20lambda$scheduleReads$0$hxdashDashFile$RangeTransfer(piece, j, j2, readGroup);
                }
            });
        }
    }
}
