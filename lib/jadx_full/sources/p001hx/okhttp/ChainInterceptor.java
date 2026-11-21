package p001hx.okhttp;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import p001hx.util.Reflect;

/* loaded from: classes3.dex */
public abstract class ChainInterceptor implements InvocationHandler {
    private final String description;
    private final String failureMessage;

    protected abstract Object intercept(Object obj) throws Throwable;

    public ChainInterceptor(String str, String str2) {
        this.description = str;
        this.failureMessage = str2;
    }

    @Override // java.lang.reflect.InvocationHandler
    public final Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
        String name = method.getName();
        boolean z = false;
        int length = objArr != null ? objArr.length : 0;
        if (length == 0 && "toString".equals(name)) {
            return this.description;
        }
        if (length == 0 && "hashCode".equals(name)) {
            return Integer.valueOf(System.identityHashCode(obj));
        }
        if (length == 1 && "equals".equals(name)) {
            if (obj == objArr[0]) {
                z = true;
            }
            return Boolean.valueOf(z);
        }
        if (length != 1) {
            return null;
        }
        return interceptAsIoFailure(objArr[0]);
    }

    protected static Object request(Object obj) throws ReflectiveOperationException {
        return Reflect.call(obj, "request");
    }

    protected static Object proceed(Object obj, Object obj2) throws ReflectiveOperationException {
        return Reflect.call(obj, "proceed", new Class[]{obj2.getClass()}, obj2);
    }

    private Object interceptAsIoFailure(Object obj) throws IOException {
        try {
            return intercept(obj);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            Throwable th = e;
            if (cause != null) {
                th = e.getCause();
            }
            throw asIoException(th);
        } catch (Throwable th2) {
            throw asIoException(th2);
        }
    }

    private IOException asIoException(Throwable th) {
        return th instanceof IOException ? (IOException) th : new IOException(this.failureMessage, th);
    }
}
