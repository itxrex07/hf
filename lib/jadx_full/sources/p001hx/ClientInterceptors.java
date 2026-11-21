package p001hx;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import p001hx.util.Reflect;

/* loaded from: classes3.dex */
final class ClientInterceptors {
    private static final String INTERCEPTOR = "okhttp3.Interceptor";

    private ClientInterceptors() {
    }

    static Object proxy(ClassLoader classLoader, InvocationHandler invocationHandler) throws ClassNotFoundException {
        return Proxy.newProxyInstance(classLoader, new Class[]{Class.forName(INTERCEPTOR, false, classLoader)}, invocationHandler);
    }

    static Object addFirst(Object obj, Object obj2) throws ReflectiveOperationException {
        Object call = Reflect.call(obj, "newBuilder");
        insert(Reflect.call(call, "interceptors"), 0, obj2);
        return Reflect.call(call, "build");
    }

    static Object addBeforeTerminal(Object obj, Object obj2) throws ReflectiveOperationException {
        Object call = Reflect.call(obj, "newBuilder");
        insert(Reflect.call(call, "interceptors"), Math.max(((Integer) Reflect.call(r0, "size")).intValue() - 1, 0), obj2);
        return Reflect.call(call, "build");
    }

    private static void insert(Object obj, int i, Object obj2) throws ReflectiveOperationException {
        Reflect.call(obj, "add", new Class[]{Integer.TYPE, Object.class}, Integer.valueOf(i), obj2);
    }
}
