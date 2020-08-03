package com.oc.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author chuangyeifang
 */
@Slf4j
public class DateUtils {

    /**
     * 判断指定时间是否在区间内(注意开始结束时间位置)
     * @param startTime 开始时间 (HH:mm:ss)
     * @param endTime   结束时间 (HH:mm:ss)
     * @return
     */
    public static boolean isTimeInZone(String startTime, String endTime){
        try {
            int cur = toSeconds(DateFormat.parse(new Date(), DateFormat.HMS_FORMAT));
            int start = toSeconds(startTime);
            int end = toSeconds(endTime);
            if (start <= end) {
                if (cur >= start || cur <= end) {
                    return true;
                }
            } else {
                if (cur >= start && cur <= end){
                    return true;
                }
            }
        } catch (ParseException e) {
           log.error("日期解析异常", e);
        }
        return false;
    }

    public static String check(String str) {
        String regex = "([0-1]?[0-9]|2[0-4]):([0-5][0-9]):([0-5][0-9])$";
        if (!Pattern.matches(regex, str)) {
            return null;
        }
        return str;
    }

    /**
     * 将时间 （H:M:S） 转化为秒
     * @param timeStr
     * @return
     */
    private static int toSeconds(String timeStr) throws ParseException {
        String[] array = timeStr.split(":");
        if (array.length == 3) {
            return Integer.parseInt(array[0]) * 3600
                    + Integer.parseInt(array[1]) * 60
                    + Integer.parseInt(array[2]);
        }
        throw new ParseException("日期解析失败", 0);
    }
}
