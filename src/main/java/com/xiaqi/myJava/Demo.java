package com.xiaqi.myJava;

public class Demo {

    public static void main(String[] args) {
        try {
            Object xx = 1;
            String yy = (String) xx;
        } catch (Exception e) {
            throw e;
        } finally {
            System.out.print("finally");
        }
    }

}
