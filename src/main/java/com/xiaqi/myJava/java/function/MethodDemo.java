package com.xiaqi.myJava.java.function;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 方法引用写法：如果函数式接口的实现恰好可以通过调用一个方法来实现，那么我们可以使用方法引用
 */
public class MethodDemo {
    public static void main(String[] args) {
        // 静态方法引用--通过类名调用
        Consumer<String> consumerStatic = Java3y::MyNameStatic;
        consumerStatic.accept("3y---static");

        // 实例方法引用--通过实例调用
        Java3y java3y = new Java3y();
        Consumer<String> consumer = java3y::myName;
        consumer.accept("3y---instance");

        // 构造方法方法引用--无参数
        Supplier<Java3y> supplier = Java3y::new;
        System.out.println(supplier.get());
    }
}

class Java3y {
    // 静态方法
    public static void MyNameStatic(String name) {
        System.out.println(name);
    }

    // 实例方法
    public void myName(String name) {
        System.out.println(name);
    }

    // 无参构造方法
    public Java3y() {
        //System.out.println("haha");
    }
}