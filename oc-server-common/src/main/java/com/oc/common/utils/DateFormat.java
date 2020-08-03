package com.oc.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期操作
 * @author chuangyeifang
 */
public class DateFormat {

    public static final String YMD_FORMAT = "yyyy-MM-dd";

    public static final String HMS_FORMAT = "HH:mm:ss";

    public static final String HM_FORMAT = "HH:mm";

    public static final String YMD_HMS_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private DateFormat() { }

    public static Date parse(String str, String pattern) throws ParseException {
        return getFormat(pattern).parse(str);
    }

    public static String parse(Date date, String pattern) {
        return getFormat(pattern).format(date);
    }

    private static SimpleDateFormat getFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }
}
