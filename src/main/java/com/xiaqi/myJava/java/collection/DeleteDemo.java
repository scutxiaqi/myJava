package com.xiaqi.myJava.java.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DeleteDemo {
	public static void main(String[] args) {
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		list.add(2);
		list.add(3);
		list.add(4);
		forDelete(list);
		// iteratorDelete(list);
	}

	/**
	 * for循环有可能漏删或不能完全的删除
	 * 1.最后输出=[1, 2, 3, 4] 只删除了一个2，还有一个没有完全删除
	 * 2.删除了第一个2后，集合里的元素个数减1，后面的元素往前移了1位，此时，第二个2已经移到了索引index=1的位置，而此时i马上i++了，list.
	 * get(i)获得的是数据3。
	 * 
	 * @param list
	 */
	public static void forDelete(List<Integer> list) {
		for (int i = 0; i < list.size(); i++) {
			if (2 == list.get(i)) {
				list.remove(i);
			}
		}
		System.out.println("最后输出=" + list.toString());
	}

	/**
	 * 1、每调用一次iterator.next()方法，只能调用一次remove()方法。
	 * 2、调用remove()方法前，必须调用过一次next()方法。
	 * 
	 * @param list
	 */
	public static void iteratorDelete(List<Integer> list) {
		Iterator<Integer> it = list.iterator();
		while (it.hasNext()) {
			Integer item = it.next();
			if (2 == item) {
				it.remove();
			}
		}
		System.out.println("最后输出=" + list.toString());
	}

}
