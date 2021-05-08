package com.xiaqi.myJava.other;

import com.xiaqi.myJava.java.annotation.Person;

public class Demo {

    public static void main(String[] args) throws CloneNotSupportedException {
        Person baobao = new Person("baobao", 20);
        Person baobao2 = (Person) baobao.clone();
        System.out.print(baobao2.getName());
    }

}
