package com.xiaqi.myJava.java.string;

/**
 * StringBuilder是非线程安全的可变字符序列，底层基于char类型数组<br>
 * 主要实现在父类里面。
 */
public class StringBuilder8 extends AbstractStringBuilder8 {
    public StringBuilder8() {
        super(16);
    }

    public StringBuilder8(String str) {
        super(str.length() + 16);
        append(str);
    }
    
    @Override
    public StringBuilder8 append(String str) {
        super.append(str);
        return this;
    }
}
