package com.xiaqi.myJava;

import java.math.BigDecimal;

public class Demo {

    public static void main(String[] args) {
        BigDecimal bb= new BigDecimal(0.0000);
        System.out.println(bb.compareTo(BigDecimal.ZERO));
    }

}
