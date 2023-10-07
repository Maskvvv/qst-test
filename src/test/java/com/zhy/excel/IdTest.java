package com.zhy.excel;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.zhy.utils.InternshipExcel;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 专属页面相关
 *
 * @author zhouhongyin
 * @since 2022/5/23 10:38
 */
//@SpringBootTest
class IdTest {


    /**
     * 生成事先设计好id
     */
    @Test
    void excel() throws SQLException {

        //List<InternshipExcel> internshipExcels = dbCompany();

        ExcelReader reader = ExcelUtil.getReader("D:\\UserFiles\\桌面\\错误文件.xlsx", 0);
        List<String> readAll = reader.read().stream().map(s -> String.valueOf(s.get(0))).collect(Collectors.toList());

        System.out.println(readAll.size());
        String phones = String.join(",", readAll);


        System.out.println(phones);

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
