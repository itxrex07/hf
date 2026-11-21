package p001hx;

import android.content.SharedPreferences;
import android.util.Log;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import p001hx.util.Logs;

/* loaded from: classes3.dex */
final class Laboratory {
    private static final String MMKV_APP_ID = "kv_app";
    private static final String MMKV_CLASS = "com.tencent.mmkv.MMKV";
    private static final String PASSWORD_TIME_KEY = "lab_enter_password_time";

    private Laboratory() {
    }

    static void unlock(ClassLoader classLoader) {
        try {
            Method findFactory = findFactory(Class.forName(MMKV_CLASS, false, classLoader));
            if (findFactory == null) {
                throw new IllegalStateException("no mmkvWithID(String) on com.tencent.mmkv.MMKV");
            }
            ((SharedPreferences.Editor) findFactory.invoke(null, MMKV_APP_ID)).putLong(PASSWORD_TIME_KEY, Long.MAX_VALUE);
            Log.i(Logs.TAG, "Laboratory unlocked");
        } catch (Exception e) {
            Log.e(Logs.TAG, "cannot unlock Laboratory", e);
        }
    }

    private static Method findFactory(Class<?> cls) {
        for (Method method : cls.getDeclaredMethods()) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (Modifier.isStatic(method.getModifiers()) && method.getReturnType() == cls && parameterTypes.length == 1 && parameterTypes[0] == String.class) {
                return method;
            }
        }
        return null;
    }
}
