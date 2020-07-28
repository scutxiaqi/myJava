package com.xiaqi.myJava.designpattern.proxy;

/**
 * 代理对象
 */
public class ProxyServiceImpl implements Service {
    private Service service;

    public ProxyServiceImpl(Service service) {
        this.service = service;
    }

    @Override
    public void doSomething() {
        //before 可以做一些事情，如：记录log
        service.doSomething();// 切面
        //after 可以做一些事情，如：记录log
    }

}
