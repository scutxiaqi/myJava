package com.xiaqi.myJava.java.function;

import java.util.function.Consumer;

public class BaseDemo {
    public static void main(String[] args) {
        consumerDemo();
    }
    
    /**
     * consumer接口的accept方法有一个参数无返回值
     */
    public static void consumerDemo() {
        Consumer<String> consumer = s-> System.out.println(s);
        consumer.accept("consumer");
    }
}
