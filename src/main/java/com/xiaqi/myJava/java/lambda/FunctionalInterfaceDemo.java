package com.xiaqi.myJava.java.lambda;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 函数式接口: 是一个接口，这个接口里只有一个抽象方法。<br>
 * java.util.function包下提供了若干函数式接口，下面举例说明。
 *
 */
public class FunctionalInterfaceDemo {
    public static void main(String[] args) {
        List<String> list = new ArrayList<String>();
        list.add("aaa");
        list.add("bbb");
        list.forEach(s -> s.toUpperCase());
        System.out.println(list);
    }

    /**
     * Consumer类有一个入参，无返回值
     */
    public void testConsumer() {
        Consumer<String> c = new Consumer<String>() {
            @Override
            public void accept(String o) {
                System.out.println(o);
            }
        };
    }
}
