package com.xiaqi.myJava.java.io.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;



/**
 * 序列化
 * 
 * @author xiaqi
 *
 */
public class SerializableDemo {
	public static void main(String[] args) throws Exception {
		/*Circle c = new Circle();
		c.name = "xiaqi";
		c.radius = 1;
		c.color = 255;
		c.type = "aaa";
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("D:\\file.txt"));
		out.writeObject(c);*/
		Circle.type="sss";
		ObjectInputStream in = new ObjectInputStream(new FileInputStream("D:\\file.txt"));
		Circle x = (Circle) in.readObject();
		//System.out.println(c.type);
		System.out.println(x.type);
	}
}

class Shape implements Serializable {//父类也需要实现Serializable接口，否则父类字段不能被序列化
	public String name;
}

class Circle extends Shape implements Serializable {
	public float radius;
	public transient int color;
	public static String type = "Circle";//静态变量不能被序列化

}
