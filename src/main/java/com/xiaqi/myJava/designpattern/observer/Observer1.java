package com.xiaqi.myJava.designpattern.observer;

import java.util.Observable;
import java.util.Observer;

/**
 * 观察者1
 */
public class Observer1 implements Observer{
	public void update(Observable obs, Object arg) {
	    System.out.println(arg);
	}
}
