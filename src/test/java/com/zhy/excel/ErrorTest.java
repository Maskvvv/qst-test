package com.zhy.excel;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.zhy.utils.InternProgramExcel;
import com.zhy.utils.InternshipExcel;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhouhongyin
 * @since 2022/5/23 10:38
 */
//@SpringBootTest
class ErrorTest {


    @Test
    void excel() throws SQLException {

        List<InternProgramExcel> internProgramExcels = db();

        Map<String, InternProgramExcel> libIntern = new HashMap<>();

        internProgramExcels.forEach(intern -> {

            String id = String.valueOf(intern.getId()).trim();
            String name = String.valueOf(intern.getName()).trim();

            InternProgramExcel exist = libIntern.get(name);
            if (exist != null) {
                exist.incrDuplicates();
                return;
            }

            InternProgramExcel internshipExcel = new InternProgramExcel();
            internshipExcel.setId(id);
            internshipExcel.setName(name);
            internshipExcel.incrDuplicates();

            libIntern.put(name, internshipExcel);

        });

        ExcelReader reader = ExcelUtil.getReader("D:\\UserFiles\\桌面\\岗位描述错误.xlsx", 0);
        List<InternProgramExcel> readAll = reader.read().stream().map(s -> {
            InternProgramExcel internshipExcel = new InternProgramExcel();
            internshipExcel.setSort(String.valueOf(s.get(0)));

            internshipExcel.setName(String.valueOf(s.get(2)));
            internshipExcel.setId(libIntern.getOrDefault(internshipExcel.getName().trim(), new InternProgramExcel()).getId());
            internshipExcel.setDuplicates(libIntern.getOrDefault(internshipExcel.getName().trim(), new InternProgramExcel()).getDuplicates());
            return internshipExcel;
        }).collect(Collectors.toList());


        // 通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter("D:\\UserFiles\\桌面\\错误职位id.xlsx");
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(readAll);
        // 关闭writer，释放内存
        writer.close();

        System.out.println(readAll.get(1));

        txt(readAll);
    }

    @Test
    void txt(List<InternProgramExcel> readAll) {
        String filePath = "D:\\UserFiles\\桌面\\错误职位id.txt";
        FileUtil.del(filePath);


        FileWriter writer = new FileWriter(filePath);
        for (InternProgramExcel internshipExcel : readAll) {
            String id = StrUtil.blankToDefault(internshipExcel.getId(), "NULL");
            writer.append("\"" + id + "\",");
        }
    }


    @Test
    void excelText() {

        ExcelReader libReader = ExcelUtil.getReader("D:\\UserFiles\\桌面\\错误职位id.xlsx", 0);
        List<InternshipExcel> libSchool = libReader.read().stream().map(s -> {
            InternshipExcel schoolExcel = new InternshipExcel();
            schoolExcel.setId(String.valueOf(s.get(2)).trim());
            return schoolExcel;
        }).collect(Collectors.toList());

        String filePath = "D:\\UserFiles\\桌面\\错误职位id.txt";
        FileUtil.del(filePath);

        FileWriter writer = new FileWriter(filePath);
        for (InternshipExcel schoolExcel : libSchool) {

            String id = StrUtil.blankToDefault(schoolExcel.getId(), "NULL");
            writer.append("'" + id + "',");

        }
    }

    @Test
    List<InternProgramExcel> db() throws SQLException {
        List<InternProgramExcel> internshipExcels = Db.use().findAll("intern_program_occupation").stream().map(entity -> {
            InternProgramExcel internshipExcel = new InternProgramExcel();
            internshipExcel.setId(String.valueOf(entity.get("id")));
            internshipExcel.setName(String.valueOf(entity.get("name")));

            return internshipExcel;
        }).collect(Collectors.toList());

        return internshipExcels;
    }
}
