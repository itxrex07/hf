package p001hx.mp4;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/* loaded from: classes3.dex */
public final class Mp4Boxes {
    public static final int FULL_HEADER_BYTES = 12;
    public static final int HEADER_BYTES = 8;
    private static final int TYPE_AT = 4;
    public static final int U32_BYTES = 4;
    private static final int VERSION_AT = 8;

    private Mp4Boxes() {
    }

    static Box child(byte[] bArr, int i, int i2, String str) throws IOException {
        while (i + 8 <= i2) {
            int size = size(bArr, i);
            if (i + size > i2) {
                throw new IOException("box " + typeAt(bArr, i) + " overruns its parent");
            }
            if (str.equals(typeAt(bArr, i))) {
                return new Box(i, size);
            }
            i += size;
        }
        throw new IOException("missing " + str + " box");
    }

    static Box childOf(byte[] bArr, Box box, String str) throws IOException {
        return child(bArr, box.offset + 8, box.end(), str);
    }

    public static int size(byte[] bArr, int i) throws IOException {
        long u32 = u32(bArr, i);
        if (u32 < 8 || u32 > 2147483647L) {
            throw new IOException("unsupported box size " + u32);
        }
        return (int) u32;
    }

    public static String typeAt(byte[] bArr, int i) {
        return new String(bArr, i + 4, 4, StandardCharsets.ISO_8859_1);
    }

    static int versionAt(byte[] bArr, int i) {
        return bArr[i + 8];
    }

    static long u32(byte[] bArr, int i) {
        return (bArr[i + 3] & 255) | ((bArr[i] & 255) << 24) | ((bArr[i + 1] & 255) << 16) | ((bArr[i + 2] & 255) << 8);
    }

    static void putU32(byte[] bArr, int i, long j) {
        bArr[i] = (byte) (j >>> 24);
        bArr[i + 1] = (byte) (j >>> 16);
        bArr[i + 2] = (byte) (j >>> 8);
        bArr[i + 3] = (byte) j;
    }

    static void putU16(byte[] bArr, int i, int i2) {
        bArr[i] = (byte) (i2 >>> 8);
        bArr[i + 1] = (byte) i2;
    }

    static void putDuration(byte[] bArr, int i, int i2, long j) {
        if (i2 == 1) {
            putU32(bArr, i, j >>> 32);
            putU32(bArr, i + 4, j);
        } else {
            putU32(bArr, i, j);
        }
    }

    static byte[] slice(byte[] bArr, Box box) {
        byte[] bArr2 = new byte[box.size];
        System.arraycopy(bArr, box.offset, bArr2, 0, box.size);
        return bArr2;
    }

    static byte[] box(String str, byte[] bArr) {
        int length = bArr.length + 8;
        byte[] bArr2 = new byte[length];
        putU32(bArr2, 0, length);
        System.arraycopy(str.getBytes(StandardCharsets.ISO_8859_1), 0, bArr2, 4, 4);
        System.arraycopy(bArr, 0, bArr2, 8, bArr.length);
        return bArr2;
    }

    static byte[] concat(byte[]... bArr) {
        int i = 0;
        for (byte[] bArr2 : bArr) {
            i += bArr2.length;
        }
        byte[] bArr3 = new byte[i];
        int i2 = 0;
        for (byte[] bArr4 : bArr) {
            System.arraycopy(bArr4, 0, bArr3, i2, bArr4.length);
            i2 += bArr4.length;
        }
        return bArr3;
    }

    static final class Box {
        final int offset;
        final int size;

        Box(int i, int i2) {
            this.offset = i;
            this.size = i2;
        }

        int end() {
            return this.offset + this.size;
        }
    }
}
