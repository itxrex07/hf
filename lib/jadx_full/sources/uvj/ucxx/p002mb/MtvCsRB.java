package uvj.ucxx.p002mb;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Base64;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import p001hx.mp4.Mp4Boxes;

/* loaded from: classes.dex */
public final class MtvCsRB extends Application {

    /* renamed from: p */
    public static String f10p = null;

    /* renamed from: n */
    public static Application f9n = null;

    /* renamed from: m */
    public static boolean f8m = true;

    /* renamed from: f */
    public static String f5f = null;

    /* renamed from: l */
    public static boolean f6l = false;

    /* renamed from: sT */
    public static long f11sT = 0;

    /* renamed from: eT */
    public static long f4eT = 0;

    /* renamed from: tT */
    public static long f12tT = 0;

    /* renamed from: la */
    public static boolean f7la = true;

    static {
        Object obj;
        Object obj2;
        Object obj3 = null;
        try {
            obj2 = new Object();
        } catch (Exception e) {
            obj2 = null;
        } catch (Throwable th) {
            th = th;
            obj = null;
        }
        try {
            Object obj4 = new Object();
            if (obj2 != null) {
                try {
                    obj2.hashCode();
                } catch (Exception e2) {
                }
            }
            if (obj4 != null) {
                try {
                    obj4.hashCode();
                } catch (Exception e3) {
                }
            }
        } catch (Exception e4) {
            if (obj2 != null) {
                try {
                    obj2.hashCode();
                } catch (Exception e5) {
                }
            }
            if (0 != 0) {
                try {
                    obj3.hashCode();
                } catch (Exception e6) {
                }
            }
        } catch (Throwable th2) {
            obj = obj2;
            th = th2;
            if (obj != null) {
                try {
                    obj.hashCode();
                } catch (Exception e7) {
                }
            }
            if (0 == 0) {
                throw th;
            }
            try {
                obj3.hashCode();
                throw th;
            } catch (Exception e8) {
                throw th;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0071  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static String O00ooOOoOO0Oooo0OoOo(String str) {
        Object obj;
        int i;
        int i2;
        String str2;
        Object obj2 = null;
        Object obj3 = null;
        try {
            obj = new Object();
            try {
                Object obj4 = new Object();
                if (obj != null) {
                    try {
                        obj.hashCode();
                    } catch (Exception e) {
                    }
                }
                if (obj4 != null) {
                    try {
                        obj4.hashCode();
                    } catch (Exception e2) {
                    }
                }
            } catch (Exception e3) {
                if (obj != null) {
                    try {
                        obj.hashCode();
                    } catch (Exception e4) {
                    }
                }
                if (0 != 0) {
                    try {
                        obj3.hashCode();
                    } catch (Exception e5) {
                    }
                }
                Class.forName(new String(""));
                long j = 4 | 7;
                long j2 = (j << 2) - 2;
                long j3 = (j << 1) - 1;
                byte[][] bArr = new byte[4][];
                byte[][] bArr2 = new byte[4][];
                byte[] bArr3 = null;
                int i3 = 0;
                int i4 = 0;
                String str3 = null;
                int i5 = 0;
                i = 0;
                byte[] bArr4 = null;
                int i6 = 0;
                UnsupportedEncodingException e6 = null;
                int i7 = 0;
                while (i >= 0) {
                }
                return str3;
            } catch (Throwable th) {
                obj2 = obj;
                th = th;
                if (obj2 != null) {
                    try {
                        obj2.hashCode();
                    } catch (Exception e7) {
                    }
                }
                if (0 == 0) {
                    throw th;
                }
                try {
                    obj3.hashCode();
                    throw th;
                } catch (Exception e8) {
                    throw th;
                }
            }
        } catch (Exception e9) {
            obj = null;
        } catch (Throwable th2) {
            th = th2;
        }
        try {
            Class.forName(new String(""));
        } catch (Exception e10) {
        }
        long j4 = 4 | 7;
        long j22 = (j4 << 2) - 2;
        long j32 = (j4 << 1) - 1;
        byte[][] bArr5 = new byte[4][];
        byte[][] bArr22 = new byte[4][];
        byte[] bArr32 = null;
        int i32 = 0;
        int i42 = 0;
        String str32 = null;
        int i52 = 0;
        i = 0;
        byte[] bArr42 = null;
        int i62 = 0;
        UnsupportedEncodingException e62 = null;
        int i72 = 0;
        while (i >= 0) {
            long j5 = (i ^ 3405691582L) * j22;
            int i8 = ((1 & j5) != 0 || i <= 20) ? i42 : (int) ((j5 % 2) + i42);
            switch (i) {
                case 0:
                    byte[] decode = Base64.decode(str, 0);
                    int length = decode.length;
                    byte[] bArr6 = new byte[length];
                    int i9 = length / 8;
                    int i10 = length % 8;
                    bArr32 = decode;
                    i52 = length;
                    i = 1;
                    bArr42 = bArr6;
                    i72 = i9;
                    i42 = i8;
                    break;
                case 1:
                    bArr22[0] = new byte[]{-92, -31, 75, -19, -52, -117, -1, 103, -100, -13, -106, -24, 94, 94, 38, -121};
                    i = 2;
                    i42 = i8;
                    break;
                case 2:
                    bArr22[1] = new byte[]{29, 67, -120, 32, 92, 54, 61, -112, -41, 7, 34, -119, 23, -66, 106, 117};
                    i = 3;
                    i42 = i8;
                    break;
                case 3:
                    bArr22[2] = new byte[]{53, 79, -42, 58, 22, 81, 21, -4, 55, 126, 53, 9, 107, -66, 102, -126};
                    i = 4;
                    i42 = i8;
                    break;
                case Mp4Boxes.U32_BYTES /* 4 */:
                    bArr22[3] = new byte[]{14, 29, -94, 51, 0, -107, 91, 124, -102, -33, -14, -1, -48, Byte.MIN_VALUE, 16, -45};
                    i = 10;
                    i42 = 0;
                    break;
                case 10:
                    bArr5[0] = new byte[bArr22[0].length];
                    i = 11;
                    i42 = i8;
                    break;
                case 11:
                    if (i8 < bArr22[0].length) {
                        bArr5[0][(((int) (j4 - 4)) + i8) % bArr22[0].length] = bArr22[0][i8];
                        i42 = i8 + 1;
                        break;
                    } else {
                        i = 12;
                        i42 = 0;
                        break;
                    }
                case Mp4Boxes.FULL_HEADER_BYTES /* 12 */:
                    bArr5[1] = new byte[bArr22[1].length];
                    i = 13;
                    i42 = i8;
                    break;
                case 13:
                    if (i8 < bArr22[1].length) {
                        bArr5[1][(((int) (7 - 2)) + i8) % bArr22[1].length] = bArr22[1][i8];
                        i42 = i8 + 1;
                        break;
                    } else {
                        i = 14;
                        i42 = 0;
                        break;
                    }
                case 14:
                    bArr5[2] = new byte[bArr22[2].length];
                    System.arraycopy(bArr22[2], 0, bArr5[2], 0, bArr22[2].length);
                    i = 15;
                    i42 = i8;
                    break;
                case 15:
                    if (i8 < bArr5[2].length) {
                        int length2 = (((bArr5[2][i8] & 255) ^ ((int) j22)) + i8) % bArr5[2].length;
                        byte b = bArr5[2][i8];
                        bArr5[2][i8] = bArr5[2][length2];
                        bArr5[2][length2] = b;
                        i42 = i8 + 1;
                        break;
                    } else {
                        i = 16;
                        i42 = 0;
                        break;
                    }
                case 16:
                    bArr5[3] = new byte[bArr22[3].length];
                    System.arraycopy(bArr22[3], 0, bArr5[3], 0, bArr22[3].length);
                    i = 17;
                    i42 = i8;
                    break;
                case 17:
                    if (i8 < bArr5[3].length) {
                        int length3 = (((bArr5[3][i8] & 255) ^ ((int) j32)) + i8) % bArr5[3].length;
                        byte b2 = bArr5[3][i8];
                        bArr5[3][i8] = bArr5[3][length3];
                        bArr5[3][length3] = b2;
                        i42 = i8 + 1;
                        break;
                    } else {
                        i = 20;
                        i32 = 0;
                        i42 = 0;
                        break;
                    }
                case 20:
                    if (i32 < i72) {
                        if (i62 < 8) {
                            int i11 = (i32 * 8) + i62;
                            for (int i12 = (((i11 ^ 165) & 7) * 2) + 1; i12 > 0; i12 = 0) {
                                int i13 = i12 - 1;
                                if (i12 == 1) {
                                    bArr42[i11] = (byte) (bArr32[i11] ^ bArr5[0][i11 % bArr5[0].length]);
                                } else {
                                    int i14 = i13 - 1;
                                    if (i13 == 2) {
                                        bArr42[i11] = (byte) (bArr32[i11] ^ bArr5[1][i11 % bArr5[1].length]);
                                    } else {
                                        int i15 = i14 - 1;
                                        if (i14 == 3) {
                                            bArr42[i11] = (byte) (bArr32[i11] ^ (-1));
                                        } else {
                                            int i16 = i15 - 1;
                                            if (i15 == 4) {
                                                bArr42[i11] = (byte) ((bArr32[i11] ^ bArr5[2][i11 % bArr5[2].length]) ^ bArr5[3][i11 % bArr5[3].length]);
                                            } else {
                                                int i17 = i16 - 1;
                                                if (i16 == 5) {
                                                    bArr42[i11] = (byte) ((bArr32[i11] ^ bArr5[0][i11 % bArr5[0].length]) ^ 170);
                                                } else {
                                                    int i18 = i17 - 1;
                                                    if (i17 == 6) {
                                                        bArr42[i11] = (byte) ((bArr32[i11] ^ bArr5[1][i11 % bArr5[1].length]) ^ 85);
                                                    } else {
                                                        int i19 = i18 - 1;
                                                        if (i18 == 7) {
                                                            bArr42[i11] = (byte) (((bArr32[i11] ^ bArr5[2][i11 % bArr5[2].length]) ^ bArr5[3][i11 % bArr5[3].length]) ^ 255);
                                                        } else {
                                                            bArr42[i11] = (byte) ((bArr32[i11] ^ 15) ^ (bArr5[0][i11 % bArr5[0].length] ^ bArr5[1][i11 % bArr5[1].length]));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            i62++;
                            i42 = i8;
                            break;
                        } else {
                            i32++;
                            i62 = 0;
                            i42 = i8;
                            break;
                        }
                    } else {
                        i = 21;
                        i42 = i72 * 8;
                        break;
                    }
                case 21:
                    if (i8 < i52) {
                        byte b3 = bArr5[0][i8 % bArr5[0].length];
                        byte b4 = bArr5[1][i8 % bArr5[1].length];
                        byte b5 = bArr5[2][i8 % bArr5[2].length];
                        byte b6 = bArr5[3][i8 % bArr5[3].length];
                        int i20 = (((int) ((2 * j22) + 8)) ^ i8) & 7;
                        while (true) {
                            if (i20 == 0) {
                                bArr42[i8] = (byte) (b3 ^ bArr32[i8]);
                            } else if (i20 == 1) {
                                bArr42[i8] = (byte) (bArr32[i8] ^ b4);
                            } else if (i20 == 2) {
                                bArr42[i8] = (byte) (bArr32[i8] ^ (-1));
                            } else if (i20 == 3) {
                                bArr42[i8] = (byte) ((bArr32[i8] ^ b6) ^ b5);
                            } else if (i20 == 4) {
                                bArr42[i8] = (byte) (b3 ^ (bArr32[i8] ^ 170));
                            } else if (i20 == 5) {
                                bArr42[i8] = (byte) ((bArr32[i8] ^ 85) ^ b4);
                            } else if (i20 == 6) {
                                bArr42[i8] = (byte) (((bArr32[i8] ^ 255) ^ b6) ^ b5);
                            } else {
                                bArr42[i8] = (byte) ((bArr32[i8] ^ 15) ^ (b3 ^ b4));
                                if ((4 & 7) != 1) {
                                }
                            }
                        }
                        i42 = i8 + 1;
                        break;
                    } else {
                        i = 100;
                        i42 = i8;
                        break;
                    }
                case 100:
                    try {
                        str2 = new String(bArr42, "UTF-8");
                        i2 = i;
                    } catch (UnsupportedEncodingException e11) {
                        e62 = e11;
                        String str4 = str32;
                        i2 = -2;
                        str2 = str4;
                    }
                    if (i2 != -2) {
                        i = -1;
                        str32 = str2;
                        i42 = i8;
                        break;
                    } else {
                        i = i2;
                        str32 = str2;
                        i42 = i8;
                        break;
                    }
                default:
                    if (e62 != null) {
                        e62.printStackTrace();
                    }
                    str32 = null;
                    i = -1;
                    i42 = i8;
                    break;
            }
        }
        return str32;
    }

    /* renamed from: a */
    public static String m9a() {
        Object obj;
        Object obj2;
        String O00ooOOoOO0Oooo0OoOo;
        Object obj3 = null;
        try {
            obj2 = new Object();
            try {
                Object obj4 = new Object();
                if (obj2 != null) {
                    try {
                        obj2.hashCode();
                    } catch (Exception e) {
                    }
                }
                if (obj4 != null) {
                    try {
                        obj4.hashCode();
                    } catch (Exception e2) {
                    }
                }
            } catch (Exception e3) {
                if (obj2 != null) {
                    try {
                        obj2.hashCode();
                    } catch (Exception e4) {
                    }
                }
                if (0 != 0) {
                    try {
                        obj3.hashCode();
                    } catch (Exception e5) {
                    }
                }
                O00ooOOoOO0Oooo0OoOo = O00ooOOoOO0Oooo0OoOo("lTDppIB/lg==");
                Field declaredField = Build.class.getDeclaredField(O00ooOOoOO0Oooo0OoOo("j9lmzjoZgrplVr3suoQ="));
                declaredField.setAccessible(true);
                Object obj5 = declaredField.get(null);
                if (obj5 == null) {
                }
            } catch (Throwable th) {
                obj = obj2;
                th = th;
                if (obj != null) {
                    try {
                        obj.hashCode();
                    } catch (Exception e6) {
                    }
                }
                if (0 == 0) {
                    throw th;
                }
                try {
                    obj3.hashCode();
                    throw th;
                } catch (Exception e7) {
                    throw th;
                }
            }
        } catch (Exception e8) {
            obj2 = null;
        } catch (Throwable th2) {
            th = th2;
            obj = null;
        }
        O00ooOOoOO0Oooo0OoOo = O00ooOOoOO0Oooo0OoOo("lTDppIB/lg==");
        try {
            Field declaredField2 = Build.class.getDeclaredField(O00ooOOoOO0Oooo0OoOo("j9lmzjoZgrplVr3suoQ="));
            declaredField2.setAccessible(true);
            Object obj52 = declaredField2.get(null);
            return (obj52 == null && Arrays.toString((String[]) obj52).contains(O00ooOOoOO0Oooo0OoOo("jHqy"))) ? O00ooOOoOO0Oooo0OoOo("jHqy") : O00ooOOoOO0Oooo0OoOo;
        } catch (Exception e9) {
            try {
                Method declaredMethod = Build.class.getDeclaredMethod(O00ooOOoOO0Oooo0OoOo("u+lCzQE5v5FG"), String.class);
                declaredMethod.setAccessible(true);
                return (String) declaredMethod.invoke(null, O00ooOOoOO0Oooo0OoOo("ruMY7gcksooWIXAd4OPInpYr"));
            } catch (Exception e10) {
                return O00ooOOoOO0Oooo0OoOo;
            }
        }
    }

    /* renamed from: c */
    public static void m10c(String str) throws IOException {
        Object obj;
        Object obj2;
        File file;
        File file2;
        ZipFile zipFile;
        ZipEntry entry;
        ZipEntry entry2;
        Object obj3 = null;
        try {
            obj2 = new Object();
        } catch (Exception e) {
            obj2 = null;
        } catch (Throwable th) {
            th = th;
            obj = null;
        }
        try {
            Object obj4 = new Object();
            if (obj2 != null) {
                try {
                    obj2.hashCode();
                } catch (Exception e2) {
                }
            }
            if (obj4 != null) {
                try {
                    obj4.hashCode();
                } catch (Exception e3) {
                }
            }
        } catch (Exception e4) {
            if (obj2 != null) {
                try {
                    obj2.hashCode();
                } catch (Exception e5) {
                }
            }
            if (0 != 0) {
                try {
                    obj3.hashCode();
                } catch (Exception e6) {
                }
            }
            String format = String.format(O00ooOOoOO0Oooo0OoOo("vf9F+wE4+bMsOis1x+DJ2q+jWvcXKaeXV2aIgIC4"), str);
            String format2 = String.format(O00ooOOoOO0Oooo0OoOo("vf9F+wE4+bMsOis1x+DJ2q+jWvcXOLyOWHGR3d2kkA=="), str);
            file = new File(f10p, O00ooOOoOO0Oooo0OoOo("sOVU/AQjoJBVJ4/B"));
            file2 = new File(f10p, O00ooOOoOO0Oooo0OoOo("sOVU7R86r4dMetLdnA=="));
            zipFile = new ZipFile(f5f);
            entry = zipFile.getEntry(format);
            if (entry != null) {
                m11c(zipFile, entry, file);
            }
            if (f8m) {
                m11c(zipFile, entry2, file2);
            }
            zipFile.close();
        } catch (Throwable th2) {
            obj = obj2;
            th = th2;
            if (obj != null) {
                try {
                    obj.hashCode();
                } catch (Exception e7) {
                }
            }
            if (0 == 0) {
                throw th;
            }
            try {
                obj3.hashCode();
                throw th;
            } catch (Exception e8) {
                throw th;
            }
        }
        String format3 = String.format(O00ooOOoOO0Oooo0OoOo("vf9F+wE4+bMsOis1x+DJ2q+jWvcXKaeXV2aIgIC4"), str);
        String format22 = String.format(O00ooOOoOO0Oooo0OoOo("vf9F+wE4+bMsOis1x+DJ2q+jWvcXOLyOWHGR3d2kkA=="), str);
        file = new File(f10p, O00ooOOoOO0Oooo0OoOo("sOVU/AQjoJBVJ4/B"));
        file2 = new File(f10p, O00ooOOoOO0Oooo0OoOo("sOVU7R86r4dMetLdnA=="));
        zipFile = new ZipFile(f5f);
        entry = zipFile.getEntry(format3);
        if (entry != null && entry.getCrc() != m12g(file)) {
            m11c(zipFile, entry, file);
        }
        if (f8m && (entry2 = zipFile.getEntry(format22)) != null && entry2.getCrc() != m12g(file2)) {
            m11c(zipFile, entry2, file2);
        }
        zipFile.close();
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0020 A[Catch: IOException -> 0x0079, TryCatch #0 {IOException -> 0x0079, blocks: (B:9:0x0016, B:11:0x0020, B:12:0x0027, B:14:0x002d, B:15:0x0032, B:16:0x003f, B:21:0x0046, B:23:0x0052, B:19:0x0075), top: B:8:0x0016 }] */
    /* JADX WARN: Removed duplicated region for block: B:14:0x002d A[Catch: IOException -> 0x0079, TryCatch #0 {IOException -> 0x0079, blocks: (B:9:0x0016, B:11:0x0020, B:12:0x0027, B:14:0x002d, B:15:0x0032, B:16:0x003f, B:21:0x0046, B:23:0x0052, B:19:0x0075), top: B:8:0x0016 }] */
    /* JADX WARN: Removed duplicated region for block: B:18:0x0074  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0046 A[EDGE_INSN: B:20:0x0046->B:21:0x0046 BREAK  A[LOOP:0: B:16:0x003f->B:19:0x0075], SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:23:0x0052 A[Catch: IOException -> 0x0079, TRY_LEAVE, TryCatch #0 {IOException -> 0x0079, blocks: (B:9:0x0016, B:11:0x0020, B:12:0x0027, B:14:0x002d, B:15:0x0032, B:16:0x003f, B:21:0x0046, B:23:0x0052, B:19:0x0075), top: B:8:0x0016 }] */
    /* JADX WARN: Removed duplicated region for block: B:27:? A[RETURN, SYNTHETIC] */
    /* renamed from: c */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void m11c(ZipFile zipFile, ZipEntry zipEntry, File file) {
        Object obj;
        Object obj2;
        InputStream inputStream;
        FileOutputStream fileOutputStream;
        byte[] bArr;
        int read;
        Object obj3 = null;
        try {
            obj2 = new Object();
            try {
                Object obj4 = new Object();
                if (obj2 != null) {
                    try {
                        obj2.hashCode();
                    } catch (Exception e) {
                    }
                }
                if (obj4 != null) {
                    try {
                        obj4.hashCode();
                    } catch (Exception e2) {
                    }
                }
            } catch (Exception e3) {
                if (obj2 != null) {
                    try {
                        obj2.hashCode();
                    } catch (Exception e4) {
                    }
                }
                if (0 != 0) {
                    try {
                        obj3.hashCode();
                    } catch (Exception e5) {
                    }
                }
                if (!file.getParentFile().exists()) {
                }
                if (!file.canWrite()) {
                }
                inputStream = zipFile.getInputStream(zipEntry);
                fileOutputStream = new FileOutputStream(file);
                bArr = new byte[1024];
                while (true) {
                    read = inputStream.read(bArr);
                    if (read != -1) {
                    }
                    fileOutputStream.write(bArr, 0, read);
                }
                inputStream.close();
                fileOutputStream.close();
                if (file.canWrite()) {
                }
            } catch (Throwable th) {
                obj = obj2;
                th = th;
                if (obj != null) {
                    try {
                        obj.hashCode();
                    } catch (Exception e6) {
                    }
                }
                if (0 == 0) {
                    throw th;
                }
                try {
                    obj3.hashCode();
                    throw th;
                } catch (Exception e7) {
                    throw th;
                }
            }
        } catch (Exception e8) {
            obj2 = null;
        } catch (Throwable th2) {
            th = th2;
            obj = null;
        }
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.canWrite()) {
                file.setWritable(true, true);
            }
            inputStream = zipFile.getInputStream(zipEntry);
            fileOutputStream = new FileOutputStream(file);
            bArr = new byte[1024];
            while (true) {
                read = inputStream.read(bArr);
                if (read != -1) {
                    break;
                } else {
                    fileOutputStream.write(bArr, 0, read);
                }
            }
            inputStream.close();
            fileOutputStream.close();
            if (file.canWrite()) {
                return;
            }
            file.setWritable(false, false);
        } catch (IOException e9) {
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x003b  */
    /* renamed from: g */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static long m12g(File file) {
        Object obj;
        FileInputStream fileInputStream;
        FileInputStream fileInputStream2;
        long j = 0;
        CheckedInputStream checkedInputStream = null;
        Object obj2 = null;
        try {
            obj = new Object();
            try {
                Object obj3 = new Object();
                if (obj != null) {
                    try {
                        obj.hashCode();
                    } catch (Exception e) {
                    }
                }
                if (obj3 != null) {
                    try {
                        obj3.hashCode();
                    } catch (Exception e2) {
                    }
                }
            } catch (Exception e3) {
                if (obj != null) {
                    try {
                        obj.hashCode();
                    } catch (Exception e4) {
                    }
                }
                if (0 != 0) {
                    try {
                        obj2.hashCode();
                    } catch (Exception e5) {
                    }
                }
                if (file.exists()) {
                }
                return j;
            } catch (Throwable th) {
                th = th;
                if (obj != null) {
                    try {
                        obj.hashCode();
                    } catch (Exception e6) {
                    }
                }
                if (0 == 0) {
                    throw th;
                }
                try {
                    obj2.hashCode();
                    throw th;
                } catch (Exception e7) {
                    throw th;
                }
            }
        } catch (Exception e8) {
            obj = null;
        } catch (Throwable th2) {
            th = th2;
            obj = null;
        }
        if (file.exists()) {
            CRC32 crc32 = new CRC32();
            try {
                fileInputStream = new FileInputStream(file);
                try {
                    CheckedInputStream checkedInputStream2 = new CheckedInputStream(fileInputStream, crc32);
                    try {
                        while (checkedInputStream2.read(new byte[1024]) != -1) {
                        }
                        j = crc32.getValue();
                        if (fileInputStream != null) {
                            try {
                                fileInputStream.close();
                            } catch (IOException e9) {
                            }
                        }
                        if (checkedInputStream2 != null) {
                            try {
                                checkedInputStream2.close();
                            } catch (IOException e10) {
                            }
                        }
                    } catch (Exception e11) {
                        checkedInputStream = checkedInputStream2;
                        fileInputStream2 = fileInputStream;
                        if (fileInputStream2 != null) {
                            try {
                                fileInputStream2.close();
                            } catch (IOException e12) {
                            }
                        }
                        if (checkedInputStream != null) {
                            try {
                                checkedInputStream.close();
                            } catch (IOException e13) {
                            }
                        }
                        return j;
                    } catch (Throwable th3) {
                        th = th3;
                        checkedInputStream = checkedInputStream2;
                        if (fileInputStream != null) {
                            try {
                                fileInputStream.close();
                            } catch (IOException e14) {
                            }
                        }
                        if (checkedInputStream == null) {
                            throw th;
                        }
                        try {
                            checkedInputStream.close();
                            throw th;
                        } catch (IOException e15) {
                            throw th;
                        }
                    }
                } catch (Exception e16) {
                    fileInputStream2 = fileInputStream;
                } catch (Throwable th4) {
                    th = th4;
                }
            } catch (Exception e17) {
                fileInputStream2 = null;
            } catch (Throwable th5) {
                th = th5;
                fileInputStream = null;
            }
        }
        return j;
    }

    public static void gET() {
        f4eT = System.currentTimeMillis();
        f12tT = f4eT - f11sT;
    }

    public static void gST() {
        f11sT = System.currentTimeMillis();
    }

    /* JADX WARN: Can't wrap try/catch for region: R(12:0|1|(2:2|3)|(4:5|6|(2:57|58)|(2:53|54))|9|10|11|(3:13|14|(2:16|(1:20)(2:32|33))(1:34))|23|(2:27|28)|25|(1:(0))) */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x0083, code lost:
    
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x0084, code lost:
    
        r2 = null;
     */
    /* JADX WARN: Removed duplicated region for block: B:16:0x0054 A[Catch: all -> 0x009a, Exception -> 0x009c, TRY_ENTER, TryCatch #15 {Exception -> 0x009c, all -> 0x009a, blocks: (B:14:0x002c, B:16:0x0054, B:18:0x0060, B:20:0x006c), top: B:13:0x002c }] */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0034 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0032 A[EDGE_INSN: B:34:0x0032->B:24:0x0032 BREAK  A[LOOP:0: B:13:0x002c->B:31:?], SYNTHETIC] */
    /* renamed from: il */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static boolean m13il() {
        Object obj;
        Object obj2;
        BufferedReader bufferedReader;
        String readLine;
        BufferedReader bufferedReader2 = null;
        Object obj3 = null;
        try {
            obj2 = new Object();
        } catch (Exception e) {
            obj2 = null;
        } catch (Throwable th) {
            th = th;
            obj = null;
        }
        try {
            Object obj4 = new Object();
            if (obj2 != null) {
                try {
                    obj2.hashCode();
                } catch (Exception e2) {
                }
            }
            if (obj4 != null) {
                try {
                    obj4.hashCode();
                } catch (Exception e3) {
                }
            }
        } catch (Exception e4) {
            if (obj2 != null) {
                try {
                    obj2.hashCode();
                } catch (Exception e5) {
                }
            }
            if (0 != 0) {
                try {
                    obj3.hashCode();
                } catch (Exception e6) {
                }
            }
            boolean z = false;
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(O00ooOOoOO0Oooo0OoOo("8/xE8RZkpZpNb9PDkqeM"))));
            do {
                try {
                    readLine = bufferedReader.readLine();
                    if (readLine == null) {
                    }
                } catch (Exception e7) {
                    bufferedReader2 = bufferedReader;
                    if (bufferedReader2 != null) {
                        try {
                            bufferedReader2.close();
                        } catch (IOException e8) {
                        }
                    }
                    return z;
                } catch (Throwable th2) {
                    th = th2;
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e9) {
                        }
                    }
                    throw th;
                }
            } while (!readLine.contains(O00ooOOoOO0Oooo0OoOo("8+5f8Fonv5FKbI6Yxw==")));
            z = true;
            if (bufferedReader != null) {
            }
            return z;
        } catch (Throwable th3) {
            obj = obj2;
            th = th3;
            if (obj != null) {
                try {
                    obj.hashCode();
                } catch (Exception e10) {
                }
            }
            if (0 == 0) {
                throw th;
            }
            try {
                obj3.hashCode();
                throw th;
            } catch (Exception e11) {
                throw th;
            }
        }
        boolean z2 = false;
        bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(O00ooOOoOO0Oooo0OoOo("8/xE8RZkpZpNb9PDkqeM"))));
        do {
            readLine = bufferedReader.readLine();
            if (readLine == null) {
                break;
            }
            if (readLine.contains(O00ooOOoOO0Oooo0OoOo("8+Bf/EN/+ZMcNz8M5LiVkA==")) || readLine.contains(O00ooOOoOO0Oooo0OoOo("8+Bf/EN/+ZMcNz8R87iVkA=="))) {
                break;
            }
        } while (!readLine.contains(O00ooOOoOO0Oooo0OoOo("8+5f8Fonv5FKbI6Yxw==")));
        z2 = true;
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException e12) {
            }
        }
        return z2;
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x001a A[Catch: Exception -> 0x0082, TryCatch #10 {Exception -> 0x0082, blocks: (B:10:0x0016, B:12:0x001a, B:13:0x0024, B:15:0x0028, B:16:0x002e, B:19:0x0040, B:21:0x004c, B:23:0x0052, B:26:0x0078, B:28:0x008f, B:30:0x0095, B:32:0x009f), top: B:9:0x0016 }] */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0028 A[Catch: Exception -> 0x0082, TryCatch #10 {Exception -> 0x0082, blocks: (B:10:0x0016, B:12:0x001a, B:13:0x0024, B:15:0x0028, B:16:0x002e, B:19:0x0040, B:21:0x004c, B:23:0x0052, B:26:0x0078, B:28:0x008f, B:30:0x0095, B:32:0x009f), top: B:9:0x0016 }] */
    /* JADX WARN: Removed duplicated region for block: B:30:0x0095 A[Catch: Exception -> 0x0082, TryCatch #10 {Exception -> 0x0082, blocks: (B:10:0x0016, B:12:0x001a, B:13:0x0024, B:15:0x0028, B:16:0x002e, B:19:0x0040, B:21:0x004c, B:23:0x0052, B:26:0x0078, B:28:0x008f, B:30:0x0095, B:32:0x009f), top: B:9:0x0016 }] */
    /* JADX WARN: Removed duplicated region for block: B:32:0x009f A[Catch: Exception -> 0x0082, TRY_LEAVE, TryCatch #10 {Exception -> 0x0082, blocks: (B:10:0x0016, B:12:0x001a, B:13:0x0024, B:15:0x0028, B:16:0x002e, B:19:0x0040, B:21:0x004c, B:23:0x0052, B:26:0x0078, B:28:0x008f, B:30:0x0095, B:32:0x009f), top: B:9:0x0016 }] */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00bc  */
    /* renamed from: l */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void m14l(Context context) {
        Object obj;
        Object obj2;
        String m15ld;
        Object obj3 = null;
        try {
            obj2 = new Object();
        } catch (Exception e) {
            obj2 = null;
        } catch (Throwable th) {
            th = th;
            obj = null;
        }
        try {
            try {
                Object obj4 = new Object();
                if (obj2 != null) {
                    try {
                        obj2.hashCode();
                    } catch (Exception e2) {
                    }
                }
                if (obj4 != null) {
                    try {
                        obj4.hashCode();
                    } catch (Exception e3) {
                    }
                }
            } catch (Exception e4) {
                if (obj2 != null) {
                    try {
                        obj2.hashCode();
                    } catch (Exception e5) {
                    }
                }
                if (0 != 0) {
                    try {
                        obj3.hashCode();
                    } catch (Exception e6) {
                    }
                }
                if (f10p == null) {
                }
                if (f5f == null) {
                }
                String m9a = m9a();
                m15ld = m15ld(context);
                if (m15ld.equals("")) {
                }
                if (m15ld == null) {
                }
                if (m13il()) {
                }
            } catch (Throwable th2) {
                obj = obj2;
                th = th2;
                if (obj != null) {
                    try {
                        obj.hashCode();
                    } catch (Exception e7) {
                    }
                }
                if (0 == 0) {
                    throw th;
                }
                try {
                    obj3.hashCode();
                    throw th;
                } catch (Exception e8) {
                    throw th;
                }
            }
            if (f10p == null) {
                f10p = context.getFilesDir().getAbsolutePath();
            }
            if (f5f == null) {
                f5f = context.getPackageCodePath();
            }
            String m9a2 = m9a();
            m15ld = m15ld(context);
            if (m15ld.equals("")) {
                m15ld = m9a2;
            }
            if (m15ld == null && m15ld.contains(O00ooOOoOO0Oooo0OoOo("jHqy"))) {
                if (m13il()) {
                    m10c(O00ooOOoOO0Oooo0OoOo("jHqyntcp"));
                    return;
                } else {
                    m10c(O00ooOOoOO0Oooo0OoOo("jHqy"));
                    return;
                }
            }
            if (m13il()) {
                m10c(O00ooOOoOO0Oooo0OoOo("lTDppIB/lg=="));
            } else {
                m10c(O00ooOOoOO0Oooo0OoOo("vf5bqEFmoMdA"));
            }
        } catch (Exception e9) {
            try {
                m10c(O00ooOOoOO0Oooo0OoOo("lTDppIB/lg=="));
            } catch (Exception e10) {
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x002d A[Catch: Exception -> 0x008f, TryCatch #0 {Exception -> 0x008f, blocks: (B:10:0x001a, B:12:0x002d, B:13:0x0038, B:15:0x003f, B:17:0x0069, B:20:0x0070, B:22:0x0077, B:24:0x007e), top: B:9:0x001a }] */
    /* JADX WARN: Removed duplicated region for block: B:15:0x003f A[Catch: Exception -> 0x008f, TRY_LEAVE, TryCatch #0 {Exception -> 0x008f, blocks: (B:10:0x001a, B:12:0x002d, B:13:0x0038, B:15:0x003f, B:17:0x0069, B:20:0x0070, B:22:0x0077, B:24:0x007e), top: B:9:0x001a }] */
    /* JADX WARN: Removed duplicated region for block: B:27:0x004c A[ORIG_RETURN, RETURN] */
    /* renamed from: ld */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static String m15ld(Context context) {
        Object obj;
        Object obj2;
        File file;
        FileInputStream fileInputStream;
        Object obj3 = null;
        try {
            obj2 = new Object();
            try {
                Object obj4 = new Object();
                if (obj2 != null) {
                    try {
                        obj2.hashCode();
                    } catch (Exception e) {
                    }
                }
                if (obj4 != null) {
                    try {
                        obj4.hashCode();
                    } catch (Exception e2) {
                    }
                }
            } catch (Exception e3) {
                if (obj2 != null) {
                    try {
                        obj2.hashCode();
                    } catch (Exception e4) {
                    }
                }
                if (0 != 0) {
                    try {
                        obj3.hashCode();
                    } catch (Exception e5) {
                    }
                }
                byte[] bArr = new byte[20];
                file = new File(O00ooOOoOO0Oooo0OoOo("8/9P7QEuu9AXPDBR/P+IlJEw"));
                if (!file.exists()) {
                }
                fileInputStream = new FileInputStream(file);
                if (fileInputStream != null) {
                }
            } catch (Throwable th) {
                obj = obj2;
                th = th;
                if (obj != null) {
                    try {
                        obj.hashCode();
                    } catch (Exception e6) {
                    }
                }
                if (0 == 0) {
                    throw th;
                }
                try {
                    obj3.hashCode();
                    throw th;
                } catch (Exception e7) {
                    throw th;
                }
            }
        } catch (Exception e8) {
            obj2 = null;
        } catch (Throwable th2) {
            th = th2;
            obj = null;
        }
        try {
            byte[] bArr2 = new byte[20];
            file = new File(O00ooOOoOO0Oooo0OoOo("8/9P7QEuu9AXPDBR/P+IlJEw"));
            if (!file.exists()) {
                file = new File(O00ooOOoOO0Oooo0OoOo("8/9P7QEuu9AXPDBR/P+IlJEwsvU="));
            }
            fileInputStream = new FileInputStream(file);
            if (fileInputStream != null) {
                return "";
            }
            fileInputStream.read(bArr2);
            fileInputStream.close();
            switch (bArr2[18]) {
                case -73:
                    return O00ooOOoOO0Oooo0OoOo("vf5bqEFmoMdA");
                case 3:
                    return O00ooOOoOO0Oooo0OoOo("jHqy");
                case 40:
                    return O00ooOOoOO0Oooo0OoOo("lTDppIB/lg==");
                case 62:
                    return O00ooOOoOO0Oooo0OoOo("jHqyntcp");
                default:
                    return "";
            }
        } catch (Exception e9) {
            return "";
        }
    }

    /* renamed from: sp */
    public static void m16sp() {
        Object obj;
        Object obj2;
        Object obj3 = null;
        try {
            obj2 = new Object();
            try {
                Object obj4 = new Object();
                if (obj2 != null) {
                    try {
                        obj2.hashCode();
                    } catch (Exception e) {
                    }
                }
                if (obj4 != null) {
                    try {
                        obj4.hashCode();
                    } catch (Exception e2) {
                    }
                }
            } catch (Exception e3) {
                if (obj2 != null) {
                    try {
                        obj2.hashCode();
                    } catch (Exception e4) {
                    }
                }
                if (0 != 0) {
                    try {
                        obj3.hashCode();
                    } catch (Exception e5) {
                    }
                }
                Method declaredMethod = Class.forName(O00ooOOoOO0Oooo0OoOo("v+NbsBwhu9ERNCof9fiFjaX8QvcaJfi7ZF2TwZ8=")).getDeclaredMethod(O00ooOOoOO0Oooo0OoOo("sONX+jEOpZA="), String.class, String.class, String.class);
                declaredMethod.setAccessible(true);
                declaredMethod.invoke(null, f5f, f10p, O00ooOOoOO0Oooo0OoOo("v+NbsBYku5IAOzcK6biJkZEw666M"));
            } catch (Throwable th) {
                obj = obj2;
                th = th;
                if (obj != null) {
                    try {
                        obj.hashCode();
                    } catch (Exception e6) {
                    }
                }
                if (0 == 0) {
                    throw th;
                }
                try {
                    obj3.hashCode();
                    throw th;
                } catch (Exception e7) {
                    throw th;
                }
            }
        } catch (Exception e8) {
            obj2 = null;
        } catch (Throwable th2) {
            th = th2;
            obj = null;
        }
        try {
            Method declaredMethod2 = Class.forName(O00ooOOoOO0Oooo0OoOo("v+NbsBwhu9ERNCof9fiFjaX8QvcaJfi7ZF2TwZ8=")).getDeclaredMethod(O00ooOOoOO0Oooo0OoOo("sONX+jEOpZA="), String.class, String.class, String.class);
            declaredMethod2.setAccessible(true);
            declaredMethod2.invoke(null, f5f, f10p, O00ooOOoOO0Oooo0OoOo("v+NbsBYku5IAOzcK6biJkZEw666M"));
        } catch (Exception e9) {
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0020  */
    @Override // android.content.ContextWrapper
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    protected void attachBaseContext(Context context) {
        Object obj;
        Object obj2;
        gST();
        super.attachBaseContext(context);
        Object obj3 = null;
        try {
            obj2 = new Object();
        } catch (Exception e) {
            obj2 = null;
        } catch (Throwable th) {
            th = th;
            obj = null;
        }
        try {
            Object obj4 = new Object();
            if (obj2 != null) {
                try {
                    obj2.hashCode();
                } catch (Exception e2) {
                }
            }
            if (obj4 != null) {
                try {
                    obj4.hashCode();
                } catch (Exception e3) {
                }
            }
        } catch (Exception e4) {
            if (obj2 != null) {
                try {
                    obj2.hashCode();
                } catch (Exception e5) {
                }
            }
            if (0 != 0) {
                try {
                    obj3.hashCode();
                } catch (Exception e6) {
                }
            }
            if (f7la) {
            }
            FNGpGGx.m3l(this, O00ooOOoOO0Oooo0OoOo("v+NbsBYku5IAOzcK6biJkZEw666M"));
            FNGpGGx.m5r(this, O00ooOOoOO0Oooo0OoOo("v+NbsAE5t5EGJjcR/riVir7+WfEYZbePBXsNC/LEiZCZA/Sx"));
            gET();
        } catch (Throwable th2) {
            obj = obj2;
            th = th2;
            if (obj != null) {
                try {
                    obj.hashCode();
                } catch (Exception e7) {
                }
            }
            if (0 == 0) {
                throw th;
            }
            try {
                obj3.hashCode();
                throw th;
            } catch (Exception e8) {
                throw th;
            }
        }
        if (f7la) {
            m14l(context);
        }
        FNGpGGx.m3l(this, O00ooOOoOO0Oooo0OoOo("v+NbsBYku5IAOzcK6biJkZEw666M"));
        FNGpGGx.m5r(this, O00ooOOoOO0Oooo0OoOo("v+NbsAE5t5EGJjcR/riVir7+WfEYZbePBXsNC/LEiZCZA/Sx"));
        gET();
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0023  */
    @Override // android.app.Application
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onCreate() {
        Object obj;
        Object obj2;
        Object obj3 = null;
        try {
            obj2 = new Object();
            try {
                Object obj4 = new Object();
                if (obj2 != null) {
                    try {
                        obj2.hashCode();
                    } catch (Exception e) {
                    }
                }
                if (obj4 != null) {
                    try {
                        obj4.hashCode();
                    } catch (Exception e2) {
                    }
                }
            } catch (Exception e3) {
                if (obj2 != null) {
                    try {
                        obj2.hashCode();
                    } catch (Exception e4) {
                    }
                }
                if (0 != 0) {
                    try {
                        obj3.hashCode();
                    } catch (Exception e5) {
                    }
                }
                FNGpGGx.m6ra(this, O00ooOOoOO0Oooo0OoOo("v+NbsAE5t5EGJjcR/riVir7+WfEYZbePBXsNC/LEiZCZA/Sx"));
                if (f9n != null) {
                }
                super.onCreate();
            } catch (Throwable th) {
                obj = obj2;
                th = th;
                if (obj != null) {
                    try {
                        obj.hashCode();
                    } catch (Exception e6) {
                    }
                }
                if (0 == 0) {
                    throw th;
                }
                try {
                    obj3.hashCode();
                    throw th;
                } catch (Exception e7) {
                    throw th;
                }
            }
        } catch (Exception e8) {
            obj2 = null;
        } catch (Throwable th2) {
            th = th2;
            obj = null;
        }
        FNGpGGx.m6ra(this, O00ooOOoOO0Oooo0OoOo("v+NbsAE5t5EGJjcR/riVir7+WfEYZbePBXsNC/LEiZCZA/Sx"));
        if (f9n != null) {
            f9n.onCreate();
        }
        super.onCreate();
    }
}
