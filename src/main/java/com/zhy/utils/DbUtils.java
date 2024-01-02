package com.zhy.utils;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.alibaba.fastjson.JSON;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p> </p>
 *
 * @author zhouhongyin
 * @since 2024/1/2 17:11
 */
public class DbUtils {


    public static <T> List<T> query(Class<T> resultType, String sql, Object... params) throws SQLException {

        List<T> res = Db.use().query(sql, params).stream()
                .map(entity -> JSON.parseObject(JSON.toJSONString(entity), resultType))
                .collect(Collectors.toList());
        return res;
    }

    public static <R> R query(Function<List<Entity>, R> function, String sql, Object... params) throws SQLException {
        List<Entity> res = Db.use().query(sql, params);
        return function.apply(res);
    }

}
