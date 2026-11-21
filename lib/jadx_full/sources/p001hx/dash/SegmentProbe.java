package p001hx.dash;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import p001hx.mp4.Mp4Boxes;
import p001hx.mp4.Segment;
import p001hx.util.RemoteFile;
import p001hx.util.Tasks;

/* loaded from: classes3.dex */
final class SegmentProbe {
    private static final int PROBE_BYTES = 256;
    private static final int SIZING_THREADS = 48;

    private SegmentProbe() {
    }

    static void size(List<Segment> list, List<Segment> list2, final CdnCookies cdnCookies) throws IOException {
        ArrayList<Segment> arrayList = new ArrayList(list);
        arrayList.addAll(list2);
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(SIZING_THREADS);
        try {
            ArrayList arrayList2 = new ArrayList();
            for (final Segment segment : arrayList) {
                arrayList2.add(newFixedThreadPool.submit(new Callable() { // from class: hx.dash.SegmentProbe.1
                    @Override // java.util.concurrent.Callable
                    public final Object call() {
                        return SegmentProbe.lambda$size$1(Segment.this, cdnCookies);
                    }
                }));
            }
            Iterator it = arrayList2.iterator();
            while (it.hasNext()) {
                Tasks.await((Future) it.next());
            }
        } finally {
            newFixedThreadPool.shutdownNow();
        }
    }

    static /* synthetic */ Void lambda$size$1(final Segment segment, final CdnCookies cdnCookies) throws Exception {
        return (Void) CdnClient.withRetries(new CdnRequest() { // from class: hx.dash.SegmentProbe.0
            @Override // p001hx.dash.CdnRequest
            public final Object send() {
                return SegmentProbe.probe(Segment.this, cdnCookies);
            }
        });
    }

    private static int trackIdOffset(byte[] bArr, int i, int i2) throws IOException {
        int findTfhd;
        int i3 = i + 8;
        if (i3 > i2 || !"moof".equals(Mp4Boxes.typeAt(bArr, i))) {
            throw new IOException("segment does not start with moof");
        }
        long j = i2;
        int min = (int) Math.min(i + Mp4Boxes.size(bArr, i), j);
        while (i3 + 8 <= min) {
            int size = Mp4Boxes.size(bArr, i3);
            if ("traf".equals(Mp4Boxes.typeAt(bArr, i3)) && (findTfhd = findTfhd(bArr, i3, (int) Math.min(i3 + size, j))) >= 0) {
                return findTfhd + 12;
            }
            i3 += size;
        }
        throw new IOException("segment lacks a tfhd within " + i2 + " bytes");
    }

    private static int prefixLength(byte[] bArr, int i) throws IOException {
        int i2 = 0;
        if (i >= 8 && "styp".equals(Mp4Boxes.typeAt(bArr, 0))) {
            i2 = 0 + Mp4Boxes.size(bArr, 0);
        }
        if (i >= i2 + 8 && "sidx".equals(Mp4Boxes.typeAt(bArr, i2))) {
            return i2 + Mp4Boxes.size(bArr, i2);
        }
        return i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Void probe(Segment segment, CdnCookies cdnCookies) throws IOException {
        HttpURLConnection connect = CdnClient.connect(segment.url, "bytes=0-255", cdnCookies, null);
        try {
            InputStream inputStream = connect.getInputStream();
            try {
                segment.byteLength = totalLength(connect, segment.url);
                byte[] bArr = new byte[PROBE_BYTES];
                int readFully = readFully(inputStream, bArr);
                segment.prefixBytes = prefixLength(bArr, readFully);
                segment.trackIdOffset = trackIdOffset(bArr, segment.prefixBytes, readFully);
                if (inputStream != null) {
                    inputStream.close();
                }
                return null;
            } finally {
            }
        } catch (NumberFormatException e) {
            throw new IOException("unreadable Content-Range for " + segment.url, e);
        }
    }

    private static long totalLength(HttpURLConnection httpURLConnection, String str) throws IOException {
        String headerField = httpURLConnection.getHeaderField("Content-Range");
        long contentLength = headerField != null && headerField.indexOf(47) >= 0 ? RemoteFile.totalFromContentRange(headerField) : httpURLConnection.getContentLength();
        if (contentLength <= 0) {
            throw new IOException("no size for " + str);
        }
        return contentLength;
    }

    private static int readFully(InputStream inputStream, byte[] bArr) throws IOException {
        int read;
        int i = 0;
        while (i < bArr.length && (read = inputStream.read(bArr, i, bArr.length - i)) >= 0) {
            i += read;
        }
        return i;
    }

    private static int findTfhd(byte[] bArr, int i, int i2) throws IOException {
        int i3 = i + 8;
        while (i3 + 12 + 4 <= i2) {
            if ("tfhd".equals(Mp4Boxes.typeAt(bArr, i3))) {
                return i3;
            }
            i3 += Mp4Boxes.size(bArr, i3);
        }
        return -1;
    }
}
