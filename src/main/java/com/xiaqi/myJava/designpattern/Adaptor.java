package com.xiaqi.myJava.designpattern;

/**
 * 适配器模式: 主要用于做适配，将不兼容的接口转换为可兼容的接口，让原本由于接口不兼容而不能一起工作的类可以一起工作
 */
//新版本实现类
public class Adaptor implements ITarget {
    private Adaptee adaptee;

    public Adaptor(Adaptee adaptee) {
        this.adaptee = adaptee;
    }

    public void f1() {
        adaptee.fa(); // 委托给Adaptee
    }

    public void f2() {
        // ...重新实现f2()...
    }

    public void fc() {
        adaptee.fc();// 委托给Adaptee
    }
}

//对象适配器：基于组合
interface ITarget {
    void f1();

    void f2();

    void fc();
}

// 老版本
class Adaptee {
    public void fa() {
    }

    public void fb() {
    }

    public void fc() {
    }
}