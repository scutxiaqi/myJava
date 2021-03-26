package com.xiaqi.myJava.java.base;

/**
 * Object有11个方法，实际9个(wait方法有3个，合并为1个)
 *
 */
public class Object8 {
    /**
     * final方法不能被继承。返回此对象的运行时类
     */
    public final native Class<?> getClass8();

    /**
     * 创建并返回此对象的一个副本
     */
    protected native Object clone() throws CloneNotSupportedException;

    /**
     * 根据对象内存地址计算出hash值
     */
    public native int hashCode();

    /**
     * 比较两个对象是否“相等”（默认比较内存地址）
     */
    public boolean equals(Object obj) {
        return (this == obj);
    }

    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }

    /**
     * final方法不能被继承
     */
    public final native void notify8();

    public final native void notifyAll8();

    public final native void wait8(long timeout) throws InterruptedException;

    public final void wait8(long timeout, int nanos) throws InterruptedException {

    }

    public final void wait8() throws InterruptedException {
        wait(0);
    }

    /**
     * 对象将要被回收时先调用此方法
     */
    protected void finalize() throws Throwable {
    }
}
