package com.xiaqi.myJava.java.function;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 函数式接口只有一个抽象方法。<br>
 * 使用函数式接口时，并不关心接口名，方法名，参数名。我们只关注他的参数类型，参数个数，返回值<br>
 * java function包提供了若干函数式接口供使用
 */
public class FunctionalInterfaceDemo {
    public static void main(String[] args) {
        hhhDemo();
    }
    
    /**
     * 方法引用写法：如果函数式接口的实现恰好可以通过调用一个方法来实现，那么我们可以使用方法引用
     */
    public static void hhhDemo() {
        Consumer<String> consumer = System.out::println;
        consumer.accept("方法引用写法");
    }
    
    /**
     * Consumer接口：一个入参，无返回值
     */
    public static void consumerDemo() {
        Consumer<String> consumer = s-> System.out.println(s);
        consumer.accept("consumer");
    }
    
    /**
     * Supplier接口：无入参，有返回值
     */
    public static void supplierDemo() {
        Supplier<String> supplier = () -> "supplier";
        String s = supplier.get();
        System.out.println(s);
    }
    
    /**
     * Function接口：一个入参，有返回值
     */
    public static void functionDemo() {
        Function<String,String> function = name -> {return name + " hello!";};
        String str = function.apply("xiaqi");
        System.out.println(str);
    }
}
