package p001hx.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes3.dex */
public final class Streams {
    private static final int BUFFER_BYTES = 8192;

    private Streams() {
    }

    public static byte[] readAll(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[BUFFER_BYTES];
        int read = inputStream.read(bArr);
        while (read != -1) {
            byteArrayOutputStream.write(bArr, 0, read);
            read = inputStream.read(bArr);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
