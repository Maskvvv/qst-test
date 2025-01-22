package com.zhy.excel.venue;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhouhongyin
 * @since 2022/5/23 10:38
 */
//@SpringBootTest
class VenueTest {


    @Test
    void mailType() {
        ExcelReader libReader = ExcelUtil.getReader("C:\\Users\\lenovo\\Desktop\\1.xlsx", 0);

        String sql = "INSERT INTO `pn_venue` (`name`, `address`, `avatar`, `cityId`, `regionId`, `latitude`, `longitude`, `latitudeBd`, `longitudeBd`, `phones`, `addTime`, `updateTime`, `latitude02`, `longitude02`, `parking`, `transportation`, `desc`, `status`, `operator`) VALUES ( '%s', '%s', NULL, %s, 0, 13.745443, 100.525915, NULL, NULL, '', '2024-11-05 10:26:36', '2024-11-05 10:26:36', NULL, NULL, NULL, NULL, '', 0, 'hongyin.zhou@ipiaoniu.com');";

        libReader.read().stream().forEach(row -> {

            System.out.printf(sql, row.get(0), row.get(1), row.get(2));

            System.out.println();
            System.out.println();

        });

    }

    @Test
    void test() {
        StringBuilder sb = new StringBuilder();
        List<String> readAll = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            sb.append(String.format("交易ID%s：%s 数量：%s\n", i, 121, 3434));
        }
        readAll.add(sb.toString());

        // 通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter("C:\\Users\\lenovo\\Desktop\\2.xlsx");
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(readAll);
        // 关闭writer，释放内存
        writer.close();


    }




}
