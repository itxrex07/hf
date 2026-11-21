package uvj.ucxx.p002mb;

import android.util.Base64;
import java.io.UnsupportedEncodingException;
import p001hx.mp4.Mp4Boxes;

/* compiled from: C */
/* loaded from: classes.dex */
public class BJAvhDvBU {
    /* renamed from: i */
    public static native void m1i(int i);

    /* JADX WARN: Removed duplicated region for block: B:16:0x0071  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static String oOoO00oO0OO00OO0O(String str) {
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
                    bArr22[0] = new byte[]{-122, -66, -116, -49, 16, 114, 105, 116, 64, 1, -39, -83, 0, 17, 110, 91};
                    i = 2;
                    i42 = i8;
                    break;
                case 2:
                    bArr22[1] = new byte[]{-42, -27, 91, 118, 92, -23, -5, 36, -127, -93, 7, 0, 117, 62, 45, 105};
                    i = 3;
                    i42 = i8;
                    break;
                case 3:
                    bArr22[2] = new byte[]{64, -51, -20, 17, 117, Byte.MAX_VALUE, -41, -69, -113, 9, 31, 115, -17, 71, -107, 0};
                    i = 4;
                    i42 = i8;
                    break;
                case Mp4Boxes.U32_BYTES /* 4 */:
                    bArr22[3] = new byte[]{63, -63, -97, -21, -38, -117, 28, 60, 42, 59, -103, 88, -73, -126, -70, 30};
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
}
