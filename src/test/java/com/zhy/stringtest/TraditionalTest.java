package com.zhy.stringtest;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhouhongyin
 * @since 2024/4/1 11:49
 */
public class TraditionalTest {
    String inputFile = "";
    String outputFile = "";

    @Test
    void file() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(inputFile)), StandardCharsets.UTF_8));


        StringBuilder sb = new StringBuilder();

        bufferedReader.lines().forEach(line -> {
            String traditional = toTraditional(line);
            sb.append(traditional).append("\r\n");
        });

        bufferedReader.close();

        FileWriter fileWriter = new FileWriter(inputFile);
        fileWriter.append(sb.toString());
        fileWriter.flush();
        fileWriter.close();


    }

    String toTraditional(String content) {
        String regex = "(Resp.fail\\(.+?\\);)|(badRequest\\(.+\\);)|(throw.+?Exception\\(.+?\\);)";
        Pattern pattern = Pattern.compile(regex);


        Matcher matcher = pattern.matcher(content);
        StringBuffer res = new StringBuffer();

        while (matcher.find()) {
            String group = matcher.group();

            String traditional = ZhConverterUtil.toTraditional(group);

            matcher.appendReplacement(res, traditional);
        }

        matcher.appendTail(res);

        return res.toString();
    }
}
