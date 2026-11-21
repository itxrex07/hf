package p001hx;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/* loaded from: classes2.dex */
final class Boot {
    private static final String APP_HOOK_ASSET = "assets/hx.hook";
    static final String APP_HOOK_FAILED = "Patch hook failed";
    private static final String ARCH_WARNING = "This device's architecture is not supported by the patch";
    private static final String ARM32_ABI = "armeabi-v7a";
    private static final String ARM64_ABI = "arm64-v8a";
    private static final String CERTIFICATE_ASSET = "assets/hx.cer";
    static final String CODE_OVERLAY_FAILED = "Patch cannot write its code overlay";
    private static final String FACTORY_ASSET = "assets/hx.factory";
    private static final String NATIVE_LIBRARY = "libhxpatch.so";
    private static final String OVERLAY_CLASSES_ASSET = "assets/hx.classes";
    private static final String OVERLAY_CODE_ASSET = "assets/hx.code";
    private static final String STRIP_WARNING = "Disable \"Optimize for device architecture\" & re-patch";
    private static final String TAG = "hxreborn/ijiami";
    private static final int TOAST_ATTEMPTS = 30;
    private static final long TOAST_RETRY_MILLIS = 1000;
    private static boolean appHookStarted;
    private static boolean assetsRead;
    private static boolean installed;
    private static String packerFactory = "";
    private static String appHookClass = "";
    private static byte[] certificate = new byte[0];

    private static native int writeCode(byte[] bArr);

    private Boot() {
    }

    static synchronized void install(ApplicationInfo applicationInfo) {
        synchronized (Boot.class) {
            if (installed) {
                return;
            }
            installed = true;
            readAssets(applicationInfo);
            try {
                warnIfArchitectureStripped(applicationInfo);
            } catch (Throwable th) {
                Log.e(TAG, "cannot verify native library architectures", th);
            }
            try {
                loadNativeLibrary(applicationInfo);
            } catch (Throwable th2) {
                Log.e(TAG, "cannot load libhxpatch.so", th2);
            }
            try {
                spoofSignature(applicationInfo.packageName);
            } catch (Throwable th3) {
                Log.e(TAG, "cannot spoof signing certificate", th3);
            }
            try {
                spoofApplicationInfo(applicationInfo.packageName);
            } catch (Throwable th4) {
                Log.e(TAG, "cannot spoof appComponentFactory", th4);
            }
        }
    }

    static synchronized String packerFactory(ApplicationInfo applicationInfo) {
        String str;
        synchronized (Boot.class) {
            readAssets(applicationInfo);
            str = packerFactory;
        }
        return str;
    }

    static void writeCodeOverlay(ClassLoader classLoader, ApplicationInfo applicationInfo) {
        try {
            ZipFile zipFile = new ZipFile(applicationInfo.sourceDir);
            try {
                byte[] read = read(zipFile, OVERLAY_CODE_ASSET);
                String[] split = new String(read(zipFile, OVERLAY_CLASSES_ASSET), StandardCharsets.UTF_8).split("\n");
                zipFile.close();
                if (read.length == 0) {
                    return;
                }
                int i = 0;
                for (String str : split) {
                    if (!str.isEmpty()) {
                        try {
                            Class.forName(str, false, classLoader);
                            i++;
                        } catch (Throwable th) {
                            Log.e(TAG, "cannot load " + str + " before the code overlay", th);
                        }
                    }
                }
                try {
                    Log.i(TAG, "code overlay: loaded " + i + " classes, wrote " + writeCode(read) + " code items");
                } catch (Throwable th2) {
                    Log.e(TAG, "cannot write code overlay", th2);
                    toast(failureNotice(CODE_OVERLAY_FAILED, th2));
                }
            } finally {
            }
        } catch (Throwable th3) {
            Log.e(TAG, "cannot read code overlay from " + applicationInfo.sourceDir, th3);
        }
    }

    static void runAppHook(final ClassLoader classLoader) {
        synchronized (Boot.class) {
            if (!appHookStarted && !appHookClass.isEmpty()) {
                appHookStarted = true;
                final String str = appHookClass;
                Thread thread = new Thread(new Runnable() { // from class: hx.Boot.1
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            Class.forName(str, true, Boot.class.getClassLoader()).getMethod("install", ClassLoader.class).invoke(null, classLoader);
                        } catch (Throwable th) {
                            Log.e(Boot.TAG, "app hook failed", th);
                            Boot.toast(Boot.failureNotice(Boot.APP_HOOK_FAILED, th));
                        }
                    }
                }, "hxreborn-apphook");
                thread.setDaemon(true);
                thread.start();
            }
        }
    }

    private static synchronized void readAssets(ApplicationInfo applicationInfo) {
        synchronized (Boot.class) {
            if (assetsRead) {
                return;
            }
            try {
                ZipFile zipFile = new ZipFile(applicationInfo.sourceDir);
                try {
                    certificate = read(zipFile, CERTIFICATE_ASSET);
                    packerFactory = new String(read(zipFile, FACTORY_ASSET), StandardCharsets.UTF_8).trim();
                    appHookClass = new String(read(zipFile, APP_HOOK_ASSET), StandardCharsets.UTF_8).trim();
                    assetsRead = certificate.length > 0 && !packerFactory.isEmpty();
                    if (!assetsRead) {
                        Log.e(TAG, "missing patch assets in " + applicationInfo.sourceDir);
                    }
                    zipFile.close();
                } finally {
                }
            } catch (Throwable th) {
                Log.e(TAG, "cannot read patch assets from " + applicationInfo.sourceDir, th);
            }
        }
    }

    static void restoreFactoryName(ApplicationInfo applicationInfo) {
        String packerFactory2 = packerFactory(applicationInfo);
        if (packerFactory2.isEmpty()) {
            return;
        }
        applicationInfo.appComponentFactory = packerFactory2;
        try {
            Class<?> cls = Class.forName("android.app.ActivityThread");
            Object invoke = cls.getMethod("currentActivityThread", new Class[0]).invoke(null, new Object[0]);
            Field declaredField = cls.getDeclaredField("mBoundApplication");
            declaredField.setAccessible(true);
            Object obj = declaredField.get(invoke);
            Field declaredField2 = obj.getClass().getDeclaredField("info");
            declaredField2.setAccessible(true);
            Object obj2 = declaredField2.get(obj);
            Field declaredField3 = obj2.getClass().getDeclaredField("mApplicationInfo");
            declaredField3.setAccessible(true);
            ApplicationInfo applicationInfo2 = (ApplicationInfo) declaredField3.get(obj2);
            if (applicationInfo2 != null) {
                applicationInfo2.appComponentFactory = packerFactory2;
            }
        } catch (Throwable th) {
            Log.e(TAG, "cannot restore bound ApplicationInfo.appComponentFactory", th);
        }
    }

    private static void warnIfArchitectureStripped(ApplicationInfo applicationInfo) throws Exception {
        int indexOf;
        HashSet hashSet = new HashSet();
        ZipFile zipFile = new ZipFile(applicationInfo.sourceDir);
        try {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith("lib/") && (indexOf = name.indexOf(47, 4)) > 4) {
                    hashSet.add(name.substring(4, indexOf));
                }
            }
            zipFile.close();
            if (!hashSet.contains(ARM64_ABI) || hashSet.contains(ARM32_ABI)) {
                return;
            }
            Log.e(TAG, "armeabi-v7a native libraries absent from lib/; present " + hashSet);
            toast(STRIP_WARNING);
        } catch (Throwable th) {
            try {
                zipFile.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    static String failureNotice(String str, Throwable th) {
        if ((th instanceof InvocationTargetException) && th.getCause() != null) {
            th = th.getCause();
        }
        String message = th.getMessage();
        return str + ": " + th.getClass().getSimpleName() + (message == null ? "" : ": " + message);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void toast(String str) {
        toast(str, 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void toast(final String str, final int i) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: hx.Boot.2
            @Override // java.lang.Runnable
            public void run() {
                try {
                    Object invoke = Class.forName("android.app.ActivityThread").getMethod("currentApplication", new Class[0]).invoke(null, new Object[0]);
                    if (invoke != null) {
                        Toast.makeText((Context) invoke, str, 1).show();
                    } else if (i < Boot.TOAST_ATTEMPTS) {
                        Boot.toast(str, i + 1);
                    } else {
                        Log.e(Boot.TAG, "no application context to show \"" + str + "\"");
                    }
                } catch (Throwable th) {
                    Log.e(Boot.TAG, "cannot show \"" + str + "\"", th);
                }
            }
        }, i == 1 ? 0L : TOAST_RETRY_MILLIS);
    }

    private static void loadNativeLibrary(ApplicationInfo applicationInfo) throws Exception {
        String str = "lib/" + (Process.is64Bit() ? ARM64_ABI : ARM32_ABI) + "/" + NATIVE_LIBRARY;
        try {
            System.load(applicationInfo.nativeLibraryDir + File.separator + NATIVE_LIBRARY);
        } catch (Throwable th) {
            File file = new File(applicationInfo.dataDir + File.separator + "files", NATIVE_LIBRARY);
            ZipFile zipFile = new ZipFile(applicationInfo.sourceDir);
            try {
                ZipEntry entry = zipFile.getEntry(str);
                if (entry == null) {
                    toast(ARCH_WARNING);
                    throw new IllegalStateException("missing APK entry: " + str);
                }
                if (!matches(file, entry)) {
                    File parentFile = file.getParentFile();
                    if (parentFile != null && !parentFile.isDirectory() && !parentFile.mkdirs()) {
                        throw new IllegalStateException("cannot create " + parentFile);
                    }
                    File file2 = new File(file.getPath() + "." + Process.myPid() + ".partial");
                    InputStream inputStream = zipFile.getInputStream(entry);
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(file2);
                        try {
                            copy(inputStream, fileOutputStream);
                            fileOutputStream.close();
                            if (inputStream != null) {
                                inputStream.close();
                            }
                            if (!file2.renameTo(file)) {
                                file2.delete();
                                throw new IllegalStateException("cannot rename " + file2 + " to " + file);
                            }
                        } finally {
                        }
                    } finally {
                    }
                }
                zipFile.close();
                System.load(file.getPath());
            } catch (Throwable th2) {
                try {
                    zipFile.close();
                } catch (Throwable th3) {
                    th2.addSuppressed(th3);
                }
                throw th2;
            }
        }
    }

    private static boolean matches(File file, ZipEntry zipEntry) {
        if (file.length() != zipEntry.getSize()) {
            return false;
        }
        CRC32 crc32 = new CRC32();
        byte[] bArr = new byte[8192];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            while (true) {
                try {
                    int read = fileInputStream.read(bArr);
                    if (read == -1) {
                        break;
                    }
                    crc32.update(bArr, 0, read);
                } finally {
                }
            }
            fileInputStream.close();
            return crc32.getValue() == zipEntry.getCrc();
        } catch (Throwable th) {
            return false;
        }
    }

    private static byte[] read(ZipFile zipFile, String str) throws Exception {
        ZipEntry entry = zipFile.getEntry(str);
        if (entry == null) {
            return new byte[0];
        }
        InputStream inputStream = zipFile.getInputStream(entry);
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            copy(inputStream, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            if (inputStream != null) {
                inputStream.close();
            }
            return byteArray;
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    private static void copy(InputStream inputStream, OutputStream outputStream) throws Exception {
        byte[] bArr = new byte[8192];
        while (true) {
            int read = inputStream.read(bArr);
            if (read == -1) {
                return;
            } else {
                outputStream.write(bArr, 0, read);
            }
        }
    }

    private static void spoofSignature(final String str) throws Exception {
        if (certificate.length == 0) {
            return;
        }
        final Signature signature = new Signature(certificate);
        final Parcelable.Creator creator = PackageInfo.CREATOR;
        replaceCreator(PackageInfo.class, new Parcelable.Creator<PackageInfo>() { // from class: hx.Boot.3
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public PackageInfo createFromParcel(Parcel parcel) {
                Signature[] apkContentsSigners;
                PackageInfo packageInfo = (PackageInfo) creator.createFromParcel(parcel);
                if (!str.equals(packageInfo.packageName)) {
                    return packageInfo;
                }
                if (packageInfo.signatures != null && packageInfo.signatures.length > 0) {
                    packageInfo.signatures[0] = signature;
                }
                if (packageInfo.signingInfo != null && (apkContentsSigners = packageInfo.signingInfo.getApkContentsSigners()) != null && apkContentsSigners.length > 0) {
                    apkContentsSigners[0] = signature;
                }
                return packageInfo;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public PackageInfo[] newArray(int i) {
                return (PackageInfo[]) creator.newArray(i);
            }
        });
    }

    private static void spoofApplicationInfo(final String str) throws Exception {
        final String str2 = packerFactory;
        if (str2.isEmpty()) {
            return;
        }
        final Parcelable.Creator creator = ApplicationInfo.CREATOR;
        replaceCreator(ApplicationInfo.class, new Parcelable.Creator<ApplicationInfo>() { // from class: hx.Boot.4
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ApplicationInfo createFromParcel(Parcel parcel) {
                ApplicationInfo applicationInfo = (ApplicationInfo) creator.createFromParcel(parcel);
                if (str.equals(applicationInfo.packageName)) {
                    applicationInfo.appComponentFactory = str2;
                }
                return applicationInfo;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ApplicationInfo[] newArray(int i) {
                return (ApplicationInfo[]) creator.newArray(i);
            }
        });
    }

    private static void replaceCreator(Class<?> cls, Object obj) throws Exception {
        Field declaredField = cls.getDeclaredField("CREATOR");
        declaredField.setAccessible(true);
        declaredField.set(null, obj);
    }
}
