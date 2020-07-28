package com.xiaqi.myJava.java.annotation;

import java.lang.reflect.Field;

/**
 * 注解处理器
 */
public class Handler {
	public static void main(String[] args) {
		Field[] fields = Apple.class.getDeclaredFields();
		for(Field field : fields){
			if(field.isAnnotationPresent(FruitName.class)){
				FruitName fruitName = field.getAnnotation(FruitName.class);
				System.out.println(fruitName.value());
			}
			if(field.isAnnotationPresent(FruitColor.class)){
				FruitColor color = field.getAnnotation(FruitColor.class);
				System.out.println(color.fruitColor());
			}
			if(field.isAnnotationPresent(FruitProvider.class)){
				FruitProvider provider = field.getAnnotation(FruitProvider.class);
				System.out.println(provider.name());
				System.out.println(provider.address());
			}
		}
	}
}
