package com.zhy.db;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhouhongyin
 * @since 2022/7/5 11:14
 */
public class DistrictTest {


    @Test
    public void dbTest1() throws SQLException {
        int i = 1;

        String translateSql = "SELECT * \n" +
                "FROM `pn_property_translate` ppt\n" +
                "left JOIN pn_property_translated_content pptc ON pptc.propertyTranslateId = ppt.id\n" +
                "\n" +
                "WHERE ppt.bizType = 7 and ppt.languageType = 2 and ppt.propertyType = 10004 and ppt.bizId = %s";


        String districtSql = "UPDATE `pn_district` SET  `name` = '%s' WHERE `id` = %s;";

        List<Entity> list = Db.use().query("SELECT * FROM pn_district");
        for (Entity entity : list) {
            String id = entity.getStr("id");

            List<Entity> translateList = Db.use().query(String.format(translateSql, id));
            String name = "null";
            if (!CollUtil.isEmpty(translateList)) {
                name = translateList.get(0).getStr("translatedContent").replace("'", "\\'");
            }

            System.out.println(String.format(districtSql, name, id));


            //System.out.println(i++);
        }

    }


}
