package com.xiaqi.myJava.designpattern.observer;

import java.util.Observable;
import java.util.Observer;

/**
 * 观察者
 */
public class RealObserver implements Observer, IObserver {
	Observable observable;
	private float temperature;
	private float humidity;

	public RealObserver(Observable observable) {
		this.observable = observable;
		observable.addObserver(this);
	}

	public void update(Observable obs, Object arg) {
		if (obs instanceof RealObservable) {
			RealObservable observable = (RealObservable) obs;
			this.temperature = observable.getTemperature();
			this.humidity = observable.getHumidity();
			display();
		}
	}

	public void display() {
		System.out.println("Current conditions: " + temperature + "F degrees and " + humidity + "% humidity");
	}
	
	public static void main(String[] args) {
		RealObservable observable = new RealObservable();
		Observer observer = new RealObserver(observable);
		observable.setMeasurements(11, 11, 11);
				
	}
}
