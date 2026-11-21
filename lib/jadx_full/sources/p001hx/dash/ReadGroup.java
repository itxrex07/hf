package p001hx.dash;

import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* loaded from: classes3.dex */
final class ReadGroup {
    private boolean closed;
    private final Set<HttpURLConnection> open = new HashSet();

    ReadGroup() {
    }

    synchronized void add(HttpURLConnection httpURLConnection) throws InterruptedIOException {
        if (this.closed) {
            httpURLConnection.disconnect();
            throw new InterruptedIOException("read cancelled");
        }
        this.open.add(httpURLConnection);
    }

    synchronized void remove(HttpURLConnection httpURLConnection) {
        this.open.remove(httpURLConnection);
    }

    synchronized boolean isClosed() {
        return this.closed;
    }

    synchronized void close() {
        this.closed = true;
        Iterator<HttpURLConnection> it = this.open.iterator();
        while (it.hasNext()) {
            it.next().disconnect();
        }
        this.open.clear();
    }
}
