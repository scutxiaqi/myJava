package com.xiaqi.myJava.java.jvm;

/**
 * 常量池
 */
public class ConstantPool {
    public static void main(String[] args) {
        runtimeConstantPool();
    }

    /**
     * 运行时常量池
     */
    public static void runtimeConstantPool() {
        Integer i1 = 40; // 引用常量池中的对象
        Integer i4 = new Integer(40); // new的对象在堆里
        System.out.println(i1.equals(i4));
        System.out.println(i1 == i4);
    }

    /**
     * 字符串常量池
     */
    public static void stringConstantPool() {
        String str3 = new String("2") + new String("2"); // str3引用的对象"22"放在堆里
        str3.intern();// 将对象"22"的引用放入字符串常量池
        String str4 = "22";// str4指向的对象在堆里
        System.out.println(str3.equals(str4));
        System.out.println(str3 == str4);
    }
}
