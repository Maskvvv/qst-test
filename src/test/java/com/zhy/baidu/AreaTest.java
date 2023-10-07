package com.zhy.baidu;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhy.utils.BaiduAreaExcel;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>  </p>
 *
 * @author zhouhongyin
 * @since 2023/6/8 11:48
 */
public class AreaTest {

    @Test
    void excel() throws IOException, InterruptedException {
        ExcelReader libReader = ExcelUtil.getReader("D:\\UserFiles\\桌面\\学校.xlsx", 0);
        List<BaiduAreaExcel> libArea = libReader.read().stream().map(s -> {
            BaiduAreaExcel baiduAreaExcel = new BaiduAreaExcel();
            baiduAreaExcel.setCode(String.valueOf(s.get(0)).trim());
            //baiduAreaExcel.setCity(String.valueOf(s.get(1)).trim());
            //baiduAreaExcel.setArea(String.valueOf(s.get(2)).trim());
            baiduAreaExcel.setName(String.valueOf(s.get(3)).trim());
            baiduAreaExcel.setOther(String.valueOf(s.get(4)).trim());
            return baiduAreaExcel;
        }).collect(Collectors.toList());


        for (int i = 0; i < 100; i++) {
            BaiduAreaExcel baiduAreaExcel = libArea.get(i);
            String[] distance = baiduArea(baiduAreaExcel.getName());
            baiduAreaExcel.setCity(distance[0]);
            baiduAreaExcel.setArea(distance[0]);

        }

        // 通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter("D:\\UserFiles\\桌面\\学校地区.xlsx");
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(libArea);
        // 关闭writer，释放内存
        writer.close();

    }

    @Test
    public String[] baiduArea(String wd) throws IOException, InterruptedException {
        int random = (int) (Math.random() * 1000);
        Thread.sleep(random);

        //String wd = "%E9%9D%92%E5%B2%9B%E5%9B%BD%E9%99%85%E5%88%9B%E6%96%B0%E5%9B%AD";
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://map.baidu.com/su?wd=" + wd +
                        "&cid=236" +
                        "&type=0" +
                        "&newmap=1" +
                        "&b=(13964350.037516482%2C5232085.734395605%3B13990072.737472527%2C5244771.245604396)" +
                        "&t=1686187720036" +
                        "&pc_ver=2" +
                        "&qt=sug" +
                        "&auth=")
                .method("GET", null)
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Connection", "keep-alive")
                .addHeader("Referer", "https://map.baidu.com/search/%E4%B8%9C%E4%B8%B0%E5%8E%BF%E4%B8%9C%E4%B8%B0%E9%95%87%E4%B8%9C%E9%98%B3%E5%AD%A6%E6%A0%A1/@13977211.387494504,5238428.49,14.35z?querytype=con&wd=%E4%B8%9C%E4%B8%B0%E5%8E%BF%E4%B8%9C%E4%B8%B0%E9%95%87%E4%B8%9C%E9%98%B3%E5%AD%A6%E6%A0%A1&c=183&provider=pc-aladin&pn=0&device_ratio=2&da_src=shareurl")
                .addHeader("Sec-Fetch-Dest", "empty")
                .addHeader("Sec-Fetch-Mode", "cors")
                .addHeader("Sec-Fetch-Site", "same-origin")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36")
                .addHeader("auth", "")
                .addHeader("newfrom", "zhuzhan_webmap")
                .addHeader("pcevaname", "pc4.1")
                .addHeader("sec-ch-ua", "\"Google Chrome\";v=\"113\", \"Chromium\";v=\"113\", \"Not-A.Brand\";v=\"24\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"Windows\"")
                .addHeader("seckey", "")
                .addHeader("Cookie", "")
                .build();
        Response response = client.newCall(request).execute();

        String bodyString = response.body().string();

        JSONObject jsonObject = JSON.parseObject(bodyString);
        JSONArray s = jsonObject.getJSONArray("s");
        String o = (String) s.get(0);

        String[] split = o.split("\\$");
        System.out.println(wd + " " + split[0] + " " + split[1]);
        return split;
    }

    @Test
    public void random() {

        for (int i = 0; i < 10; i++) {
            double random = Math.random();
            System.out.println((int) (random * 1000));
        }

    }

}
