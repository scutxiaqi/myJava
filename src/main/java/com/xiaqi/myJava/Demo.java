package com.xiaqi.myJava;

import java.util.HashMap;
import java.util.Map;

public class Demo {

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(null, "hahaha");
        System.out.println(System.currentTimeMillis());
    }

}
