package com.xiaqi.myJava.java.concurrent.threadlocal;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadLocal8<T> {
    private final int threadLocalHashCode = nextHashCode();
    private static AtomicInteger nextHashCode = new AtomicInteger();
    /**
     * 这个值和斐波那契散列
     */
    private static final int HASH_INCREMENT = 0x61c88647;

    public void set(T value) {
        Thread8 t = Thread8.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            createMap(t, value);
    }

    public T get() {
        Thread8 t = Thread8.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null) {
            ThreadLocalMap.Entry e = map.getEntry(this);
            if (e != null) {
                @SuppressWarnings("unchecked")
                T result = (T) e.value;
                return result;
            }
        }
        return setInitialValue();
    }

    private T setInitialValue() {
        T value = initialValue();
        Thread8 t = Thread8.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            createMap(t, value);
        return value;
    }

    protected T initialValue() {
        return null;
    }

    ThreadLocalMap getMap(Thread8 t) {
        return t.threadLocals;
    }

    void createMap(Thread8 t, T firstValue) {
        t.threadLocals = new ThreadLocalMap(this, firstValue);
    }

    private static int nextHashCode() {
        return nextHashCode.getAndAdd(HASH_INCREMENT);
    }

    /**
     * 这个类本质上是一个map，和HashMap之类的实现相似，依然是key-value的形式
     */
    static class ThreadLocalMap {
        /**
         * 初始容量 —— 必须是2的冥
         */
        private static final int INITIAL_CAPACITY = 16;
        /**
         * 存放数据的table，Entry类的定义在下面分析
         * 同样，数组长度必须是2的冥。
         */
        private Entry[] table;
        /**
         * 数组里面entrys的个数，可以用于判断table当前使用量是否超过负因子。
         */
        private int size = 0;
        /**
         * 进行扩容的阈值，表使用量大于它的时候进行扩容。
         */
        private int threshold; // Default to 0

        /**
         * 定义为长度的2/3
         */
        private void setThreshold(int len) {
            threshold = len * 2 / 3;
        }

        ThreadLocalMap(ThreadLocal8<?> firstKey, Object firstValue) {
            // 初始化table
            table = new Entry[INITIAL_CAPACITY];
            // 计算索引
            int i = firstKey.threadLocalHashCode & (INITIAL_CAPACITY - 1);
            // 设置值
            table[i] = new Entry(firstKey, firstValue);
            size = 1;
            setThreshold(INITIAL_CAPACITY);// 设置阈值
        }

        private void set(ThreadLocal8<?> key, Object value) {
            Entry[] tab = table;
            int len = tab.length;
            // 计算槽位值
            int i = key.threadLocalHashCode & (len - 1);
            // 根据获取到的索引进行循环，如果当前索引上的table[i]不为空，在没有return的情况下，就使用nextIndex()获取下一个（上面提到到线性探测法）。
            for (Entry e = tab[i]; e != null; e = tab[i = nextIndex(i, len)]) {
                ThreadLocal8<?> k = e.get();
                // table[i]上key不为空，并且和当前key相同，更新value
                if (k == key) {
                    e.value = value;
                    return;
                }
                /**
                 * table[i]上的key为空，说明被回收了（上面的弱引用中提到过）。
                 * 这个时候说明改table[i]可以重新使用，用新的key-value将其替换,并删除其他无效的entry
                 */
                if (k == null) {
                    replaceStaleEntry(key, value, i);
                    return;
                }
            }
            // 找到为空的插入位置，插入值，在为空的位置插入需要对size进行加1操作
            tab[i] = new Entry(key, value);
            int sz = ++size;
            /**
             * cleanSomeSlots用于清除那些e.get()==null，也就是table[index] != null && table[index].get()==null
             * 之前提到过，这种数据key关联的对象已经被回收，所以这个Entry(table[index])可以被置null。
             * 如果没有清除任何entry,并且当前使用量达到了负载因子所定义(长度的2/3)，那么进行rehash()
             */
            if (!cleanSomeSlots(i, sz) && sz >= threshold)
                rehash();
        }
        
        private Entry getEntry(ThreadLocal8<?> key) {
            int i = key.threadLocalHashCode & (table.length - 1);
            Entry e = table[i];
            if (e != null && e.get() == key)
                return e;
            else
                return getEntryAfterMiss(key, i, e);
        }
        
        private Entry getEntryAfterMiss(ThreadLocal8<?> key, int i, Entry e) {
            return null;
        }

        /**
         * 获取环形数组的下一个索引
         */
        private static int nextIndex(int i, int len) {
            return ((i + 1 < len) ? i + 1 : 0);
        }

        /**
         * 替换无效entry
         */
        private void replaceStaleEntry(ThreadLocal8<?> key, Object value, int staleSlot) {

        }

        /**
         * 启发式的扫描清除，扫描次数由传入的参数n决定
         *
         * @param i 从i向后开始扫描（不包括i，因为索引为i的Slot肯定为null）
         *
         * @param n 控制扫描次数，正常情况下为 log2(n) ，
         * 如果找到了无效entry，会将n重置为table的长度len,进行段清除。
         *
         * map.set()点用的时候传入的是元素个数，replaceStaleEntry()调用的时候传入的是table的长度len
         *
         * @return true if any stale entries have been removed.
         */
        private boolean cleanSomeSlots(int i, int n) {
            return true;
        }

        private void rehash() {

        }

        /**
         * Entry继承WeakReference，并且用ThreadLocal作为key.如果key为null
         * (entry.get() == null)表示key不再被引用，表示ThreadLocal对象被回收
         * 因此这时候entry也可以从table从清除。
         */
        static class Entry extends WeakReference<ThreadLocal8<?>> {
            /** The value associated with this ThreadLocal. */
            Object value;

            Entry(ThreadLocal8<?> k, Object v) {
                super(k);
                value = v;
            }
        }
    }
}
