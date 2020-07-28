package com.xiaqi.myJava.designpattern.proxy;

/**
 * 静态代理模式例子
 *
 */
public class StaticProxyMain {
    public static void main(String[] args) {
        Service service = new ServiceImpl();
        Service proxyService = new ProxyServiceImpl(service);
        proxyService.doSomething();
    }
}
