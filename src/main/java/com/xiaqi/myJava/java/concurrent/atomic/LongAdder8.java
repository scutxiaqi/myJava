package com.xiaqi.myJava.java.concurrent.atomic;

/**
 * 在高并发的场景下会比它的前辈————AtomicLong 具有更好的性能
 *
 */
public class LongAdder8 extends Striped64 {

    /**
     * 类似于i++操作
     */
    public void increment() {
        add(1L);
    }

    public void add(long x) {
        Cell[] as;
        long b, v;
        int m;
        Cell a;
        // 如果cells为null，所有的值都会累积到base中。否则会使用cells计算
        if ((as = cells) != null || !casBase(b = base, b + x)) {
            boolean uncontended = true;
            // 如果槽数组as为空或者数组中对应槽位是null，进入if块中；不为空，所有的值都会累积到槽数组中
            if (as == null || (m = as.length - 1) < 0 || (a = as[getProbe() & m]) == null || !(uncontended = a.cas(v = a.value, v + x))) {
                longAccumulate(x, null, uncontended);
            }

        }
    }

    /**
     * 返回计数值。
     * 计数值可能是不准确的。因为可能有其它线程在进行累加。
     * 方法的调用和返回不是同一时刻，在高并发下，这个值只是近似准确的计数值。
     * 
     * 高并发时，除非全局加锁，否则得不到某个时刻准确值
     * 
     * @return
     */
    public long sum() {
        Cell[] as = cells;
        Cell a;
        long sum = base;
        if (as != null) {
            for (int i = 0; i < as.length; ++i) {
                if ((a = as[i]) != null)
                    sum += a.value;
            }
        }
        return sum;
    }

}
