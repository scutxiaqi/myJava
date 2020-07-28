package com.xiaqi.myJava.designpattern.proxy.dynamic;

import java.lang.reflect.Proxy;

import com.xiaqi.myJava.designpattern.proxy.Service;
import com.xiaqi.myJava.designpattern.proxy.ServiceImpl;


/**
 * 动态代理例子
 */
public class DynamicProxyTest {
	public static void main(String args[]) {
	    Service service = new ServiceImpl();
		Service proxy  = (Service) Proxy.newProxyInstance(Service.class.getClassLoader(),
				new Class[] { Service.class }, new ProxyHandler(service));
		proxy.doSomething();
	}

}
