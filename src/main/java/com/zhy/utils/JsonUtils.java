package com.zhy.utils;

import com.alibaba.fastjson.JSON;
import com.zhy.model.Competition;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhouhongyin
 * @since 2023/10/23 23:17
 */
public class JsonUtils {

    public static <T> List<T> getPagingData(String json, Class<T> clazz) {
        List<T> res = JSON.parseObject(json).getJSONObject("data").getJSONArray("rows").stream().map(o -> JSON.parseObject(o.toString(), clazz)).collect(Collectors.toList());
        return res;
    }

}
