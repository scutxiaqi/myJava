package com.xiaqi.myJava.java.collection;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;



public class Demo {

	public static void main(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(null, null);
		Hashtable<String, String> table = new Hashtable<String, String>();
		table.put("111", null);
	}

}
