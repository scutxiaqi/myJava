package com.xiaqi.myJava.java.base;

public class Test {
	public static void main(String[] args) {
			System.out.println(getValue());
	}
	
	@SuppressWarnings("finally")
	public static int getValue() {
		try{
			throw new NullPointerException();
            //return 1;
        }catch(Exception e){
            return 2;
        }finally{
            //return 3;
        }
    }
}
