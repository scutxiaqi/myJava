package com.xiaqi.myJava.java.collection.list;

import java.util.ArrayList;
import java.util.List;

public class ListDemo {
    public static void main(String[] args) {
        List<String> list = new ArrayList<String>();
        list.add(null);
        list.add(null);
        System.out.println(list.size());
        System.out.println(list.isEmpty());
        System.out.println(list);
    }
}
