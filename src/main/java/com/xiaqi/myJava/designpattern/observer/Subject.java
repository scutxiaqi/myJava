package com.xiaqi.myJava.designpattern.observer;

import java.util.Observable;

/**
 * 被观察对象
 */
public class Subject extends Observable {
    /**
     * 观察者模式：
     */
    public static void main(String[] args) {
        Subject subject = new Subject();
        subject.addObserver(new Observer1());// 添加观察者，可以添加多个
        subject.setChanged();// 被观察对象有变动了
        subject.notifyObservers("love xiaqi");// 通知观察者
      }
}
