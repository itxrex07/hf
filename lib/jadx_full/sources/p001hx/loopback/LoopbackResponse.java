package p001hx.loopback;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/* loaded from: classes3.dex */
final class LoopbackResponse {
    private static final String STATUS_FOUND = "302 Found";
    private static final String STATUS_NOT_FOUND = "404 Not Found";
    private static final String STATUS_OK = "200 OK";
    private static final String STATUS_PARTIAL = "206 Partial Content";
    private static final String STATUS_UNAVAILABLE = "503 Service Unavailable";
    private static final String STATUS_UNSATISFIABLE = "416 Range Not Satisfiable";

    private LoopbackResponse() {
    }

    static void writeRedirect(OutputStream outputStream, String str) throws IOException {
        write(outputStream, statusHeader(STATUS_FOUND).append("Location: ").append(str).append("\r\n").append("Content-Length: 0\r\n\r\n"));
    }

    static void writeNotFound(OutputStream outputStream) throws IOException {
        writeEmpty(outputStream, STATUS_NOT_FOUND, null);
    }

    static void writeUnavailable(OutputStream outputStream) throws IOException {
        writeEmpty(outputStream, STATUS_UNAVAILABLE, null);
    }

    static void writeUnsatisfiable(OutputStream outputStream, long j) throws IOException {
        writeEmpty(outputStream, STATUS_UNSATISFIABLE, "bytes */" + j);
    }

    static void writeHeaders(OutputStream outputStream, ByteRange byteRange, long j, boolean z) throws IOException {
        StringBuilder append = statusHeader(z ? STATUS_PARTIAL : STATUS_OK).append("Content-Length: ").append(byteRange.size()).append("\r\n");
        if (z) {
            append.append("Content-Range: bytes ").append(byteRange.start).append('-').append(byteRange.end).append('/').append(j).append("\r\n");
        }
        write(outputStream, append.append("\r\n"));
    }

    private static void writeEmpty(OutputStream outputStream, String str, String str2) throws IOException {
        StringBuilder append = statusHeader(str).append("Content-Length: 0\r\n");
        if (str2 != null) {
            append.append("Content-Range: ").append(str2).append("\r\n");
        }
        write(outputStream, append.append("\r\n"));
    }

    private static StringBuilder statusHeader(String str) {
        return new StringBuilder("HTTP/1.1 ").append(str).append("\r\n").append("Connection: close\r\nAccept-Ranges: bytes\r\nContent-Type: video/mp4\r\n");
    }

    private static void write(OutputStream outputStream, StringBuilder sb) throws IOException {
        outputStream.write(sb.toString().getBytes(StandardCharsets.ISO_8859_1));
    }
}
