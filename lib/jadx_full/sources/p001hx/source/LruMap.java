package p001hx.source;

import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: classes3.dex */
final class LruMap<V> extends LinkedHashMap<String, V> {
    private static final float LOAD_FACTOR = 0.75f;
    private static final long serialVersionUID = 1;
    private final int capacity;

    LruMap(int i) {
        super(i, LOAD_FACTOR, true);
        this.capacity = i;
    }

    @Override // java.util.LinkedHashMap
    protected boolean removeEldestEntry(Map.Entry<String, V> entry) {
        return size() > this.capacity;
    }
}
