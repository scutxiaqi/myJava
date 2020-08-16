package com.xiaqi.myJava.java.base.time;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Locale;

import com.xiaqi.myJava.java.base.time.FieldPosition8.FieldDelegate8;

/**
 * 线程不安全：DateFormat有一个共享成员变量calendar，format(date)方法参数传入的日期是交由变量calendar来储存的，此处线程不安全
 *
 */
public class SimpleDateFormat8 extends DateFormat {
    private static final long serialVersionUID = -9024017405404616315L;
    private DateFormatSymbols formatData;
    private String pattern;
    private Locale locale;
    
    transient private char[] compiledPattern;
    
    private final static int TAG_QUOTE_ASCII_CHAR       = 100;
    private final static int TAG_QUOTE_CHARS            = 101;

    public SimpleDateFormat8(String pattern, Locale locale) {
        if (pattern == null || locale == null) {
            throw new NullPointerException();
        }

        initializeCalendar(locale);
        this.pattern = pattern;
        // this.formatData = DateFormatSymbols.getInstanceRef(locale);
        this.locale = locale;
        initialize(locale);
    }

    private void initialize(Locale loc) {

    }

    private void initializeCalendar(Locale loc) {

    }

    @Override
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos) {
        // pos.beginIndex = pos.endIndex = 0;
        return null;
    }

    private StringBuffer format(Date date, StringBuffer toAppendTo, FieldDelegate8 delegate) {
        calendar.setTime(date);

        boolean useDateFormatSymbols = useDateFormatSymbols();

        for (int i = 0; i < compiledPattern.length;) {
            int tag = compiledPattern[i] >>> 8;
            int count = compiledPattern[i++] & 0xff;
            if (count == 255) {
                count = compiledPattern[i++] << 16;
                count |= compiledPattern[i++];
            }

            switch (tag) {
            case TAG_QUOTE_ASCII_CHAR:
                toAppendTo.append((char) count);
                break;

            case TAG_QUOTE_CHARS:
                toAppendTo.append(compiledPattern, i, count);
                i += count;
                break;

            default:
                //subFormat(tag, count, delegate, toAppendTo, useDateFormatSymbols);
                break;
            }
        }
        return toAppendTo;
    }

    @Override
    public Date parse(String source, ParsePosition pos) {
        // TODO Auto-generated method stub
        return null;
    }
    
    private boolean useDateFormatSymbols() {
        return true;
    }
}

class FieldPosition8{
    interface FieldDelegate8 {
        
    }
}
