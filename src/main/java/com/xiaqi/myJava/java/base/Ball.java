package com.xiaqi.myJava.java.base;

interface Playable {
	void play();
}

interface Bounceable {
	void play();
}

interface Rollable extends Playable, Bounceable {
	Ball ball = new Ball("al");
}

public class Ball implements Rollable {
	private String name;	
	public String getName(){
		return name;
	}
	
	public Ball(String name){
		this.name=name;
	}

	public void play() {
		//ball = new Ball("Football");
		System.out.println(ball.getName());
	}
}
