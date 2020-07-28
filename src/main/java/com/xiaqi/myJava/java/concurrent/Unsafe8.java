package com.xiaqi.myJava.java.concurrent;

/**
 * Unsafe类，提供了硬件级别的原子操作
 *
 */
public class Unsafe8 {
    /**
     * 获取对象obj内存地址为offset的变量值， 并将该变量值加上delta
     * 
     * @param obj
     * @param offset
     * @param delta
     * @return
     */
    public final int getAndAddInt(Object obj, long offset, int delta) {
        int v;
        do {
            v = this.getIntVolatile(obj, offset);
            /*
             * while中的compareAndSwapInt()方法尝试修改v的值,具体地, 该方法也会通过obj和offset获取变量的值
             * 如果这个值和v不一样,说明其他线程修改了obj+offset地址处的值, 此时compareAndSwapInt()返回false, 继续循环
             * 如果这个值和v一样, 说明没有其他线程修改obj+offset地址处的值, 此时可以将obj+offset地址处的值改为v+delta,
             * compareAndSwapInt()返回true, 退出循环
             */
        } while (!this.compareAndSwapInt(obj, offset, v, v + delta));

        return v;
    }

    /**
     * 此方法为原子性操作。<br/>
     * 读取传入对象o在内存中偏移量为offset位置的值与期望值expected作比较.<br/>
     * 相等就把x值赋值给offset位置的值。方法返回true。 不相等，就取消赋值，方法返回false。
     * 
     * @param o
     * @param offset
     * @param expected
     * @param x
     * @return
     */
    public final native boolean compareAndSwapInt(Object o, long offset, int expected, int x);

    /**
     * 获得给定对象的指定偏移量offset的int值，使用volatile语义，总能获取到最新的int值。
     * 
     * @param o
     * @param offset
     * @return
     */
    public native int getIntVolatile(Object o, long offset);

}
