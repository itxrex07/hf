package p001hx.gateway;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

/* loaded from: classes3.dex */
public final class SharedHeaders {
    private SharedHeaders() {
    }

    /* renamed from: of */
    public static Map<String, String> m0of(Class<?> cls) throws ReflectiveOperationException {
        for (Field field : cls.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && Map.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                Object obj = field.get(null);
                if (obj != null) {
                    return (Map) obj;
                }
            }
        }
        throw new NoSuchFieldException("no static header map on " + cls.getName());
    }
}
