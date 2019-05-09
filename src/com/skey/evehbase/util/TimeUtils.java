package com.skey.evehbase.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 时间工具
 * <p>
 * Date: 2018/11/20 14:17
 *
 * @author A Lion~
 */
public class TimeUtils {

    private TimeUtils() {
        throw new AssertionError(this + "不应该被实例化！");
    }

    /**
     * 时间解析
     * @param time 20181011093000
     * @return 1539221400
     */
    public static long parse(String time){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        long l = 0;
        try {
            l = format.parse(time).getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return l;
    }

}
