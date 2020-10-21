package com.xiaqi.myJava.java.collection.list;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程安全的ArrayList.（读操作不加锁，写操作加锁）<br>
 * 线程安全原理：运用了一种“ 写时复制”的思想：读操作在原列表，写操作在列表副本上<br>
 * 通俗的理解就是当我们需要修改（增/删/改）列表中的元素时，不直接进行修改，而是先将列表Copy，然后在新的副本上进行修改，修改完成之后，再将引用从原列表指向新列表。<br>
 * 
 * 适合场景：读多写少。不适合大数据量(会造成频繁GC)
 */
public class CopyOnWriteArrayList8<E> {
    /**
     * 排它锁, 用于同步修改操作
     */
    final transient ReentrantLock lock = new ReentrantLock();
    /**
     * 内部数组
     */
    private transient volatile Object[] array;

    final void setArray(Object[] a) {
        array = a;
    }

    final Object[] getArray() {
        return array;
    }

    /**
     * Creates an empty list.
     */
    public CopyOnWriteArrayList8() {
        setArray(new Object[0]);
    }

    public boolean add(E e) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            // 拷贝array到一个新数组, 新数组的大小是原来数组大小加增加1,所以CopyOnWriteArrayList是无界List
            Object[] newElements = Arrays.copyOf(elements, len + 1);
            newElements[len] = e;
            setArray(newElements);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public E remove(int index) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            E oldValue = get(elements, index);
            int numMoved = len - index - 1;
            if (numMoved == 0)
                setArray(Arrays.copyOf(elements, len - 1));
            else {
                Object[] newElements = new Object[len - 1];
                System.arraycopy(elements, 0, newElements, 0, index);
                System.arraycopy(elements, index + 1, newElements, index, numMoved);
                setArray(newElements);
            }
            return oldValue;
        } finally {
            lock.unlock();
        }
    }

    public E get(int index) {
        return get(getArray(), index);
    }

    @SuppressWarnings("unchecked")
    private E get(Object[] a, int index) {
        return (E) a[index];
    }

    public int size() {
        return getArray().length;
    }
}
