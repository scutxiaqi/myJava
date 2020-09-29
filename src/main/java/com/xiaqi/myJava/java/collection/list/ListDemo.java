package com.xiaqi.myJava.java.collection.list;

import java.util.ArrayList;
import java.util.List;

public class ListDemo {
    public static void main(String[] args) {
        List<String> list = new ArrayList<String>();
        list.add("aaa");
        list.add("bbb");
        //list.stream()
    }

    public static void forEach(List<String> list) {
        list.forEach(item -> {
            System.out.println(item);
        });
    }
}
