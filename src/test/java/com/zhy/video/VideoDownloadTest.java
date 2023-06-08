package com.zhy.video;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.lang.Console;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;

/**
 * @author zhouhongyin
 * @since 2023/5/22 10:00
 */
public class VideoDownloadTest {

    private String filePathPrefix = "E:\\需求文档\\file\\胜软科技\\与胜软的邂逅\\带你看胜软！！\\";
    private String missionGroupId = "cf6eba47f4f4423288f1ddec61a698b6";

    @Test
    public String getHost() {
        Config load = ConfigFactory.parseResources("qst.conf");
        String qstHost = load.getString("qstHost");
        System.out.println(qstHost);
        return qstHost;
    }

    @Test
    public void test() {
        String qstHost = getHost();

        String url = qstHost + "ourea/internship_missions?missionGroupId=" + missionGroupId;
        String result = get(url);
        JSONObject jsonObject = JSON.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONArray rows = data.getJSONArray("rows");


        int length = rows.size();
        for (int i = 0; i < length; i++) {
            JSONObject row = rows.getJSONObject(i);
            String materialId = (String) row.get("materialId");
            String fileName = (String) row.get("name");
            String type = (String) row.get("type");

            if (!type.equals("Document") && !type.equals("Video")) continue;
            System.out.println(fileName);

            String fileUrl = qstHost + "ourea/internship_materials/" + materialId + "/view";
            String viewResult = get(fileUrl);
            JSONObject viewData = JSON.parseObject(viewResult).getJSONObject("data");

            String kind = viewData.getString("kind");

            String viewUrl = kind.equals("Video") ? viewData.getString("viewUrl") : viewData.getString("path");


            file(viewUrl, fileName, i + 1, length);

        }

        System.out.println(result);
    }

    @Test
    public void file(String fileUrl, String fileName, int index, int totalCount) {
        //将文件下载后保存在E盘，返回结果为下载文件大小
        long size = HttpUtil.downloadFile(fileUrl, FileUtil.file(filePathPrefix + fileName), new StreamProgress() {

            @Override
            public void start() {
                Console.log(fileName + " 开始下载。。。。");
            }

            @Override
            public void progress(long total, long progressSize) {
                Console.log(fileName + " 已下载: {}, {}, {}/{}", FileUtil.readableFileSize(progressSize), ((progressSize * 100) / total) + "%", index, totalCount);
            }

            @Override
            public void finish() {
                Console.log(fileName + " 下载完成！");
            }
        });
    }

    private String get(String url) {
        String result = HttpRequest.get(url)
                .header("X-Access-Token", "NzZjMjU3NzMtZDA3Ny00MjdmLWIyMDEtOTEyMWY5YzEwZTMz")
                .timeout(20000)//超时，毫秒
                .execute().body();
        return result;
    }

}
