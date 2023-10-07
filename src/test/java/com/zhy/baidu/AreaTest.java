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
                        "&auth=R0aDU2J5wYaI7SQef0wLP5AUZ2Z7bW8NuxLRLxRNNxNtHK%40ZKXTXRKCEBwyS8v7uvkGcuVtvvhguVtvyheuVtvCMGuVtvCQMuVtvIPcuVtvYvjuVtvZgMuVtcvY1SGpuxxtFiFmEb1vc3CuVtvcPPuVtveGvuVtveh3uxtwiKDv7uvhgMuxVVtvrMhuVtGccZcuxtf0wd0vyMSMySOOyO&seckey=ESmxq2EbEXwArGejZFp9pPZA9e3kOf7ZO94NyzVFET0%3D%2CA3ToHVLZUBBmLO1ceqFMZ2Lqqf8UU1DboSGnujAGnh_bgEvsIvA4ZTMNXlInikGdYK4UTSt8UBxx4qBRC8RnQhOM8g_PVNBL2jN-szJQ7U3AHwl9L7DgK0DNexQBIN6K1_CmifiU7aDHWKUSPkXpOyuIZoo3hoA7UwJjghFWc12f1H89jqIxVL0zFiIG13Ol&pcevaname=pc4.1&newfrom=zhuzhan_webmap")
                .method("GET", null)
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Connection", "keep-alive")
                .addHeader("Referer", "https://map.baidu.com/search/%E4%B8%9C%E4%B8%B0%E5%8E%BF%E4%B8%9C%E4%B8%B0%E9%95%87%E4%B8%9C%E9%98%B3%E5%AD%A6%E6%A0%A1/@13977211.387494504,5238428.49,14.35z?querytype=con&wd=%E4%B8%9C%E4%B8%B0%E5%8E%BF%E4%B8%9C%E4%B8%B0%E9%95%87%E4%B8%9C%E9%98%B3%E5%AD%A6%E6%A0%A1&c=183&provider=pc-aladin&pn=0&device_ratio=2&da_src=shareurl")
                .addHeader("Sec-Fetch-Dest", "empty")
                .addHeader("Sec-Fetch-Mode", "cors")
                .addHeader("Sec-Fetch-Site", "same-origin")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36")
                .addHeader("auth", "R0aDU2J5wYaI7SQef0wLP5AUZ2Z7bW8NuxLRLxRNNxNtHK%40ZKXTXRKCEBwyS8v7uvkGcuVtvvhguVtvyheuVtvCMGuVtvCQMuVtvIPcuVtvYvjuVtvZgMuVtcvY1SGpuxxtFiFmEb1vc3CuVtvcPPuVtveGvuVtveh3uxtwiKDv7uvhgMuxVVtvrMhuVtGccZcuxtf0wd0vyMSMySOOyO")
                .addHeader("newfrom", "zhuzhan_webmap")
                .addHeader("pcevaname", "pc4.1")
                .addHeader("sec-ch-ua", "\"Google Chrome\";v=\"113\", \"Chromium\";v=\"113\", \"Not-A.Brand\";v=\"24\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"Windows\"")
                .addHeader("seckey", "ESmxq2EbEXwArGejZFp9pPZA9e3kOf7ZO94NyzVFET0%3D%2CA3ToHVLZUBBmLO1ceqFMZ2Lqqf8UU1DboSGnujAGnh_bgEvsIvA4ZTMNXlInikGdYK4UTSt8UBxx4qBRC8RnQhOM8g_PVNBL2jN-szJQ7U3AHwl9L7DgK0DNexQBIN6K1_CmifiU7aDHWKUSPkXpOyuIZoo3hoA7UwJjghFWc12f1H89jqIxVL0zFiIG13Ol")
                .addHeader("Cookie", "BIDUPSID=C911457D001DD8242E9340BF2B5E5687; PSTM=1649209765; BAIDUID=C911457D001DD824920CA13709615A47:SL; BDSFRCVID=Mc8OJeCmHRhWneoj7rJgM73fEeKK0gOTHllnoMiRAPOFYGCVJeC6EG0Ptf8g0KubuTkzogKK0gOTH6KF_2uxOjjg8UtVJeC6EG0Ptf8g0M5; H_BDCLCKID_SF=tbCeoK0-tDt3qn7I5KIhDjo-qxbXqMr2fgOZ0lOEWUosSnrNhPCbKxIsXHteKfQ-W20j0h7mWnRSDR7EMq5N0M4zWtJmLfT-0bc4KKJxbnLWeIJo5t5h3-PhhUJiB5OMBan7_qvIXKohJh7FM4tW3J0ZyxomtfQxtNRJ0DnjtpChbRO4-TF5j5ObDU5; newlogin=1; BDUSS=VNObzd1eVNsaDYtZnlMQVBQSGpXMnpkcDJLendtYWlTUnozZDdJR1cyd2FDbnRrRVFBQUFBJCQAAAAAAAAAAAEAAADspojrTWFzazE5OTgxMjEwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABp9U2QafVNkd; BDUSS_BFESS=VNObzd1eVNsaDYtZnlMQVBQSGpXMnpkcDJLendtYWlTUnozZDdJR1cyd2FDbnRrRVFBQUFBJCQAAAAAAAAAAAEAAADspojrTWFzazE5OTgxMjEwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABp9U2QafVNkd; delPer=0; BAIDUID_BFESS=C911457D001DD824920CA13709615A47:SL; BDSFRCVID_BFESS=Mc8OJeCmHRhWneoj7rJgM73fEeKK0gOTHllnoMiRAPOFYGCVJeC6EG0Ptf8g0KubuTkzogKK0gOTH6KF_2uxOjjg8UtVJeC6EG0Ptf8g0M5; H_BDCLCKID_SF_BFESS=tbCeoK0-tDt3qn7I5KIhDjo-qxbXqMr2fgOZ0lOEWUosSnrNhPCbKxIsXHteKfQ-W20j0h7mWnRSDR7EMq5N0M4zWtJmLfT-0bc4KKJxbnLWeIJo5t5h3-PhhUJiB5OMBan7_qvIXKohJh7FM4tW3J0ZyxomtfQxtNRJ0DnjtpChbRO4-TF5j5ObDU5; BDRCVFR[dG2JNJb_ajR]=mk3SLVN4HKm; BDRCVFR[-pGxjrCMryR]=mk3SLVN4HKm; BDRCVFR[qm96YCg0y3b]=mk3SLVN4HKm; ZFY=KNkvxoudbszHEvTtVqy1k0ZZLIAmAkVPTN2mH9uCxkc:C; PSINO=2; H_PS_PSSID=38515_36548_38686_38540_38610_38767_38719_38841_38792_38808_38840_38638_26350_38569; BA_HECTOR=0h8485ah01242h0kala10gci1i82bli1n; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; M_LG_UID=3951601388; M_LG_SALT=ceb6264a9b132cd1437a1237881752f6; MCITY=-%3A; validate=43979; ab_sr=1.0.1_OWI5OWM5Mzk0OTkwZDVhYjQ5MTI4MzgyYjYzM2FmZjA5MDJlNWZhZGUxODZmNThhMzhmM2Y4NDhlYjg0OGE0NjBmZjMzMzQ4NmE5ZTdiNWEzOTFjMWJhMGE2MGUzMjNmYjM5ODk5ZjgxNDQ4YzY5OGEyMjJhYWRjZjQ2ZmNkOTE3NjI1ZTFjMzQ1ZmFmMDk3OTkzYTFiZDAzNjk2MTE1OWEyYzAwOTg5YTQ0YTkwOWFlMzQ3OTAwZDZjM2FhMTJm")
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
