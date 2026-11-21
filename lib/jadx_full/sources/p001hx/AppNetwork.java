package p001hx;

import java.lang.reflect.Field;
import p001hx.util.Reflect;

/* loaded from: classes3.dex */
final class AppNetwork {
    private static final String OKHTTP_CLIENT = "okhttp3.OkHttpClient";
    private static final String OKHTTP_URL = "okhttp3.HttpUrl";
    private static final String RETROFIT_PACKAGE = "retrofit2.";
    final String apiBase;
    private final Field callFactoryField;
    final Object client;
    private final Field clientField;
    private final Object instance;
    final ClassLoader loader;
    private final Object retrofit;

    private AppNetwork(ClassLoader classLoader, Object obj, Field field, Object obj2, Field field2, String str) throws IllegalAccessException {
        this.loader = classLoader;
        this.instance = obj;
        this.clientField = field;
        this.client = field.get(obj);
        this.retrofit = obj2;
        this.callFactoryField = field2;
        this.apiBase = str;
    }

    static AppNetwork locate(Class<?> cls) throws ReflectiveOperationException {
        Object singleton = singleton(cls);
        if (singleton == null) {
            throw new IllegalStateException("no network manager singleton");
        }
        ClassLoader classLoader = cls.getClassLoader();
        Class<?> cls2 = Class.forName(OKHTTP_CLIENT, false, classLoader);
        Field holderOf = holderOf(cls, cls2);
        if (holderOf == null) {
            throw new IllegalStateException("no client field on network manager");
        }
        if (!cls2.isInstance(holderOf.get(singleton))) {
            throw new IllegalStateException("network manager field is not an OkHttpClient");
        }
        Field fieldInPackage = fieldInPackage(cls, RETROFIT_PACKAGE);
        Object obj = fieldInPackage != null ? fieldInPackage.get(singleton) : null;
        if (obj == null) {
            throw new IllegalStateException("no Retrofit instance on network manager");
        }
        Field holderOf2 = holderOf(obj.getClass(), cls2);
        if (holderOf2 == null) {
            throw new IllegalStateException("no call factory field on Retrofit");
        }
        Field fieldInPackage2 = fieldInPackage(obj.getClass(), OKHTTP_URL);
        if (fieldInPackage2 == null) {
            throw new IllegalStateException("no base URL field on Retrofit");
        }
        return new AppNetwork(classLoader, singleton, holderOf, obj, holderOf2, String.valueOf(fieldInPackage2.get(obj)));
    }

    void useClient(Object obj) throws IllegalAccessException {
        this.clientField.set(this.instance, obj);
        try {
            this.callFactoryField.set(this.retrofit, obj);
        } catch (Exception e) {
            this.clientField.set(this.instance, this.client);
            throw e;
        }
    }

    private static Object singleton(Class<?> cls) throws ReflectiveOperationException {
        for (Field field : cls.getDeclaredFields()) {
            if ("kotlin.Lazy".equals(field.getType().getName())) {
                field.setAccessible(true);
                Object obj = field.get(null);
                if (obj != null) {
                    return Reflect.call(obj, "getValue");
                }
                return null;
            }
        }
        return null;
    }

    private static Field holderOf(Class<?> cls, Class<?> cls2) {
        while (cls != null && cls != Object.class) {
            for (Field field : cls.getDeclaredFields()) {
                if (field.getType() != Object.class && field.getType().isAssignableFrom(cls2)) {
                    field.setAccessible(true);
                    return field;
                }
            }
            cls = cls.getSuperclass();
        }
        return null;
    }

    private static Field fieldInPackage(Class<?> cls, String str) {
        while (cls != null && cls != Object.class) {
            for (Field field : cls.getDeclaredFields()) {
                if (field.getType().getName().startsWith(str)) {
                    field.setAccessible(true);
                    return field;
                }
            }
            cls = cls.getSuperclass();
        }
        return null;
    }
}
