package com.xiaqi.myJava.designpattern.factory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 简单工厂模式.又叫静态工厂方法模式<br>
 * 原理：根据入参作 if 分支判断，以创建不同的对象
 *
 */
public class SimpleFactory {
    public List<String> getList(String type) {
        if (type.equals("array")) {
            return new ArrayList<String>();
        } else if (type.equals("linked")) {
            return new LinkedList<String>();
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        SimpleFactory factory = new SimpleFactory();
        List<String> list = factory.getList("array");
        list.toString();
    }
}
