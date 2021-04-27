package com.xiaqi.myJava.java.lambda;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Lambda其实就是匿名方法，这是一种把方法作为参数进行传递的编程思想
 */
public class TestLambda {
    /**
     * 从使用普通方法，匿名类，以及Lambda这几种方式，逐渐的引入Lambda的概念
     */
    public static void main(String[] args) {
        Random r = new Random();
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < 10; i++) {
            list.add(r.nextInt(10));
        }
        System.out.print("0: ");
        filter(list);
        // 匿名类
        FilterTool tool = new FilterTool() {
            @Override
            public boolean filter(Integer number) {
                return number > 5;
            }
        };
        System.out.print("1: ");
        filter(list, tool);
        // 从匿名类演变成Lambda表达式
        // 2.把外面的壳子去掉,只保留方法参数和方法体,参数和方法体之间加上符号 ->
        FilterTool tool2 = (Integer number) -> {
            return number > 5;
        };
        System.out.print("2: ");
        filter(list, tool2);
        // 3.把return和{}去掉
        FilterTool tool3 = (Integer number) -> number > 5;
        System.out.print("3: ");
        filter(list, tool3);
        // 4.把 参数类型和圆括号去掉(只有一个参数的时候，才可以去掉圆括号)
        FilterTool tool4 = number -> number > 5;
        System.out.print("4: ");
        filter(list, tool4);
        // 5.直接把表达式传递进去
        System.out.print("5: ");
        filter(list, number -> number > 5);
        System.out.print("在Lambda表达式中使用静态方法");
        filter(list, n -> TestLambda.myFilter(n) );
        System.out.print("直接引用静态方法");
        filter(list, TestLambda::myFilter);
        System.out.print("只是传递方法的时候，需要一个对象存在");
        TestLambda entity = new TestLambda();
        filter(list, entity::myFilter2);
    }

    /**
     * 筛选出list中大于5的数字
     * 
     * @param list
     */
    public static void filter(List<Integer> list) {
        for (Integer item : list) {
            if (item > 5) {
                System.out.print(item + " ");
            }
        }
        System.out.println();
    }

    public static void filter(List<Integer> list, FilterTool tool) {
        for (Integer item : list) {
            if (tool.filter(item)) {
                System.out.print(item + " ");
            }
        }
        System.out.println();
    }
    
    public static boolean myFilter(Integer number) {
        return number > 5;
    }
    
    public boolean myFilter2(Integer number) {
        return number > 5;
    }
}

interface FilterTool {
    /**
     * 如果number大于5，返回true
     * 
     * @param number 
     */
    public boolean filter(Integer number);
}
