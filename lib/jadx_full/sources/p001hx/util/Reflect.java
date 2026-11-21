package p001hx.util;

/* loaded from: classes3.dex */
public final class Reflect {
    private Reflect() {
    }

    public static Object call(Object obj, String str) throws ReflectiveOperationException {
        return obj.getClass().getMethod(str, new Class[0]).invoke(obj, new Object[0]);
    }

    public static Object call(Object obj, String str, Class<?>[] clsArr, Object... objArr) throws ReflectiveOperationException {
        return obj.getClass().getMethod(str, clsArr).invoke(obj, objArr);
    }
}
