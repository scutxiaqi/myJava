package com.xiaqi.myJava.java.base;

/**
 * java1.8新特性——在interface中写实现方法
 */
public class InterfaceDemo implements Interface8 {

    public static void main(String[] args) {
        InterfaceDemo item = new InterfaceDemo();
        int result = item.multipute(1, 2);
        System.out.println(result);
    }

}

/**
 * interface中的的方法实现用default 修饰之后就可以了，子类就可以直接使用了
 */
interface Interface8 {
    default int multipute(int i, int j) {
        return i * j;
    }
}
