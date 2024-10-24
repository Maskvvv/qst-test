package com.zhy.db;

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
public class DbTest {



    @Test
    public void dbTest1() throws SQLException {
        List<Entity> list = Db.use().query("SELECT tc.iso_3, tc.name_en, tc.name_cn, tr.country_iso_3, tr.region_cn, tr.region_en, tct.city_cn, tct.city_en, " +
                "c_id, r_id, tct.id city_id \n" +
                "FROM t_country tc\n" +
                "LEFT JOIN t_region tr on tr.c_id = tc.id\n" +
                "LEFT JOIN t_city tct on tct.r_id = tr.id\n" +
                "WHERE tc.name_cn = '阿联酋'");


        Map<String, Entity> country = new LinkedHashMap<>();
        Map<String, Entity> region = new LinkedHashMap<>();
        Map<String, Entity> city = new LinkedHashMap<>();

        int regionId = 1;

        for (Entity entity : list) {
            country.put(entity.getStr("c_id"), entity);
            region.put(entity.getStr("r_id"), entity);
            city.put(entity.getStr("city_id"), entity);
        }


        for (Map.Entry<String, Entity> entry : country.entrySet()) {
            Entity value = entry.getValue();
            value.put("level", "");
            //System.out.println(entry);
        }


        for (Map.Entry<String, Entity> entry : region.entrySet()) {
            Entity value = entry.getValue();
            value.put("regioncode", country.get(value.getStr("c_id")).getStr("iso_3") + "-" + regionId++);
            //System.out.println(entry);
        }

        System.out.println();
        for (Map.Entry<String, Entity> entry : city.entrySet()) {
            Entity value = entry.getValue();
            Entity regionEntry = region.get(value.getStr("r_id"));

            regionEntry.put("chileCode", ((int) regionEntry.getOrDefault("chileCode", 0) + 1));

            value.put("citycode", regionEntry.getStr("regioncode") + "-" + regionEntry.get("chileCode"));
            value.put("parentCode", regionEntry.getStr("regioncode"));
            //System.out.println(entry);

        }


        String districtSql = "INSERT INTO `pn_district` ( `code`, `name`, `parentCode`, `level`) VALUES ( '%s', '%s', '%s', %s);";

        for (Map.Entry<String, Entity> entry : country.entrySet()) {
            Entity value = entry.getValue();

            System.out.println(String.format(districtSql, value.getStr("iso_3"), ZhConverterUtil.toTraditional(value.getStr("name_cn")), "000000", "1"));
        }

        System.out.println("-- ------------------------------------");

        for (Map.Entry<String, Entity> entry : region.entrySet()) {
            Entity value = entry.getValue();
            System.out.println(String.format(districtSql, value.getStr("regioncode"), ZhConverterUtil.toTraditional(value.getStr("region_cn")), value.getStr("iso_3"), "2"));
        }

        System.out.println("-- -------------------------------------");
        System.out.println();

        for (Map.Entry<String, Entity> entry : city.entrySet()) {
            Entity value = entry.getValue();
            String cityCn = value.getStr("city_cn");
            if (StringUtils.isBlank(cityCn)) {
                continue;
            }
            System.out.println(String.format(districtSql, value.getStr("citycode"), ZhConverterUtil.toTraditional(cityCn), value.getStr("parentCode"), "3"));
        }


    }


}
