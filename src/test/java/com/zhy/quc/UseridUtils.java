package com.zhy.quc;

import cn.hutool.core.io.file.FileAppender;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.zhy.model.Column;
import com.zhy.model.Table;
import com.zhy.utils.DbUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p> </p>
 *
 * @author zhouhongyin
 * @since 2024/1/2 16:40
 */
public class UseridUtils {

    private String dbName = "athena_competition";
    private List<String> useridColumns = Stream.of(
            "inviter",
            "customer_id",
            "userid",
            "user_id",
            "member_id",
            "create_by",
            "update_by"
    ).collect(Collectors.toList());


    @Test
    public void main() throws SQLException {
        List<String> dbNames = Stream.of(
                "athena_competition",
                "athena_opencourse",
                "athena_portal",
                "ourea"
        ).collect(Collectors.toList());

        for (String dbName : dbNames) {
            System.out.println("*****************************" + dbName + "*****************************");


            this.dbName = dbName;
            db();

            System.out.println("*****************************" + dbName + "*****************************");

        }


    }

    @Test
    public void db() throws SQLException {

        String getTableNameSql =
                "SELECT\n" +
                "`TABLE_NAME` \n" +
                "FROM\n" +
                "information_schema.TABLES \n" +
                "WHERE\n" +
                "table_schema = ?";

        String getColumnNameSql =
                "SELECT\n" +
                "\tCOLUMN_NAME \n" +
                "FROM\n" +
                "\tinformation_schema.COLUMNS \n" +
                "WHERE\n" +
                "\ttable_schema = ? \n" +
                "\tAND table_name = ?;";

        String updateUseridSql =
                "UPDATE `{}`.`{}` t1\n" +
                        "INNER JOIN `quc`.`user` t2 ON t1.`{}` = t2.`customer_id` \n" +
                        "SET t1.`{}` = t2.`userid`;";

        File file = new File("E:\\需求文档\\统一用户id\\userid变更sql\\" + dbName + ".sql");
        FileAppender appender = new FileAppender(file, 16, true);

        List<Table> tableList = DbUtils.query(Table.class, getTableNameSql, dbName);

        for (Table table : tableList) {

            Set<String> columnSet = DbUtils.query((entities -> entities.stream()
                    .map(entity -> JSON.parseObject(JSON.toJSONString(entity), Column.class).getColumnName())
                    .collect(Collectors.toSet())), getColumnNameSql, dbName, table.getTableName());


            System.out.println("-- ------------------------" + table.getTableName() + "--------------------");

            appender.append("-- ------------------------" + table.getTableName() + "--------------------\n");

            for (String useridColumn : useridColumns) {
                if (columnSet.contains(useridColumn)) {
                    //System.out.println(table.getTableName() + "." + useridColumn);
                    appender.append("-- " + useridColumn);

                    String formatSql = StrUtil.format(updateUseridSql, dbName, table.getTableName(), useridColumn, useridColumn);
                    appender.append(formatSql);
                    System.out.println(formatSql);

                }
            }

            appender.append("");
            appender.append("-- ------------------------" + table.getTableName() + "--------------------\n");
            appender.append("");
            appender.append("");


            appender.flush();
        }

    }
}
