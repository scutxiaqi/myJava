package com.xiaqi.myJava.java.base.string;

public class StringDemo {
    public static String str = "1234";
    public static void main(String[] args) {
        System.out.println(StringDemo.str);
    }

    public static void ddd() {
        String s = new String("1");
        s.intern();
        String s2 = "1";
        System.out.println(s == s2);// false
    }
}
