package com.xiaqi.myJava.designpattern.proxy;

/**
 * 真实的实现类
 */
public class ServiceImpl implements Service {
    @Override
    public void doSomething() {
        System.out.println("real doSomething()");
    }
}
