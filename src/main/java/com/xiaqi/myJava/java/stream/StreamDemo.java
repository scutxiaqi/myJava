package com.xiaqi.myJava.java.stream;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamDemo {
    public static void main(String[] args) {
        
    }

    public void map() {
        Stream.of("  Apple ", " pear ", " ORANGE", " BaNaNa ").map(String::trim) // 去空格
                .map(String::toLowerCase) // 变小写
                .forEach(System.out::println); // 打印
    }
    
    public void list() {
        Stream<String> stream = Stream.of("Apple", "", null, "Pear", "  ", "Orange");
        List<String> list = stream.filter(s -> s != null && !s.isEmpty()).collect(Collectors.toList());
        System.out.println(list);
    }
    
    /**
     * 输出为数组
     */
    public void array() {
        String[] array = Stream.of("Apple", "Banana", "Orange").toArray(String[]::new);
        System.out.println(Arrays.toString(array));
    }
}
