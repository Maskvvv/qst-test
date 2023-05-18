package com.zhy.excel;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.zhy.utils.DateUtils;
import com.zhy.utils.SchoolExcel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhouhongyin
 * @since 2022/5/23 10:38
 */
//@SpringBootTest
class SchoolTest {


    @Test
    void excel() {
        ExcelReader libReader = ExcelUtil.getReader("D:\\UserFiles\\桌面\\dict_standard_school.xlsx", 0);
        Map<String, SchoolExcel> libSchool = libReader.read().stream().collect(Collectors.toMap(s -> String.valueOf(s.get(1)), s -> {
            SchoolExcel schoolExcel = new SchoolExcel();
            schoolExcel.setCode(String.valueOf(s.get(0)));
            schoolExcel.setName(String.valueOf(s.get(1)).trim());
            return schoolExcel;
        }));


        ExcelReader reader = ExcelUtil.getReader("D:\\UserFiles\\桌面\\高校名单.xlsx", 2);
        List<SchoolExcel> readAll = reader.read().stream().map(s -> {
            SchoolExcel schoolExcel = new SchoolExcel();
            schoolExcel.setSort(String.valueOf(s.get(0)));
            schoolExcel.setName(String.valueOf(s.get(1)));
            schoolExcel.setCode(libSchool.getOrDefault(schoolExcel.getName().trim(), new SchoolExcel()).getCode());
            return schoolExcel;
        }).collect(Collectors.toList());



        // 通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter("D:\\UserFiles\\桌面\\高校名单双一流.xlsx");
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(readAll);
        // 关闭writer，释放内存
        writer.close();

        System.out.println(readAll.get(1));
    }

    @Test
    void sort() {

        ExcelReader libReader = ExcelUtil.getReader("D:\\UserFiles\\桌面\\高校名单双一流.xlsx", 0);
        List<SchoolExcel> libSchool = libReader.read().stream().map(s -> {
            SchoolExcel schoolExcel = new SchoolExcel();
            schoolExcel.setName(String.valueOf(s.get(1)).trim());
            schoolExcel.setCode(String.valueOf(s.get(2)).trim());
            return schoolExcel;
        }).collect(Collectors.toList());

        FileWriter writer = new FileWriter("D:\\UserFiles\\桌面\\高校名单双一流.txt");
        for (SchoolExcel schoolExcel : libSchool) {

            writer.append("'" + schoolExcel.getCode() + "',");

        }
    }
}
