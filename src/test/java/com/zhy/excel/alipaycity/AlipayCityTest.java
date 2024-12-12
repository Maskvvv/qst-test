package com.zhy.excel.alipaycity;

import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import lombok.Builder;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zhouhongyin
 * @since 2022/5/23 10:38
 */
//@SpringBootTest
public class AlipayCityTest {


    @Test
    void smsInternational() {
        ExcelReader libReader = ExcelUtil.getReader("D:\\资料\\票牛\\需求文档\\1128\\passextinfo\\country.xlsx", 1);
        Set<String> codeSet = new HashSet<>();
        String file = "D:\\资料\\票牛\\需求文档\\1128\\passextinfo\\district.sql";
        String sql = "INSERT INTO `pn_alipayHKDistrict` ( `code`, `parentCode`, `name`, `enName`, `level`) VALUES ( '%s', '%s', '%s', '%s', %s);\n";


        AtomicReference<String> preDistrictCode = new AtomicReference<>("");
        libReader.read().stream().forEach(row -> {
            String countryName = row.get(0).toString();
            String countryEnName = row.get(1).toString();
            String countryCode = row.get(2).toString();
            AlipayCity country = AlipayCity.builder()
                    .code(1 + "-" +countryCode)
                    .parentCode("0")
                    .name(countryName)
                    .enName(countryEnName).build();
            if (StringUtils.isNotBlank(countryCode) && !codeSet.contains(country.getCode())) {
                System.out.println(country);
                FileUtil.appendUtf8String(String.format(sql, country.getCode(), country.getParentCode(), country.getName().replace("'", "\\'"), country.getEnName().replace("'", "\\'"), 1), file);
                codeSet.add(country.getCode());

            }

            String provinceName = row.get(3).toString();
            String provinceEnName = row.get(4).toString();
            String provinceCode = row.get(5).toString();
            AlipayCity province = AlipayCity.builder()
                    .code(2 + "-" + provinceCode)
                    .parentCode(country.getCode())
                    .name(provinceName)
                    .enName(provinceEnName).build();
            if (StringUtils.isNotBlank(provinceCode) && !codeSet.contains(province.getCode())) {
                System.out.println(province);
                FileUtil.appendUtf8String(String.format(sql, province.getCode(), province.getParentCode(), province.getName().replace("'", "\\'"), province.getEnName().replace("'", "\\'"), 2), file);
                codeSet.add(province.getCode());
            }

            String cityName = row.get(6).toString();
            String cityEnName = row.get(7).toString();
            String cityCode = row.get(8).toString();
            AlipayCity city = AlipayCity.builder()
                    .code(3 + "-" + cityCode)
                    .parentCode(province.getCode())
                    .name(cityName)
                    .enName(cityEnName).build();
            if (StringUtils.isNotBlank(cityCode) && !codeSet.contains(city.getCode())) {
                System.out.println(city);
                FileUtil.appendUtf8String(String.format(sql, city.getCode(), city.getParentCode(), city.getName().replace("'", "\\'"), city.getEnName().replace("'", "\\'"), 3), file);
                codeSet.add(city.getCode());
            }

            String districtName = row.get(9).toString();
            String districtEnName = row.get(10).toString();
            String districtCode = row.get(11).toString();
            AlipayCity district = AlipayCity.builder()
                    .code(4 + "-" + districtCode)
                    .parentCode(city.getCode())
                    .name(districtName)
                    .enName(districtEnName).build();
            if (StringUtils.isNotBlank(districtCode) && !codeSet.contains(district.getCode())) {
                System.out.println(district);
                FileUtil.appendUtf8String(String.format(sql, district.getCode(), district.getParentCode(), district.getName().replace("'", "\\'"), district.getEnName().replace("'", "\\'"), 4), file);
                codeSet.add(district.getCode());
            }

            if (StringUtils.isNotBlank(districtCode)) {
                preDistrictCode.set(district.getCode());
            }

            String businessAreaName = row.get(12).toString();
            String businessAreaEnName = row.get(13).toString();
            String businessAreaCode = row.get(14).toString();
            AlipayCity businessArea = AlipayCity.builder()
                    .code(5 + "-" + businessAreaCode)
                    .parentCode(preDistrictCode.get())
                    .name(businessAreaName)
                    .enName(businessAreaEnName).build();
            if (StringUtils.isNotBlank(businessAreaCode) && !codeSet.contains(businessArea.getCode())) {
                System.out.println(businessArea);
                FileUtil.appendUtf8String(String.format(sql, businessArea.getCode(), businessArea.getParentCode(), businessArea.getName().replace("'", "\\'"), businessArea.getEnName().replace("'", "\\'"), 5), file);
                codeSet.add(businessArea.getCode());
            }

            System.out.println();
        });

    }


    @Builder
    @Data
    public static class AlipayCity {
        private String code;
        private String parentCode;
        private String name;
        private String enName;
    }

}
