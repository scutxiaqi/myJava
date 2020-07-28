package com.xiaqi.myJava.java.concurrent.atomic;

import sun.misc.Unsafe;

public class AtomicInteger8 {
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long valueOffset; // 变量值value在内存中的偏移地址

    static {
        try {
            valueOffset = unsafe.objectFieldOffset(AtomicInteger8.class.getDeclaredField("value")); // 获取value变量的偏移量, 赋值给valueOffset
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    private volatile int value;

    /**
     * 原子性操作i+delta
     * 
     * @return
     */
    public final int getAndAdd(int delta) {
        return unsafe.getAndAddInt(this, valueOffset, delta) ;
    }

    /**
     * 原子性操作i++
     * 
     * @return
     */
    public final int getAndIncrement() {
        return unsafe.getAndAddInt(this, valueOffset, 1);
    }
}
