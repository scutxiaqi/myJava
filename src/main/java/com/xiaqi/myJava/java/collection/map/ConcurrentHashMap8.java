package com.xiaqi.myJava.java.collection.map;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMap8<K, V> {
    /**
     * 最大容量.
     */
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    /**
     * 默认初始容量
     */
    private static final int DEFAULT_CAPACITY = 16;
    /**
     * 链表转树的阈值，即链接结点数大于8时， 链表转换为树.
     */
    static final int TREEIFY_THRESHOLD = 8;
    /**
     * 树转链表的阈值，即树结点树小于6时，树转换为链表.
     */
    static final int UNTREEIFY_THRESHOLD = 6;
    /**
     * 在链表转变成树之前，还会有一次判断：
     * 即只有键值对数量大于MIN_TREEIFY_CAPACITY，才会发生转换。
     * 这是为了避免在Table建立初期，多个键值对恰好被放入了同一个链表中而导致不必要的转化。
     */
    static final int MIN_TREEIFY_CAPACITY = 64;
    /**
     * 用于在扩容时生成唯一的随机数.
     */
    private static int RESIZE_STAMP_BITS = 16;
    /**
     * 可同时进行扩容操作的最大线程数.
     */
    private static final int MAX_RESIZERS = (1 << (32 - RESIZE_STAMP_BITS)) - 1;

    /**
     * The bit shift for recording size stamp in sizeCtl.
     */
    private static final int RESIZE_STAMP_SHIFT = 32 - RESIZE_STAMP_BITS;
    static final int MOVED = -1; // 标识ForwardingNode结点（在扩容时才会出现，不存储实际数据）
    static final int TREEBIN = -2; // 标识红黑树的根结点
    static final int RESERVED = -3; // 标识ReservationNode结点
    static final int HASH_BITS = 0x7fffffff; // 用在计算hash时消除负数。负数hash在ConcurrentHashMap中有特殊意义表示在扩容或者是树节点
    transient volatile Node<K, V>[] table;// 存放node的数组，每个数组元素都是一个链表或者红黑树
    /**
     * 扩容时新生成的数组，只有在扩容时才非空.
     */
    private transient volatile Node<K, V>[] nextTable;
    /**
     * 控制table的初始化和扩容.
     * 0  : 初始默认值
     * -1 : 有线程正在进行table的初始化
     * >0 : table初始化时使用的容量，或初始化/扩容完成后的threshold
     * -(1 + nThreads) : 记录正在执行扩容任务的线程数
     */
    private transient volatile int sizeCtl;
    /**
     * 扩容时需要用到的一个下标变量.
     */
    private transient volatile int transferIndex;
    /**
     * 计数基值,当没有线程竞争时，计数将加到该变量上。类似于LongAdder的base变量
     */
    private transient volatile long baseCount;
    /**
     * 计数数组，出现并发冲突时使用。类似于LongAdder的cells数组
     */
    private transient volatile CounterCell[] counterCells;
    /**
     * 自旋标识位，用于CounterCell[]扩容时使用。类似于LongAdder的cellsBusy变量
     */
    private transient volatile int cellsBusy;

    static final int NCPU = Runtime.getRuntime().availableProcessors();

    public ConcurrentHashMap8() {
    }

    public V put(K key, V value) {
        return putVal(key, value, false);
    }

    final V putVal(K key, V value, boolean onlyIfAbsent) {
        if (key == null || value == null) {// ConcurrentHashMap键值都不允许为null
            throw new NullPointerException();
        }
        int hash = spread(key.hashCode());
        int binCount = 0;
        for (Node<K, V>[] tab = table;;) {// 无限循环
            Node<K, V> f;// 表示i位置第一个node
            int n, i, fh; // i表示新元素在数组中对位置，fh表示f节点的hash值
            if (tab == null || (n = tab.length) == 0) {// 如果数组是空的，初始化数组
                tab = initTable();
            } else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {// 如果该位置为没有node，就直接无锁插入
                // 如果添加节点成功，跳出循环
                if (casTabAt(tab, i, null, new Node<K, V>(hash, key, value, null)))
                    break;
            } else if ((fh = f.hash) == MOVED)// 如果在进行扩容，则先进行扩容操作
                tab = helpTransfer(tab, f);
            else {
                V oldVal = null;
                synchronized (f) {// 锁住链表或者红黑树的头结点
                    if (tabAt(tab, i) == f) {
                        if (fh >= 0) {// 表示该节点是链表结构
                            binCount = 1;
                            for (Node<K, V> e = f;; ++binCount) {
                                K ek;
                                // 相同的key进行put就会覆盖原先的value
                                if (e.hash == hash && ((ek = e.key) == key || (ek != null && key.equals(ek)))) {
                                    oldVal = e.val;
                                    if (!onlyIfAbsent)
                                        e.val = value;
                                    break;
                                }
                                Node<K, V> pred = e;
                                // 遍历到链表尾部了，添加新节点到链表尾部
                                if ((e = e.next) == null) {
                                    pred.next = new Node<K, V>(hash, key, value, null);
                                    break;
                                }
                            }
                        } else if (f instanceof TreeBin) { // 红黑树结构
                            Node<K, V> p;
                            binCount = 2;
                            if ((p = ((TreeBin<K, V>) f).putTreeVal(hash, key, value)) != null) {
                                oldVal = p.val;
                                if (!onlyIfAbsent)
                                    p.val = value;
                            }
                        }
                    }
                }
                if (binCount != 0) {// 如果链表的长度大于8，时就会进行红黑树的转换
                    if (binCount >= TREEIFY_THRESHOLD)
                        treeifyBin(tab, i);
                    if (oldVal != null)
                        return oldVal;
                    break;
                }
            }
        }
        addCount(1L, binCount); // 统计size，并且检查是否需要扩容
        return null;
    }

    private final void addCount(long x, int check) {
        CounterCell[] as;
        long b, s;
        // 如果计数盒子不是空 或者 修改 baseCount 失败
        if ((as = counterCells) != null || !U.compareAndSwapLong(this, BASECOUNT, b = baseCount, s = b + x)) {
            CounterCell a;
            long v;
            int m;
            boolean uncontended = true;
            // 计数盒子是空的 或者 盒子最大位置为空（数组长度为4，最大位置是as[3]） 或者
            // 对盒子最大位置计数失败（出现并发了）。就进入if条件语句，执行fullAddCount方法
            //
            if (as == null || (m = as.length - 1) < 0 || (a = as[m]) == null
                    || !(uncontended = U.compareAndSwapLong(a, CELLVALUE, v = a.value, v + x))) {
                fullAddCount(x, uncontended);
                return;
            }
            if (check <= 1)
                return;
            s = sumCount();
        }
        // 如果需要检查,检查是否需要扩容，在 putVal 方法调用时，默认就是要检查的。
        if (check >= 0) {
            Node<K, V>[] tab, nt;
            int n, sc;
            while (s >= (long) (sc = sizeCtl) && (tab = table) != null && (n = tab.length) < MAXIMUM_CAPACITY) {
                int rs = resizeStamp(n);
                if (sc < 0) {
                    if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 || sc == rs + MAX_RESIZERS || (nt = nextTable) == null
                            || transferIndex <= 0)
                        break;
                    if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1)) {
                        // transfer(tab, nt);
                    }

                } else if (U.compareAndSwapInt(this, SIZECTL, sc, (rs << RESIZE_STAMP_SHIFT) + 2)) {
                    // transfer(tab, null);
                }

                s = sumCount();
            }
        }
    }

    private final void fullAddCount(long x, boolean wasUncontended) {
        int h;
        // 给当前线程生成一个非0的hash值
        if ((h = ThreadLocalRandom8.getProbe()) == 0) {
            ThreadLocalRandom8.localInit(); // force initialization
            h = ThreadLocalRandom8.getProbe();
            wasUncontended = true;
        }
        boolean collide = false; // True if last slot nonempty
        for (;;) {
            CounterCell[] as;
            CounterCell a;
            int n;
            long v;
            if ((as = counterCells) != null && (n = as.length) > 0) {
                if ((a = as[(n - 1) & h]) == null) {
                    if (cellsBusy == 0) { // Try to attach new Cell
                        CounterCell r = new CounterCell(x); // Optimistic create
                        if (cellsBusy == 0 && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                            boolean created = false;
                            try { // Recheck under lock
                                CounterCell[] rs;
                                int m, j;
                                if ((rs = counterCells) != null && (m = rs.length) > 0 && rs[j = (m - 1) & h] == null) {
                                    rs[j] = r;
                                    created = true;
                                }
                            } finally {
                                cellsBusy = 0;
                            }
                            if (created)
                                break;
                            continue; // Slot is now non-empty
                        }
                    }
                    collide = false;
                } else if (!wasUncontended) // CAS already known to fail
                    wasUncontended = true; // Continue after rehash
                else if (U.compareAndSwapLong(a, CELLVALUE, v = a.value, v + x))
                    break;
                else if (counterCells != as || n >= NCPU)
                    collide = false; // At max size or stale
                else if (!collide)
                    collide = true;
                else if (cellsBusy == 0 && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                    try {
                        if (counterCells == as) {// Expand table unless stale
                            CounterCell[] rs = new CounterCell[n << 1];
                            for (int i = 0; i < n; ++i)
                                rs[i] = as[i];
                            counterCells = rs;
                        }
                    } finally {
                        cellsBusy = 0;
                    }
                    collide = false;
                    continue; // Retry with expanded table
                }
                h = ThreadLocalRandom8.advanceProbe(h);
            } else if (cellsBusy == 0 && counterCells == as && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                boolean init = false;
                try { // Initialize table
                    if (counterCells == as) {
                        CounterCell[] rs = new CounterCell[2];
                        rs[h & 1] = new CounterCell(x);
                        counterCells = rs;
                        init = true;
                    }
                } finally {
                    cellsBusy = 0;
                }
                if (init)
                    break;
            } else if (U.compareAndSwapLong(this, BASECOUNT, v = baseCount, v + x))
                break; // Fall back on using base
        }
    }

    /**
     * 计算hash值
     * 
     * @param h
     * @return
     */
    static final int spread(int h) {
        return (h ^ (h >>> 16)) & HASH_BITS;
    }

    /**
     * 线程安全的初始化数组（利用CAS）
     * 
     * @return
     */
    private final Node<K, V>[] initTable() {
        Node<K, V>[] tab;
        int sc;
        while ((tab = table) == null || tab.length == 0) {
            if ((sc = sizeCtl) < 0) {// 如果一个线程发现sizeCtl<0，意味着另外的线程执行CAS操作成功
                Thread.yield(); // 使当前线程由执行状态，变成为就绪状态，让出cpu时间
            } else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                try {
                    if ((tab = table) == null || tab.length == 0) {
                        int n = (sc > 0) ? sc : DEFAULT_CAPACITY; // 数组长度，默认值为16
                        @SuppressWarnings("unchecked")
                        Node<K, V>[] nt = (Node<K, V>[]) new Node<?, ?>[n];
                        table = tab = nt;
                        sc = n - (n >>> 2);// 负载因子，等价于0.75*capacity，如果n=16，则sc=12
                    }
                } finally {
                    sizeCtl = sc;//
                }
                break;
            }
        }
        return tab;
    }

    private final void treeifyBin(Node<K, V>[] tab, int index) {
        Node<K, V> b;
        int n, sc;
        if (tab != null) {
            if ((n = tab.length) < MIN_TREEIFY_CAPACITY) {
                // tryPresize(n << 1);
            } else if ((b = tabAt(tab, index)) != null && b.hash >= 0) {
                synchronized (b) {
                    if (tabAt(tab, index) == b) {
                        TreeNode<K, V> hd = null, tl = null;
                        for (Node<K, V> e = b; e != null; e = e.next) {
                            TreeNode<K, V> p = new TreeNode<K, V>(e.hash, e.key, e.val, null, null);
                            if ((p.prev = tl) == null)
                                hd = p;
                            else
                                tl.next = p;
                            tl = p;
                        }
                        setTabAt(tab, index, new TreeBin<K, V>(hd));
                    }
                }
            }
        }
    }

    final Node<K, V>[] helpTransfer(Node<K, V>[] tab, Node<K, V> f) {
        Node<K, V>[] nextTab;
        int sc;
        if (tab != null && (f instanceof ForwardingNode) && (nextTab = ((ForwardingNode<K, V>) f).nextTable) != null) {
            int rs = resizeStamp(tab.length);
            while (nextTab == nextTable && table == tab && (sc = sizeCtl) < 0) {
                if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 || sc == rs + MAX_RESIZERS || transferIndex <= 0)
                    break;
                if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1)) {
                    // transfer(tab, nextTab);
                    break;
                }
            }
            return nextTab;
        }
        return table;
    }

    static final int resizeStamp(int n) {
        return Integer.numberOfLeadingZeros(n) | (1 << (RESIZE_STAMP_BITS - 1));
    }

    public long mappingCount() {
        long n = sumCount();
        return (n < 0L) ? 0L : n; // ignore transient negative values
    }

    final long sumCount() {
        CounterCell[] as = counterCells;
        CounterCell a;
        long sum = baseCount;
        if (as != null) {
            for (int i = 0; i < as.length; ++i) {
                if ((a = as[i]) != null)
                    sum += a.value;
            }
        }
        return sum;
    }

    public V get(Object key) {
        Node<K, V>[] tab;
        Node<K, V> e, p;
        int n, eh;
        K ek;
        int h = spread(key.hashCode());
        if ((tab = table) != null && (n = tab.length) > 0 && (e = tabAt(tab, (n - 1) & h)) != null) {
            if ((eh = e.hash) == h) {// table[i]就是待查找的项，直接返回
                if ((ek = e.key) == key || (ek != null && key.equals(ek)))
                    return e.val;
            } else if (eh < 0)// hash值<0, 说明遇到特殊结点(非链表结点), 调用find方法查找
                return (p = e.find(h, key)) != null ? p.val : null;
            while ((e = e.next) != null) {// 按链表方式查找
                if (e.hash == h && ((ek = e.key) == key || (ek != null && key.equals(ek))))
                    return e.val;
            }
        }
        return null;
    }

    static class Node<K, V> implements Map.Entry<K, V> {
        final int hash;
        final K key;
        volatile V val;
        volatile Node<K, V> next;

        Node(int hash, K key, V val, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.val = val;
            this.next = next;
        }

        public final K getKey() {
            return key;
        }

        public final V getValue() {
            return val;
        }

        public final int hashCode() {
            return key.hashCode() ^ val.hashCode();
        }

        public final String toString() {
            return key + "=" + val;
        }

        public final V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        public final boolean equals(Object o) {
            Object k, v, u;
            Map.Entry<?, ?> e;
            return ((o instanceof Map.Entry) && (k = (e = (Map.Entry<?, ?>) o).getKey()) != null && (v = e.getValue()) != null
                    && (k == key || k.equals(key)) && (v == (u = val) || v.equals(u)));
        }

        /**
         * Virtualized support for map.get(); overridden in subclasses.
         */
        Node<K, V> find(int h, Object k) {
            Node<K, V> e = this;
            if (k != null) {
                do {
                    K ek;
                    if (e.hash == h && ((ek = e.key) == k || (ek != null && k.equals(ek))))
                        return e;
                } while ((e = e.next) != null);
            }
            return null;
        }
    }

    static final class TreeBin<K, V> extends Node<K, V> {
        TreeBin(TreeNode<K, V> b) {
            super(TREEBIN, null, null, null);
        }

        final TreeNode<K, V> putTreeVal(int h, K k, V v) {
            return null;
        }
    }

    static final class TreeNode<K, V> extends Node<K, V> {
        TreeNode<K, V> parent; // red-black tree links
        TreeNode<K, V> left;
        TreeNode<K, V> right;
        TreeNode<K, V> prev; // needed to unlink next upon deletion
        boolean red;

        TreeNode(int hash, K key, V val, Node<K, V> next, TreeNode<K, V> parent) {
            super(hash, key, val, next);
            this.parent = parent;
        }
    }

    static final class ForwardingNode<K, V> extends Node<K, V> {
        final Node<K, V>[] nextTable;

        ForwardingNode(Node<K, V>[] tab) {
            super(MOVED, null, null, null);
            this.nextTable = tab;
        }

        Node<K, V> find(int h, Object k) {
            // loop to avoid arbitrarily deep recursion on forwarding nodes
            outer: for (Node<K, V>[] tab = nextTable;;) {
                Node<K, V> e;
                int n;
                if (k == null || tab == null || (n = tab.length) == 0 || (e = tabAt(tab, (n - 1) & h)) == null)
                    return null;
                for (;;) {
                    int eh;
                    K ek;
                    if ((eh = e.hash) == h && ((ek = e.key) == k || (ek != null && k.equals(ek))))
                        return e;
                    if (eh < 0) {
                        if (e instanceof ForwardingNode) {
                            tab = ((ForwardingNode<K, V>) e).nextTable;
                            continue outer;
                        } else
                            return e.find(h, k);
                    }
                    if ((e = e.next) == null)
                        return null;
                }
            }
        }
    }

    @sun.misc.Contended
    static final class CounterCell {
        volatile long value;

        CounterCell(long x) {
            value = x;
        }
    }

    /**
     * 寻找tab数组之i位置在内存中的数据
     * 
     * @param tab
     * @param i
     * @return
     */
    @SuppressWarnings("unchecked")
    static final <K, V> Node<K, V> tabAt(Node<K, V>[] tab, int i) {
        /*
         * 获取obj对象中offset偏移地址对应的object型field的值,支持volatile load语义。 public native Object
         * getObjectVolatile(Object obj, long offset);
         */
        return (Node<K, V>) U.getObjectVolatile(tab, ((long) i << ASHIFT) + ABASE);
    }

    /**
     * CAS方式更新tab上 i位置的结点为 v
     * @param <K>
     * @param <V>
     * @param tab
     * @param i
     * @param c
     * @param v
     * @return
     */
    static final <K, V> boolean casTabAt(Node<K, V>[] tab, int i, Node<K, V> c, Node<K, V> v) {
        return U.compareAndSwapObject(tab, ((long) i << ASHIFT) + ABASE, c, v);
    }

    static final <K, V> void setTabAt(Node<K, V>[] tab, int i, Node<K, V> v) {
        U.putObjectVolatile(tab, ((long) i << ASHIFT) + ABASE, v);
    }

    // Unsafe mechanics
    private static final sun.misc.Unsafe U; // 提供Java直接操作内存的方法
    private static final long SIZECTL;
    private static final long TRANSFERINDEX;
    private static final long BASECOUNT;
    private static final long CELLSBUSY;
    private static final long CELLVALUE;
    private static final long ABASE; // 起始位置
    private static final int ASHIFT;

    static {
        try {
            U = sun.misc.Unsafe.getUnsafe();
            Class<?> k = ConcurrentHashMap.class;
            SIZECTL = U.objectFieldOffset(k.getDeclaredField("sizeCtl")); // objectFieldOffset方法返回指定field的内存地址偏移量
            TRANSFERINDEX = U.objectFieldOffset(k.getDeclaredField("transferIndex"));
            BASECOUNT = U.objectFieldOffset(k.getDeclaredField("baseCount"));
            CELLSBUSY = U.objectFieldOffset(k.getDeclaredField("cellsBusy"));
            Class<?> ck = CounterCell.class;
            CELLVALUE = U.objectFieldOffset(ck.getDeclaredField("value"));
            Class<?> ak = Node[].class;
            ABASE = U.arrayBaseOffset(ak);
            int scale = U.arrayIndexScale(ak);
            if ((scale & (scale - 1)) != 0)
                throw new Error("data type scale not a power of two");
            ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
