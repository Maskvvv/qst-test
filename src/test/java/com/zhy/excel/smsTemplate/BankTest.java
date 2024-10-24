package com.zhy.excel.smsTemplate;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhouhongyin
 * @since 2022/5/23 10:38
 */
//@SpringBootTest
class BankTest {


    @Test
    void mailType() {
        ExcelReader libReader = ExcelUtil.getReader("D:\\资料\\票牛\\需求文档\\港澳台计划1.0\\商家端\\香港银行.xlsx", 0);

        String sql = "INSERT INTO `pndb_beta`.`pn_bank` ( `id`, `name`, `nameEn`, `status`, `addTime`, `updateTime`, `pic`, `weight` )\n" +
                "VALUES\n" +
                "\t( %s, '%s', '%s', 1, '2024-04-10 01:43:11', '2024-04-10 01:43:11', NULL, 1000 );";


        AtomicInteger i = new AtomicInteger(1);
        libReader.read().stream().forEach(row -> {

            if (row.get(2) == null || StrUtil.isBlank(row.get(2).toString())) {
                return;
            }

            System.out.printf(sql, i.getAndIncrement() + "", row.get(2), row.get(1));

            System.out.println();
            System.out.println();

        });

    }

}
