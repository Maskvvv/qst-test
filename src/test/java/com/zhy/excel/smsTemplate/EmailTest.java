package com.zhy.excel.smsTemplate;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.zhy.model.MailType;
import com.zhy.utils.SchoolExcel;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhouhongyin
 * @since 2022/5/23 10:38
 */
//@SpringBootTest
class EmailTest {


    @Test
    void smsInternational() {
        ExcelReader libReader = ExcelUtil.getReader("D:\\资料\\票牛\\需求文档\\港澳台计划1.0\\短信模板.xlsx", 0);

        String sql = "UPDATE pn_smstype\n" +
                "\tSET `dayuType` = '%s' \n" +
                "WHERE\n" +
                "\t`typeId` = %s;";

        libReader.read().stream().forEach(row -> {


            System.out.printf(sql, row.get(7).toString().trim(), row.get(0));

            System.out.println();
            System.out.println();

        });

    }

    @Test
    void mailType() {
        ExcelReader libReader = ExcelUtil.getReader("D:\\资料\\票牛\\需求文档\\港澳台计划1.0\\短信模板.xlsx", 0);

        String sql = "INSERT INTO `pndb_beta`.`pn_mailtype` ( `typeId`, `rank`, `channel`, `subject`, `template`, `memo`, `addTime` )\n" +
                "VALUES\n" +
                "\t( %s, %s, %s, '%s', '%s', '%s', '%s' );";

        libReader.read().stream().forEach(row -> {


            System.out.printf(sql, row.get(0), row.get(1), 1, "票牛通知", row.get(5), row.get(2), row.get(3));

            System.out.println();
            System.out.println();

        });

    }

}
