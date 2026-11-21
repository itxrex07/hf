package p001hx.loopback;

import android.net.Uri;
import android.util.Log;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import p001hx.dash.DashFile;
import p001hx.util.Logs;

/* loaded from: classes3.dex */
public final class DashServer implements Runnable {
    private static final int BACKLOG = 16;
    private static final byte[] LOOPBACK = {Byte.MAX_VALUE, 0, 0, 1};
    private static final int OUTPUT_BUFFER_BYTES = 65536;
    static final int PORT = 47291;
    private static final int QUEUED_REQUESTS = 16;
    private static final int SOCKET_TIMEOUT_MS = 30000;
    private static final int WORKERS = 8;
    private final ServerSocket serverSocket;
    private final DashFileSource source;
    private boolean started;
    private final ThreadPoolExecutor workers = new ThreadPoolExecutor(8, 8, 0, TimeUnit.SECONDS, new ArrayBlockingQueue(16));

    private DashServer(DashFileSource dashFileSource, ServerSocket serverSocket) {
        this.source = dashFileSource;
        this.serverSocket = serverSocket;
    }

    public static DashServer bind(DashFileSource dashFileSource) throws IOException {
        return new DashServer(dashFileSource, new ServerSocket(PORT, 16, InetAddress.getByAddress(LOOPBACK)));
    }

    public static String buildUrl(String str, int i, int i2, int i3, String str2, long j, String str3) {
        return "http://127.0.0.1:47291/dash/" + str + "/" + i + "/" + i2 + "/" + i3 + ".mp4?origin=" + Uri.encode(str2) + "&size=" + j + (str3 != null ? "&resourceId=" + Uri.encode(str3) : "");
    }

    public synchronized void start() {
        if (this.started) {
            throw new IllegalStateException("already started");
        }
        this.started = true;
        Thread thread = new Thread(this, "hx-dash-server");
        thread.setDaemon(true);
        thread.start();
    }

    public void close() {
        try {
            this.serverSocket.close();
        } catch (IOException e) {
        }
        this.workers.shutdownNow();
    }

    @Override // java.lang.Runnable
    public void run() {
        Socket accept;
        while (!this.serverSocket.isClosed() && (accept = accept()) != null) {
            dispatch(accept);
        }
    }

    private Socket accept() {
        try {
            return this.serverSocket.accept();
        } catch (IOException e) {
            if (!this.serverSocket.isClosed()) {
                Log.e(Logs.TAG, "DASH server stopped", e);
            }
            close();
            return null;
        }
    }

    private void dispatch(final Socket socket) {
        try {
            this.workers.execute(new Runnable() { // from class: hx.loopback.DashServer.0
                @Override // java.lang.Runnable
                public final void run() {
                    DashServer.this.m23lambda$dispatch$0$hxloopbackDashServer(socket);
                }
            });
        } catch (RejectedExecutionException e) {
            closeQuietly(socket);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: serve, reason: merged with bridge method [inline-methods] */
    public void m23lambda$dispatch$0$hxloopbackDashServer(Socket socket) {
        try {
            try {
                socket.setSoTimeout(SOCKET_TIMEOUT_MS);
                LoopbackRequest parse = LoopbackRequest.parse(LoopbackRequest.readHead(socket.getInputStream()));
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream(), OUTPUT_BUFFER_BYTES);
                respond(parse, bufferedOutputStream);
                bufferedOutputStream.flush();
            } finally {
                closeQuietly(socket);
            }
        } catch (SocketException e) {
            Log.i(Logs.TAG, "DASH request ended by the client: " + e.getMessage());
        } catch (Throwable th) {
            Log.w(Logs.TAG, "DASH request failed", th);
        }
    }

    private void respond(LoopbackRequest loopbackRequest, OutputStream outputStream) throws IOException {
        if (loopbackRequest == null) {
            LoopbackResponse.writeNotFound(outputStream);
            return;
        }
        try {
            DashFile open = this.source.open(loopbackRequest.subjectId, loopbackRequest.season, loopbackRequest.episode, loopbackRequest.height, loopbackRequest.origin, loopbackRequest.originSize, loopbackRequest.resourceId, loopbackRequest.startsAtBeginning());
            if (open == null) {
                writeOriginOrNotFound(loopbackRequest, outputStream);
            } else if (isSizedForAnotherFile(loopbackRequest, open)) {
                Log.w(Logs.TAG, "unavailable " + loopbackRequest.target + ": file is " + open.length + " bytes, the app expects " + loopbackRequest.originSize);
                LoopbackResponse.writeNotFound(outputStream);
            } else {
                writeFile(loopbackRequest, open, outputStream);
            }
        } catch (RedirectException e) {
            LoopbackResponse.writeRedirect(outputStream, e.location);
        } catch (UnavailableException e2) {
            Log.i(Logs.TAG, "unavailable " + loopbackRequest.target + ": " + e2.getMessage());
            LoopbackResponse.writeNotFound(outputStream);
        } catch (IOException e3) {
            Log.e(Logs.TAG, "cannot open " + loopbackRequest.target, e3);
            LoopbackResponse.writeUnavailable(outputStream);
        }
    }

    private static boolean isSizedForAnotherFile(LoopbackRequest loopbackRequest, DashFile dashFile) {
        return loopbackRequest.origin == null && loopbackRequest.originSize > 0 && loopbackRequest.originSize != dashFile.length;
    }

    private static void writeOriginOrNotFound(LoopbackRequest loopbackRequest, OutputStream outputStream) throws IOException {
        if (loopbackRequest.origin == null) {
            LoopbackResponse.writeNotFound(outputStream);
        } else {
            LoopbackResponse.writeRedirect(outputStream, loopbackRequest.origin);
        }
    }

    private static void writeFile(LoopbackRequest loopbackRequest, DashFile dashFile, OutputStream outputStream) throws IOException {
        ByteRange rangeWithin = loopbackRequest.rangeWithin(dashFile.length);
        if (!rangeWithin.isSatisfiable()) {
            LoopbackResponse.writeUnsatisfiable(outputStream, dashFile.length);
            return;
        }
        LoopbackResponse.writeHeaders(outputStream, rangeWithin, dashFile.length, loopbackRequest.isPartial());
        if (!loopbackRequest.headOnly) {
            dashFile.write(outputStream, rangeWithin.start, rangeWithin.end);
        }
    }

    private static void closeQuietly(Socket socket) {
        try {
            socket.close();
        } catch (IOException e) {
        }
    }

    public static final class RedirectException extends IOException {
        private static final long serialVersionUID = 1;
        final String location;

        public RedirectException(String str) {
            super("redirect to " + str);
            this.location = str;
        }
    }

    public static final class UnavailableException extends IOException {
        private static final long serialVersionUID = 1;

        public UnavailableException(String str) {
            super(str);
        }
    }
}
