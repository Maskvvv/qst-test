package com.zhy.competition;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.zhy.utils.CompetitionApplyInfoExcel;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * <p> </p>
 *
 * @author zhouhongyin
 * @since 2023/7/7 10:55
 */
public class CompetitionTest {


    @Test
    void excel() {
        ExcelReader libReader = ExcelUtil.getReader("D:\\UserFiles\\桌面\\2023金砖赛报名数据汇总\\3-湖北省\\湖北省.xlsx", 0);
        List<CompetitionApplyInfoExcel> applyInfoExcels = new ArrayList<>();
        libReader.read().forEach(s -> {
            CompetitionApplyInfoExcel applyInfoExcel = new CompetitionApplyInfoExcel();
            applyInfoExcel.setSort(String.valueOf(s.get(0)));
            applyInfoExcel.setSchoolNme(String.valueOf(s.get(1)).trim());
            applyInfoExcel.setTrackName(String.valueOf(s.get(2)).trim());
            applyInfoExcel.setTeamNme(String.valueOf(s.get(3)).trim());
            String json = JSON.toJSONString(applyInfoExcel);

            for (int i = 0; i < 2; i++) {

                CompetitionApplyInfoExcel clone = JSON.parseObject(json, CompetitionApplyInfoExcel.class);
                clone.setName(String.valueOf(s.get(4 + i)).trim());
                if (StringUtils.isBlank(clone.getName())) break;
                applyInfoExcels.add(clone);
            }
        });


        // 通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter("D:\\UserFiles\\桌面\\2023金砖赛报名数据汇总\\3-湖北省\\湖北省-导入.xlsx");
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(applyInfoExcels);
        // 关闭writer，释放内存
        writer.close();
    }


}
