package com.xiaqi.myJava.designpattern.proxy.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * InvocationHandler 调用处理器
 */
public class ProxyHandler implements InvocationHandler {
	private Object proxied;

	public ProxyHandler(Object proxied) {
		this.proxied = proxied;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("开启事物");
		Object obj = method.invoke(proxied, args);// 转调具体目标对象的方法
		System.out.println("提交事物");
		return obj;
	}
}