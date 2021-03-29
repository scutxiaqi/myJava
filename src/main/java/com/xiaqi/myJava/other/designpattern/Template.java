package com.xiaqi.myJava.other.designpattern;

/**
 * 模板方法模式：需要定义一个算法框架模板，将某些步骤推迟到子类中实现（让子类在不改变算法整体结构的情况下，重新定义算法中的某些步骤）
 *
 */
public class Template extends AbstractClass {
    @Override
    protected void method1() {

    }

    @Override
    protected void method2() {

    }

}

abstract class AbstractClass {
    public final void templateMethod() {
        // ...
        method1();
        // ...
        method2();
        // ...
    }

    protected abstract void method1();

    protected abstract void method2();
}
