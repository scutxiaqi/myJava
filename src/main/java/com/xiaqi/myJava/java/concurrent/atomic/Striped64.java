package com.xiaqi.myJava.java.concurrent.atomic;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.LongBinaryOperator;

public class Striped64 {
    static final int NCPU = Runtime.getRuntime().availableProcessors();
    /**
     * 槽数组，大小为2的幂次方
     */
    transient volatile Cell[] cells;

    /**
     * 基数。
     * 1.没有遇到并发竞争时，直接使用base累加数值
     * 2.
     */
    transient volatile long base;
    /**
     * 锁标识，0表示未加锁，1表示加锁。
     */
    transient volatile int cellsBusy;

    final void longAccumulate(long x, LongBinaryOperator fn, boolean wasUncontended) {
        int h;
        // 给当前线程生成一个非0的hash值
        if ((h = getProbe()) == 0) {
            ThreadLocalRandom.current(); // force initialization
            h = getProbe();
            wasUncontended = true;
        }
        boolean collide = false; // True if last slot nonempty
        for (;;) {// 无限循环
            Cell[] as;
            Cell a;
            int n;
            long v;
            if ((as = cells) != null && (n = as.length) > 0) { // Cell[]数组已经初始化
                if ((a = as[(n - 1) & h]) == null) {// 如果槽位是空的
                    if (cellsBusy == 0) {
                        Cell r = new Cell(x); // Optimistically create
                        if (cellsBusy == 0 && casCellsBusy()) { // 尝试加锁
                            boolean created = false;
                            try { // Recheck under lock
                                Cell[] rs;
                                int m, j;
                                if ((rs = cells) != null && (m = rs.length) > 0 && rs[j = (m - 1) & h] == null) {
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
                else if (a.cas(v = a.value, ((fn == null) ? v + x : fn.applyAsLong(v, x)))) // cas更新cell值
                    break;
                else if (n >= NCPU || cells != as) // cells数组大小超过cpu核数，不会再扩容
                    collide = false; // 扩容标志。false表示不再扩容
                else if (!collide)
                    collide = true;
                else if (cellsBusy == 0 && casCellsBusy()) { // 加锁，并进行扩容
                    try {
                        if (cells == as) { // Expand table unless stale
                            Cell[] rs = new Cell[n << 1]; // 扩容一倍
                            for (int i = 0; i < n; ++i)
                                rs[i] = as[i];
                            cells = rs;
                        }
                    } finally {
                        cellsBusy = 0;
                    }
                    collide = false;
                    continue; // Retry with expanded table
                }
                h = advanceProbe(h);
            } else if (cellsBusy == 0 && cells == as && casCellsBusy()) { // 如果cells没加锁，且Cell[]数组未初始化
                boolean init = false;
                try { // Initialize table
                    if (cells == as) {
                        Cell[] rs = new Cell[2]; // 初始化大小为2的数组
                        rs[h & 1] = new Cell(x);
                        cells = rs;
                        init = true;
                    }
                } finally {
                    cellsBusy = 0;
                }
                if (init)
                    break;
            } else if (casBase(v = base, ((fn == null) ? v + x : fn.applyAsLong(v, x)))) // Cell[]数组正在初始化中，直接操作base基数，将值累加到base上
                break; // Fall back on using base
        }
    }

    /**
     * cas方式更新base值
     * 
     * @param cmp base原值
     * @param val base新值
     * @return
     */
    final boolean casBase(long cmp, long val) {
        return UNSAFE.compareAndSwapLong(this, BASE, cmp, val);
    }

    /**
     * CASes the cellsBusy field from 0 to 1 to acquire lock.
     */
    final boolean casCellsBusy() {
        return UNSAFE.compareAndSwapInt(this, CELLSBUSY, 0, 1);
    }

    /**
     * Returns the probe value for the current thread.
     * Duplicated from ThreadLocalRandom because of packaging restrictions.
     */
    static final int getProbe() {
        return UNSAFE.getInt(Thread.currentThread(), PROBE);
    }

    static final int advanceProbe(int probe) {
        probe ^= probe << 13; // xorshift
        probe ^= probe >>> 17;
        probe ^= probe << 5;
        UNSAFE.putInt(Thread.currentThread(), PROBE, probe);
        return probe;
    }

    @sun.misc.Contended
    static final class Cell {
        volatile long value;

        Cell(long x) {
            value = x;
        }

        final boolean cas(long cmp, long val) {
            return UNSAFE.compareAndSwapLong(this, valueOffset, cmp, val);
        }

        // Unsafe mechanics
        private static final sun.misc.Unsafe UNSAFE;
        private static final long valueOffset;
        static {
            try {
                UNSAFE = sun.misc.Unsafe.getUnsafe();
                Class<?> ak = Cell.class;
                valueOffset = UNSAFE.objectFieldOffset(ak.getDeclaredField("value"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    private static final sun.misc.Unsafe UNSAFE;
    private static final long BASE;
    private static final long CELLSBUSY;
    private static final long PROBE; // 线程的hash值
    static {
        try {
            UNSAFE = sun.misc.Unsafe.getUnsafe();
            Class<?> sk = Striped64.class;
            BASE = UNSAFE.objectFieldOffset(sk.getDeclaredField("base"));
            CELLSBUSY = UNSAFE.objectFieldOffset(sk.getDeclaredField("cellsBusy"));
            Class<?> tk = Thread.class;
            PROBE = UNSAFE.objectFieldOffset(tk.getDeclaredField("threadLocalRandomProbe"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
