package com.xiaqi.myJava.java.base;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import lombok.Data;

/**
 * 反射用法
 */
public class Reflection {
    public static void main(String[] args) throws Exception {
        User user = (User) Reflection.getInstance("com.xiaqi.myJava.java.base.User");
        Reflection.field(user);
        System.out.println(user.getName());
    }

    /**
     * 创建对象
     */
    public static Object getInstance(String className) throws Exception {
        return Class.forName(className).newInstance();
    }

    /**
     * 调用方法
     */
    public static void method(User user) throws Exception {
        Method method = User.class.getMethod("setName", String.class);
        method.invoke(user, "love");
    }
    
    /**
     * 访问类的属性
     */
    public static void field(User user) throws Exception {
        Field field = User.class.getDeclaredField("name");
        field.setAccessible(true);// 值为true 则指示field可以操作private属性
        field.set(user, "sex");
    }
}

@Data
class User {
    private String name;
}
