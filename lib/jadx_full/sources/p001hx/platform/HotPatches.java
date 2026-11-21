package p001hx.platform;

import android.content.Context;
import java.io.File;

/* loaded from: classes3.dex */
public final class HotPatches {
    private static final String PATCH_DIRECTORY = "tinker";

    private HotPatches() {
    }

    public static boolean removeApplied(Context context) {
        File file = new File(context.getApplicationInfo().dataDir, PATCH_DIRECTORY);
        return file.exists() && delete(file);
    }

    private static boolean delete(File file) {
        boolean z;
        File[] listFiles = file.listFiles();
        if (listFiles == null) {
            z = true;
        } else {
            z = true;
            for (File file2 : listFiles) {
                z &= delete(file2);
            }
        }
        return file.delete() && z;
    }
}
