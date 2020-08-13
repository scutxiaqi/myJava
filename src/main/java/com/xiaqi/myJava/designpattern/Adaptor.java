package com.xiaqi.myJava.designpattern;

/**
 * 适配器模式: 将接口转换成想要的另外一个接口
 */
//适配器
public class Adaptor{
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

// 老版本，接口不好用
class Adaptee {
    public void fa() {
    }

    public void fb() {
    }

    public void fc() {
    }
}