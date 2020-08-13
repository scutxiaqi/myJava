package com.xiaqi.myJava.designpattern.observer;

import java.util.Observable;

/**
 * 被观察对象（主题Subject）（监听器Listener）
 */
public class Subject extends Observable {
    /**
     * 观察者模式：观察者注册自己感兴趣的主题，当主题有变化时，通知观察者<br>
     * 发布订阅模式：订阅者注册自己感兴趣的消息主题，发布者广播消息到中间连接点，订阅者从对应中间连接点获取消息。发布订阅模式是异步非阻塞模式<br>
     * 两者异同：发布订阅模式多了一个中间连接点
     */
    public static void main(String[] args) {
        Subject subject = new Subject();
        subject.addObserver(new Observer1());// 添加观察者，可以添加多个
        subject.setChanged();// 被观察对象有变动了
        subject.notifyObservers("love xiaqi");// 通知观察者
      }
}
