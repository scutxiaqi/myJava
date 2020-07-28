package com.xiaqi.myJava.java.collection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * for循环、foreach循环比较
 */
public class ListForeach {
	public static void main(String[] args) {
		List<Integer> arrayList = new ArrayList<Integer>();
        List<Integer> linkedList = new LinkedList<Integer>();
        for (int i = 0; i < 100000; i++){
            arrayList.add(i);
            linkedList.add(i);
        }
        //listFor(arrayList);//速度快，在毫秒级
       // listFor(linkedList);//速度慢，在10秒数量级
        listForeach(arrayList);//速度快，在毫秒级
        listForeach(linkedList);//速度快，在毫秒级
	}
	/**
	 * for循环
	 */
	public static void listFor(List<Integer> list){
		long startTime = System.currentTimeMillis();
        for (int i = 0; i < list.size(); i++){
        	list.get(i);
        }
        System.out.println("遍历速度：" + (System.currentTimeMillis() - startTime) + "ms");
	}
	
	/**
	 * foreach循环
	 */
	public static void listForeach(List<Integer> list){
		long startTime = System.currentTimeMillis();
        for (Integer i : list){
        	
        }
        System.out.println("遍历速度：" + (System.currentTimeMillis() - startTime) + "ms");
	}
}
