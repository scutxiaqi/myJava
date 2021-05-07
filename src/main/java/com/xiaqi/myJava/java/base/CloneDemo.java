package com.xiaqi.myJava.java.base;

import lombok.Data;

/**
 * Object对象有个clone()方法，实现了对象中各个属性的拷贝，它是protected方法。<br>
 * 一个对象要使用clone()方法：<br>
 * 1.类实现Cloneable接口，这是一个标记接口，自身没有方法。（如果没实现，运行时会抛出CloneNotSupportedException）<br>
 * 2.覆写clone()方法，可见性提升为public。
 */
public class CloneDemo {
    public static void main(String[] args) throws CloneNotSupportedException {
        Person baobao = new Person("baobao", 20);
        Person baobao2 = (Person) baobao.clone();
        baobao.toString();
        System.out.print(baobao2.getName());
    }
}

@Data
class Person implements Cloneable {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}