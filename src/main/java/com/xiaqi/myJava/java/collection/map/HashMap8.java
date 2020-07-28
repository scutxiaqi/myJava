package com.xiaqi.myJava.java.collection.map;

import java.util.Map;
import java.util.Objects;

public class HashMap8<K, V> {
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;// 默认的初始容量是16
    // 最大容量
    static final int MAXIMUM_CAPACITY = 1 << 30;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;// 默认的加载因子。0.75f是官方给出的一个比较好的临界值
    // 当桶(bucket)上的结点数大于这个值时会转成红黑树
    static final int TREEIFY_THRESHOLD = 8;
    // 当桶(bucket)上的结点数小于等于这个值时树转链表
    static final int UNTREEIFY_THRESHOLD = 6;
    // 桶中结构转化为红黑树对应的table的最小大小
    static final int MIN_TREEIFY_CAPACITY = 64;
    transient Node<K, V>[] table; // 存储元素的数组，总是2的幂次倍
    // 存放具体元素的集
    // transient Set<map.entry<K, V>> entrySet;
    // map中存放元素的个数，注意这个不等于数组的长度。
    transient int size;
    // 每次扩容和更改map结构的计数器
    transient int modCount;
    // 临界值 当实际大小(容量*填充因子)超过临界值时，会进行扩容
    int threshold;
    /**
     * 加载因子。控制数组存放数据的疏密程度。<br/>
     * loadFactor太大导致查找元素效率低，太小导致数组的利用率低，存放的数据会很分散。
     */
    final float loadFactor;

    public HashMap8() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
    }

    public HashMap8(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }

    /**
     * 返回与参数最近的2的整数次幂的数。比如10，则返回16
     */
    public HashMap8(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * 返回大于输入参数且最近的2的整数次幂的数。比如10，则返回16
     */
    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;// 右移1位，|和&是位运算，6|3=110|011=111=7
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }

    final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
        Node<K, V>[] tab = table;
        int n;// 存储元素数组的长度length
        if (tab == null || (n = tab.length) == 0) {// 存储元素的数组table未初始化或者长度为0，初始化table
            tab = resize();
            n = tab.length;
        }
        Node<K, V> p;
        int i = (n - 1) & hash;// 确定元素要存放的位置。详见indexFor方法
        p = tab[i];// 该位置第一个node
        if (p == null) {// 如果该位置为没有node，将新node直接放进去
            tab[i] = newNode(hash, key, value, null);
        } else {
            Node<K, V> e;// 表示被命中的节点(新旧节点hash和key值均相等)
            K k = p.key;
            if (p.hash == hash && (k == key || (key != null && key.equals(k)))) {// 如果p节点被命中
                e = p;
            } else if (p instanceof TreeNode) {// 如果p节点是TreeNode，把新元素放入树中
                e = ((TreeNode<K, V>) p).putTreeVal(this, tab, hash, key, value);
            } else {// 如果p为链表结点
                for (int binCount = 0;; ++binCount) {// 无限循环遍历节点
                    e = p.next;
                    if (e == null) {// 到达链表的尾部
                        p.next = newNode(hash, key, value, null);// 在尾部插入新结点
                        if (binCount >= TREEIFY_THRESHOLD - 1) {// 当该链表的长度大于等于9，转化为红黑树
                            treeifyBin(tab, hash);
                        }
                        break;
                    }
                    if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k)))) { // 如果链表中某节点被命中，跳出循环
                        break;
                    }
                    p = e;
                }
            }
            if (e != null) { // 如果map中存在key值相等的元素，直接替换value，并返回旧值
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        if (++size > threshold) {// map中元素个数超过临界值时，会进行扩容
            resize();
        }
        return null;
    }

    /**
     * 当该链表的长度大于等于9
     * 1.如果数组长度小于 64，扩容
     * 2.数组长度大于等于64，转化为红黑树
     * 
     * @param tab  map中的数组
     * @param hash 要增加的键值对的key的hash值
     */
    final void treeifyBin(Node<K, V>[] tab, int hash) {
        int n, index;
        Node<K, V> e;
        /*
         * 如果元素数组为空 或者 数组长度小于 64，不进行转换，直接扩容
         * 当一个数组位置上集中了多个键值对，那是因为这些key的hash值和数组长度取模之后结果相同。（并不是因为这些key的hash值相同）
         * 因为hash值相同的概率不高，所以可以通过扩容的方式，来使得最终这些key的hash值在和新的数组长度取模之后，拆分到多个数组位置上。
         */
        if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY) {
            resize();
        } else if ((e = tab[index = (n - 1) & hash]) != null) {// 得到链表的首节点
            TreeNode<K, V> hd = null, tl = null;
            do {
                TreeNode<K, V> p = replacementTreeNode(e, null);
                if (tl == null)
                    hd = p;
                else {
                    p.prev = tl;
                    tl.next = p;
                }
                tl = p;
            } while ((e = e.next) != null);
            if ((tab[index] = hd) != null)
                hd.treeify(tab);
        }
    }

    TreeNode<K, V> replacementTreeNode(Node<K, V> p, Node<K, V> next) {
        return new TreeNode<>(p.hash, p.key, p.value, next);
    }

    /**
     * 散列值优化函数。减少map中的碰撞<br/>
     * hash值是int类型，32位bit。取值范围从-2147483648到2147483648，有几十亿。对于长度16的数组来说太大
     * 
     * @param key
     * @return
     */
    static final int hash(Object key) {
        int h;
        // 做一次扰动。将原hash值中高16位与低16位做位运算，使低位信息中含有高位信息
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * new 一个节点
     * 
     * @param hash
     * @param key
     * @param value
     * @param next
     * @return
     */
    Node<K, V> newNode(int hash, K key, V value, Node<K, V> next) {
        return new Node<>(hash, key, value, next);
    }

    /**
     * 确定元素要存放的位置<br/>
     * 当数组长度为2的n次方时,位运算从数值上来讲其实等价于对length取模，也就是h%length。位运算快于取模运算
     * 
     * @param hash
     * @param length
     * @return
     */
    private int indexFor(int hash, int length) {
        return hash & (length - 1);
    }

    /**
     * 扩容。伴随着一次重新hash分配，并且会遍历hash表中所有的元素，非常耗时
     * 
     * @return
     */
    final Node<K, V>[] resize() {
        Node<K, V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;// 旧数组length
        int oldThr = threshold;
        int newCap;// 新数组length
        int newThr = 0;// 新临界值
        /**
         * 扩容数组。1.map为空，初始化容量为16 2.否则容量扩大一倍
         */
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {// 超过最大值就不再扩充了
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            newCap = oldCap << 1;// newCap扩容一倍，等价于newCap = newCap*2;
            if (newCap < MAXIMUM_CAPACITY && oldCap >= DEFAULT_INITIAL_CAPACITY) {// 旧容量大于等于16，且新容量小于最大值
                newThr = oldThr << 1; // 临界值也扩大为原来2倍
            }
        } else if (oldThr > 0) {// 在第一次带参数初始化时候会有这种情况
            newCap = oldThr;// 如果原来的thredshold大于0则将容量设为原来的thredshold.
        } else { // 如果这个map是空的
            newCap = DEFAULT_INITIAL_CAPACITY;// 设置初始容量为16
            newThr = (int) (DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);// 设置临界值为16*0.75=12
        }
        if (newThr == 0) {
            float ft = (float) newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float) MAXIMUM_CAPACITY ? (int) ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        @SuppressWarnings({ "rawtypes", "unchecked" })
        Node<K, V>[] newTab = (Node<K, V>[]) new Node[newCap];// 新数组
        table = newTab;
        /**
         * 将节点移动到新数组中
         */
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                Node<K, V> e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null) {// 如果只有一个节点，直接移动
                        newTab[e.hash & (newCap - 1)] = e;
                    } else if (e instanceof TreeNode) {// 如果是tree
                        ((TreeNode<K, V>) e).split(this, newTab, j, oldCap);
                    } else { // 如果是链表
                        Node<K, V> loHead = null, loTail = null;// 低位节点
                        Node<K, V> hiHead = null, hiTail = null;// 高位节点，一个长度32的数组，如果低位是1，则高位是16+1=17
                        Node<K, V> next;
                        // oldTab[j]上的节点，在新数组中的位置，只可能是j或者j + oldCap
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            } else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;// 在原位置不动
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }

    void afterNodeAccess(Node<K, V> p) {
    }

    /**
     * 链表节点
     * 
     * @author xiaqi
     *
     * @param <K>
     * @param <V>
     */
    static class Node<K, V> implements Map.Entry<K, V> {
        final int hash;// 哈希值
        final K key;// 键
        V value;// 值
        Node<K, V> next; // 指向下一个节点

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final K getKey() {
            return key;
        }

        public final V getValue() {
            return value;
        }

        public final String toString() {
            return key + "=" + value;
        }

        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (o == this)
                return true;
            if (o instanceof Map.Entry) {
                Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
                if (Objects.equals(key, e.getKey()) && Objects.equals(value, e.getValue()))
                    return true;
            }
            return false;
        }
    }

    static class Entry<K, V> extends Node<K, V> {
        Entry<K, V> before, after;

        Entry(int hash, K key, V value, Node<K, V> next) {
            super(hash, key, value, next);
        }
    }

    /**
     * 树节点
     * 
     * @author xiaqi
     *
     * @param <K>
     * @param <V>
     */
    static final class TreeNode<K, V> extends Entry<K, V> {
        TreeNode<K, V> parent; // red-black tree links
        TreeNode<K, V> left;
        TreeNode<K, V> right;
        TreeNode<K, V> prev; // needed to unlink next upon deletion
        boolean red;

        TreeNode(int hash, K key, V val, Node<K, V> next) {
            super(hash, key, val, next);
        }

        final TreeNode<K, V> putTreeVal(HashMap8<K, V> map, Node<K, V>[] tab, int h, K k, V v) {
            return null;
        }

        /** 这个方法在HashMap进行扩容时会调用到:  ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
         * @param map 代表要扩容的HashMap
         * @param tab 代表新创建的数组
         * @param index 代表旧数组的索引
         * @param bit 代表旧数组的长度，需要配合使用来做按位与运算
         */
        final void split(HashMap8<K, V> map, Node<K, V>[] tab, int index, int bit) {
            // 做个赋值，因为这里是((TreeNode<K,V>)e)这个对象调用split()方法，所以this就是指(TreeNode<K,V>)e对象，所以才能类型对应赋值
            TreeNode<K, V> b = this;
            // 设置低位首节点和低位尾节点
            TreeNode<K, V> loHead = null, loTail = null;
            // 设置高位首节点和高位尾节点
            TreeNode<K, V> hiHead = null, hiTail = null;
            // 定义两个变量lc和hc，初始值为0，后面比较要用，他们的大小决定了红黑树是否要转回链表
            int lc = 0, hc = 0;
            for (TreeNode<K, V> e = b, next; e != null; e = next) {
                next = (TreeNode<K, V>) e.next;
                e.next = null;
                // 以下的操作就是做个按位与运算，按照结果拉出两条链表
                if ((e.hash & bit) == 0) {
                    if ((e.prev = loTail) == null)
                        loHead = e;
                    else
                        loTail.next = e;
                    loTail = e;
                    ++lc;// 做个计数，看下拉出低位链表下会有几个元素
                } else {
                    if ((e.prev = hiTail) == null)
                        hiHead = e;
                    else
                        hiTail.next = e;
                    hiTail = e;
                    ++hc;// 做个计数，看下拉出高位链表下会有几个元素
                }
            }
            // 如果低位链表首节点不为null，说明有这个链表存在
            if (loHead != null) {
                // 如果loHead上元素个数小于等于6
                if (lc <= UNTREEIFY_THRESHOLD)
                  //那就从红黑树转链表了，低位链表，迁移到新数组中下标不变，还是等于原数组到下标
                    tab[index] = loHead.untreeify(map);
                else {
                  //低位链表，迁移到新数组中下标不变，还是等于原数组到下标，把低位链表整个拉到这个下标下
                    tab[index] = loHead;
                  //如果高位首节点不为空，说明原来的红黑树已经被拆分了
                    if (hiHead != null) 
                      //那么低位就需要构建新的红黑树了
                        loHead.treeify(tab);
                }
            }
            // 如果高位链表首节点不为null，说明有这个链表存在
            if (hiHead != null) {
              //如果链表下的元素小于等于6
                if (hc <= UNTREEIFY_THRESHOLD)
                  //那就从红黑树转链表了，高位链表，迁移到新数组中的下标=【旧数组+旧数组长度】
                    tab[index + bit] = hiHead.untreeify(map);
                else {
                    tab[index + bit] = hiHead;
                    if (loHead != null)
                        hiHead.treeify(tab);
                }
            }
        }
        /**
         * 红黑树退化成链表
         */
        final Node<K, V> untreeify(HashMap8<K, V> map) {
            Node<K,V> hd = null, tl = null;
          //循环，将红黑树转成链表
            for (Node<K,V> q = this; q != null; q = q.next) {
              //构造一个普通链表节点
                Node<K,V> p = map.replacementNode(q, null);
                if (tl == null)
                    hd = p;
                else
                    tl.next = p;
                tl = p;
            }
            return hd;
        }

        final void treeify(Node<K, V>[] tab) {

        }
    }
    
    Node<K,V> replacementNode(Node<K,V> p, Node<K,V> next) {
        return new Node<>(p.hash, p.key, p.value, next);
    }
}
