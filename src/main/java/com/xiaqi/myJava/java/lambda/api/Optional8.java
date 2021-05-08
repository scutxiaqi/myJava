package com.xiaqi.myJava.java.lambda.api;

import java.util.NoSuchElementException;

public final class Optional8<T> {
    private final T value;

    private Optional8() {
        this.value = null;
    }

    /**
     * 返回实际的值，如果为null，抛出 NoSuchElementException
     * 
     * @return
     */
    public T get() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }
}
