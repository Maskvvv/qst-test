package com.zhy.excel;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.zhy.utils.InternshipExcel;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 专属页面相关
 *
 * @author zhouhongyin
 * @since 2022/5/23 10:38
 */
//@SpringBootTest
class InternshipTest {


    /**
     * 生成事先设计好id
     */
    @Test
    void excel() throws SQLException {

        List<InternshipExcel> internshipExcels = dbCompany();

        Map<String, InternshipExcel> libIntern = new HashMap<>();

        internshipExcels.forEach(intern -> {


            String id = String.valueOf(intern.getId()).trim();
            String name = String.valueOf(intern.getName()).trim();
            String companyName = String.valueOf(intern.getCompanyName()).trim();

            String key = name + companyName;

            InternshipExcel exist = libIntern.get(key);
            if (exist != null) {
                exist.incrDuplicates();
                return;
            }

            InternshipExcel internshipExcel = new InternshipExcel();
            internshipExcel.setId(id);
            internshipExcel.setName(name);
            internshipExcel.setCompanyName(companyName);
            internshipExcel.incrDuplicates();

            libIntern.put(key, internshipExcel);

        });

        ExcelReader reader = ExcelUtil.getReader("D:\\UserFiles\\桌面\\28家-海大实习生计划参与企业名单 5.25.xlsx", 0);
        List<InternshipExcel> readAll = reader.read().stream().map(s -> {
            InternshipExcel internshipExcel = new InternshipExcel();
            internshipExcel.setSort(String.valueOf(s.get(0)));
            internshipExcel.setCompanyName(String.valueOf(s.get(1)).trim());
            internshipExcel.setName(String.valueOf(s.get(3)).trim());
            String key = internshipExcel.getName() + internshipExcel.getCompanyName();
            internshipExcel.setId(libIntern.getOrDefault(key, new InternshipExcel()).getId());
            internshipExcel.setDuplicates(libIntern.getOrDefault(key, new InternshipExcel()).getDuplicates());
            return internshipExcel;
        }).collect(Collectors.toList());


        // 通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter("D:\\UserFiles\\桌面\\海大id.xlsx");
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(readAll);
        // 关闭writer，释放内存
        writer.close();

        System.out.println(readAll.get(1));

        txt(readAll);
    }

    @Test
    void txt(List<InternshipExcel> readAll) {
        String filePath = "D:\\UserFiles\\桌面\\海大id.txt";
        FileUtil.del(filePath);


        FileWriter writer = new FileWriter(filePath);
        for (InternshipExcel internshipExcel : readAll) {
            String id = StrUtil.blankToDefault(internshipExcel.getId(), "NULL");
            writer.append("\"" + id + "\",");
        }
    }


    @Test
    void excelText() {

        ExcelReader libReader = ExcelUtil.getReader("D:\\UserFiles\\桌面\\current.xlsx", 0);
        List<InternshipExcel> libSchool = libReader.read().stream().map(s -> {
            InternshipExcel schoolExcel = new InternshipExcel();
            schoolExcel.setId(String.valueOf(s.get(4)).trim());
            return schoolExcel;
        }).collect(Collectors.toList());

        String filePath = "D:\\UserFiles\\桌面\\海大id.txt";
        FileUtil.del(filePath);

        FileWriter writer = new FileWriter(filePath);
        for (InternshipExcel schoolExcel : libSchool) {

            String id = StrUtil.blankToDefault(schoolExcel.getId(), "NULL");
            writer.append("\"" + id + "\",");

        }
    }


    @Test
    List<InternshipExcel> dbCompany() throws SQLException {
        List<InternshipExcel> internshipExcels = Db.use().query("SELECT\n" +
                "\tipg.id id,\n" +
                "\tipg.`name` NAME,\n" +
                "\tcp.`name` companyName \n" +
                "FROM\n" +
                "\t`intern_program` ipg\n" +
                "\tLEFT JOIN company cp ON cp.id = ipg.company_id").stream().map(entity -> {
            InternshipExcel internshipExcel = new InternshipExcel();
            internshipExcel.setId(String.valueOf(entity.get("id")));
            internshipExcel.setName(String.valueOf(entity.get("name")));
            internshipExcel.setCompanyName(String.valueOf(entity.get("companyName")));

            return internshipExcel;
        }).collect(Collectors.toList());

        return internshipExcels;

    }


    @Test
    List<InternshipExcel> db() throws SQLException {

        List<InternshipExcel> internshipExcels = Db.use().findAll("intern_program").stream().map(entity -> {
            InternshipExcel internshipExcel = new InternshipExcel();
            internshipExcel.setId(String.valueOf(entity.get("id")));
            internshipExcel.setName(String.valueOf(entity.get("name")));

            return internshipExcel;
        }).collect(Collectors.toList());


        return internshipExcels;
    }
}
