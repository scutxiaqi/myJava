package com.xiaqi.myJava.java.lambda.api;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collector;

public interface Stream8<T> {
    /**
     * 去重，根据equals方法比较元素是否重复
     * 
     * @return
     */
    Stream8<T> distinct();

    /**
     * 过滤，去掉不符合的元素
     * 
     * @param predicate
     * @return
     */
    Stream8<T> filter(Predicate<? super T> predicate);

    /**
     * 截断流，长度为maxSize
     * 
     * @param maxSize
     * @return
     */
    Stream8<T> limit(long maxSize);

    /**
     * 转换，把T类型的流转换为R类型的流
     * 
     * @param mapper
     * @return
     */
    <R> Stream8<R> map(Function<? super T, ? extends R> mapper);

    /**
     * 从n开始返回此流
     * 
     * @param n 要跳过的元素数量
     * @return
     */
    Stream8<T> skip(long n);

    // ------ 终端操作 --------/
    <R, A> R collect(Collector<? super T, A, R> collector);
   
}
