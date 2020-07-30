package com.xiaqi.myJava.java.collection.list;

import java.util.AbstractList;
import java.util.Arrays;

public class ArrayList8<E> extends AbstractList<E> {
    /**
     * 初始化容量大小为10
     */
    private static final int DEFAULT_CAPACITY = 10;

    private static final Object[] EMPTY_ELEMENTDATA = {};

    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    transient Object[] elementData;

    /**
     * ArrayList的实际大小。数组中的元素个数
     */
    private int size;

    /**
     * 构造一个空列表
     */
    public ArrayList8() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

    public ArrayList8(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
    }

    public boolean add(E e) {
        ensureCapacityInternal(size + 1); // Increments modCount!!
        // 将元素添加到数组末尾
        elementData[size++] = e;
        return true;
    }

    /**
     * 数组容量检查，不够时进行扩容
     * @param minCapacity 想要的最小容量
     */
    private void ensureCapacityInternal(int minCapacity) {
        ensureExplicitCapacity(calculateCapacity(elementData, minCapacity));
    }

    private void ensureExplicitCapacity(int minCapacity) {
        modCount++;

        // 数组越界，需要进行容量扩展
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }

    private static int calculateCapacity(Object[] elementData, int minCapacity) {
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            return Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        return minCapacity;
    }

    /**
    * 分派给arrays的最大容量
    * 为什么要减去8呢？
    * 因为某些VM会在数组中保留一些头字，尝试分配这个最大存储容量，可能会导致array容量大于VM的limit，最终导致OutOfMemoryError。
    */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * 数组扩容1.5倍，初始化数组长度为10
     * @param minCapacity
     */
    private void grow(int minCapacity) {
        int oldCapacity = elementData.length;// 当前数组的容量
        // newCapacity代表新数组的长度，为oldCapacity的1.5倍
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)// 如果扩容后的容量大于临界值
            newCapacity = hugeCapacity(minCapacity);
        // 新的容量大小已经确定好了，就copy数组，改变容量大小。
        // copyof(原数组，新的数组长度)
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }

    public int size() {
        return size;
    }

    public E get(int index) {
        rangeCheck(index);
        return elementData(index);
    }

    /**
     * 按索引删除元素
     */
    public E remove(int index) {
        // 如果索引值大于ArrayList里元素的个数，则抛出索引越界异常
        rangeCheck(index);
        modCount++;
        // 用oldValue 保存要删除的值
        E oldValue = elementData(index);
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            /**
             * @param      src      源数组
             * @param      srcPos   源数组的起始位置
             * @param      dest     目标数组
             * @param      destPos  目标数组的起始位置
             * @param      length   复制的长度
             * arraycopy(Object src,int srcPos,Object dest, int destPos,int length)
             */
            System.arraycopy(elementData, index + 1, elementData, index, numMoved);
        }
        elementData[--size] = null; // clear to let GC do its work
        return oldValue;
    }

    @SuppressWarnings("unchecked")
    E elementData(int index) {
        return (E) elementData[index];
    }

    /**
     * 如果索引值大于ArrayList里元素的个数，则抛出索引越界异常
     * @param index
     */
    private void rangeCheck(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private String outOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + size;
    }
}
