package com.zhy.excel;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.zhy.utils.InternshipExcel;
import com.zhy.utils.SchoolExcel;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhouhongyin
 * @since 2022/5/23 10:38
 */
//@SpringBootTest
class InternshipTest {


    @Test
    void excel() {
        ExcelReader libReader = ExcelUtil.getReader("D:\\UserFiles\\桌面\\intern_program.xlsx", 0);


        Map<String, InternshipExcel> libIntern = new HashMap<>();

        List<InternshipExcel> libList = new ArrayList<>();

        libReader.read().forEach(intern -> {

            String id = String.valueOf(intern.get(0)).trim();
            String name = String.valueOf(intern.get(1)).trim();
            InternshipExcel exist = libIntern.get(name);
            if (exist != null) {
                exist.incrDuplicates();
                return;
            }

            InternshipExcel internshipExcel = new InternshipExcel();
            internshipExcel.setId(id);
            internshipExcel.setName(name);
            internshipExcel.incrDuplicates();

            libIntern.put(name, internshipExcel);
            libList.add(internshipExcel);

        });

        //InternshipExcel jingzao = libIntern.get("京汽造青工职院订单班");
        //System.out.println(jingzao);


        ExcelReader reader = ExcelUtil.getReader("D:\\UserFiles\\桌面\\企业排序(1).xlsx", 0);
        List<InternshipExcel> readAll = reader.read().stream().map(s -> {
            InternshipExcel internshipExcel = new InternshipExcel();
            internshipExcel.setSort(String.valueOf(s.get(0)));
            internshipExcel.setCompanyName(String.valueOf(s.get(1)));
            internshipExcel.setName(String.valueOf(s.get(2)));
            internshipExcel.setId(libIntern.getOrDefault(internshipExcel.getName().trim(), new InternshipExcel()).getId());
            internshipExcel.setDuplicates(libIntern.getOrDefault(internshipExcel.getName().trim(), new InternshipExcel()).getDuplicates());
            return internshipExcel;
        }).collect(Collectors.toList());


        // 通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter("D:\\UserFiles\\桌面\\青工职id.xlsx");
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(readAll);
        // 关闭writer，释放内存
        writer.close();

        System.out.println(readAll.get(1));

        txt(readAll);
    }

    @Test
    void txt(List<InternshipExcel> readAll) {
        String filePath = "D:\\UserFiles\\桌面\\青工职id.txt";
        FileUtil.del(filePath);


        FileWriter writer = new FileWriter(filePath);
        for (InternshipExcel internshipExcel : readAll) {
            writer.append("'" + internshipExcel.getId() + "',");
        }
    }


    @Test
    void excelText() {

        ExcelReader libReader = ExcelUtil.getReader("D:\\UserFiles\\桌面\\青工职id.xlsx", 0);
        List<InternshipExcel> libSchool = libReader.read().stream().map(s -> {
            InternshipExcel schoolExcel = new InternshipExcel();
            schoolExcel.setId(String.valueOf(s.get(4)).trim());
            return schoolExcel;
        }).collect(Collectors.toList());

        String filePath = "D:\\UserFiles\\桌面\\青工职id.txt";
        FileUtil.del(filePath);

        FileWriter writer = new FileWriter(filePath);
        for (InternshipExcel schoolExcel : libSchool) {

            String id = StrUtil.blankToDefault(schoolExcel.getId(), "NULL");
            writer.append("'" + id + "',");

        }
    }
}
