package com.xiaqi.myJava.java.collection.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Demo {

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("111", "111");
        map.put("222", "222");
        for (Iterator<String> it = map.keySet().iterator(); it.hasNext(); ) {
            it.next();
            it.remove();
        }
        System.out.println(map);
    }

}
