package com.xiaqi.myJava.designpattern.observer;

import java.util.Observable;

/**
 * 被观察对象
 */
// 这是一个气象监测站，每当监测到数据，通知观察者
public class Subject extends Observable {
    private float temperature;// 温度
    private float humidity;// 湿度
    private float pressure;// 气压

    /**
     * 重新设定了测量值
     */
    public void setMeasurements(float temperature, float humidity, float pressure) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        setChanged(); // 设定改变了
        notifyObservers(); // 通知所有的观察者
    }

    public float getTemperature() {
        return temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getPressure() {
        return pressure;
    }
}
