package p001hx.okhttp;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import p001hx.util.Reflect;

/* loaded from: classes3.dex */
public final class ApiClient {
    private static final String REQUEST_BUILDER = "okhttp3.Request$Builder";
    private final String base;
    private final Object client;
    private final ClassLoader loader;

    public ApiClient(ClassLoader classLoader, Object obj, String str) {
        this.loader = classLoader;
        this.client = obj;
        this.base = str.endsWith("/") ? str : str + "/";
    }

    public String url(String str) {
        return this.base + str;
    }

    public String get(String str) throws IOException {
        try {
            Object execute = execute(str);
            try {
                return successfulBody(execute, str);
            } finally {
                Reflect.call(execute, "close");
            }
        } catch (InvocationTargetException e) {
            throw new IOException(e.getCause());
        } catch (ReflectiveOperationException e2) {
            throw new IOException(e2);
        }
    }

    private Object execute(String str) throws ReflectiveOperationException {
        Object newInstance = Class.forName(REQUEST_BUILDER, false, this.loader).getConstructor(new Class[0]).newInstance(new Object[0]);
        Reflect.call(newInstance, "url", new Class[]{String.class}, str);
        Object call = Reflect.call(newInstance, "build");
        return Reflect.call(Reflect.call(this.client, "newCall", new Class[]{call.getClass()}, call), "execute");
    }

    private static String successfulBody(Object obj, String str) throws ReflectiveOperationException, IOException {
        int intValue = ((Integer) Reflect.call(obj, "code")).intValue();
        if (intValue < 200 || intValue >= 300) {
            throw new IOException("HTTP " + intValue + " for " + str);
        }
        return (String) Reflect.call(Reflect.call(obj, "body"), "string");
    }
}
