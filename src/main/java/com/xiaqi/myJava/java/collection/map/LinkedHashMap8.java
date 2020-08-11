package com.xiaqi.myJava.java.collection.map;

import java.util.HashMap;
import java.util.Map;

public class LinkedHashMap8<K, V> extends HashMap<K, V> implements Map<K, V> {
    private static final long serialVersionUID = 1009146497908050594L;
    
    final boolean accessOrder;

    public LinkedHashMap8() {
        super();
        accessOrder = false;
    }

    public LinkedHashMap8(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor);
        this.accessOrder = accessOrder;
    }
}
