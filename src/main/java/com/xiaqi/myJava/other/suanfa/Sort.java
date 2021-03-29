package com.xiaqi.myJava.other.suanfa;

import java.util.Arrays;

/**
 * 排序
 * 
 * @author xiaqi
 *
 */
public class Sort {
	public static void main(String[] args) {
		int a[] = { 49, 38, 65, 12, 4, 62, 99 };
		int[] result = bubble(a);
		System.out.println(Arrays.toString(result));
	}

	/**
	 * 冒泡排序
	 * 
	 * @param a
	 * @return
	 */
	public static int[] bubble(int[] a) {
		int temp=0;
		for (int i = 0; i < a.length - 1; i++)
			for (int j = 0; j < a.length - i - 1; j++) {
				if (a[j] > a[j + 1]) {
					temp = a[j];
					a[j] = a[j + 1];
					a[j + 1] = temp;
				}
			}
		return a;
	}
}
