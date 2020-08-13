package com.xiaqi.myJava.java.base;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class SimpleDateFormat8 {
    public static void main(String[] args){
        Instant now = Instant.now();
        System.out.println(now);
    }
    
    public static void simpleDateFormatDemo() {
        String strDateFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        System.out.println(sdf.format(new Date()));
    }
    
    public static void dateTimeFormatterDemo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime zdt = LocalDateTime.now();
        System.out.println(formatter.format(zdt));
    }
}
