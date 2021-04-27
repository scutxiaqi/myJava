package com.xiaqi.myJava.java.lambda;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Stream {
    public static void main(String[] args) {
        Random r = new Random();
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < 10; i++) {
            list.add(r.nextInt(10));
        }
        list.stream().filter(h -> h > 5).forEach(h -> System.out.print(h + " "));
    }
}
