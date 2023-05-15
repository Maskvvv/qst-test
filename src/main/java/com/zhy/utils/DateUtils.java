package com.zhy.utils;

import java.util.Date;

/**
 * @author zhouhongyin
 * @since 2022/5/23 10:37
 */
public class DateUtils {

    /**
     * 通过秒毫秒数判断两个时间的间隔的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(Date date1, Date date2) {
        return (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
    }

    /**
     * 通过秒毫秒数判断两个时间的间隔的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(Long date1, Long date2) {
        return (int) ((date2 - date1) / (1000 * 3600 * 24));
    }

}
