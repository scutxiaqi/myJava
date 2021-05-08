package com.xiaqi.myJava.java.lambda;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.Data;

/**
 * 函数式接口: 是一个接口，这个接口里只有一个抽象方法。<br>
 * java.util.function包下提供了若干函数式接口，下面举例说明。
 *
 */
public class FunctionalInterfaceDemo {
    public static void main(String[] args) {
        List<Product> list = new ArrayList<>();
        list.add(new Product("茅台", 2500));
        list.add(new Product("五粮液", 1099));
        list.add(new Product("剑南春", 699));
        System.out.println(list);
        testFunction(list);
        testPredicate(list);
        //testConsumer(list);
    }
    
    /**
     * Predicate 用于测试输入值是否满足条件
     * @param list
     */
    public static void testPredicate(List<Product> list) {
        Predicate<Product> p = new Predicate<Product>() {
            @Override
            public boolean test(Product t) {
                return t.getPrice() > 1000; // 选出高端酒
            }
        };
        List<Product> result = list.stream().filter(p).collect(Collectors.toList()); // 选出高端酒, 传统方式
        System.out.println(result);
        result = list.stream().filter(each -> each.getPrice() > 1000).collect(Collectors.toList()); // 选出高端酒，Lambda方式
        System.out.println(result);
    }

    /**
     * Consumer 有一个入参，无返回值
     */
    public static void testConsumer(List<Product> list) {
        Consumer<Product> c = new Consumer<Product>() {
            @Override
            public void accept(Product o) {
                o.setName("沱牌");
                o.setPrice(50);
            }
        };
        list.forEach(c);
        System.out.println(list);
        list.forEach(each -> each.setName("舍得"));
        System.out.println(list);
    }
    
    /**
     * Function 接收T对象，返回R对象。用于类型转换
     * @param list
     */
    public static void testFunction(List<Product> list) {
        Function<Product, String> f= new Function<Product, String>() {
            @Override
            public String apply(Product t) {
                return t.getName();
            }
        };
        List<String> result = list.stream().map(f).collect(Collectors.toList()); // 输出name List, 传统方式
        System.out.println(result);
        result = list.stream().map(each -> each.getName()).collect(Collectors.toList()); // 输出name List, Lambda方式
        System.out.println(result);
        result = list.stream().map(Product::getName).collect(Collectors.toList()); // 调用对象的某个方法时，可以进一步简化
        System.out.println(result);
    }
}

@Data
class Product {
    private String name;
    private int price;
    
    public Product(String name, int price) {
        this.name = name;
        this.price = price;
    }
}
