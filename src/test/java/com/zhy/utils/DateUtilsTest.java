package com.zhy.utils;

import cn.hutool.core.date.DateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author zhouhongyin
 * @since 2022/5/23 10:38
 */
@SpringBootTest
class DateUtilsTest {

    @Test
    void differentDaysByMillisecond() {
    }

    @Test
    void testDifferentDaysByMillisecond() {

        System.out.println(DateUtils.differentDaysByMillisecond(1665849599000L, 1665849659000L));
    }

    @Test
    void dateFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");

        String dateNormal = sdf.format(new Date(1665849599000L));
        System.out.println(dateNormal);
    }
}
