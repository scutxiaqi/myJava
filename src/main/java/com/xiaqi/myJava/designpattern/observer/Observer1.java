package com.xiaqi.myJava.designpattern.observer;

import java.util.Observable;
import java.util.Observer;

/**
 * 观察者1
 */
public class Observer1 implements Observer{
	private Observable observable;

	public Observer1(Observable observable) {
		this.observable = observable;
		observable.addObserver(this);
	}

	public void update(Observable obs, Object arg) {
		if (obs instanceof Subject) {
			Subject observable = (Subject) obs;
			display();
		}
	}

	public void display() {
		System.out.println("有新数据了");
	}
	
	public static void main(String[] args) {
		Subject observable = new Subject();
		Observer observer = new Observer1(observable);
		observable.setMeasurements(11, 11, 11);
	}
}
