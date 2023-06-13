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

    private String filePathPrefix = "E:/需求文档/file/奥利普奇智";
    private String internshipGroupId = "3f2ca7d4c74f4e88beeb4ae1e57ae0fe";
    private String internshipId = "";
    private String phaseId = "";
    private String missionGroupId = "";

    @Test
    public String getHost() {
        Config load = ConfigFactory.parseResources("qst.conf");
        String qstHost = load.getString("qstHost");
        //System.out.println(qstHost);
        return qstHost;
    }

    @Test
    public void internship() {
        String qstHost = getHost();
        String url = qstHost + "ourea/internships/" + internshipGroupId + "/company";
        String result = get(url);
        JSONObject jsonObject = JSON.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONArray rows = data.getJSONArray("rows");

        String filePathPrefix = this.filePathPrefix;
        for (int i = 0; i < rows.size(); i++) {
            JSONObject internship = rows.getJSONObject(i);

            String internshipId = internship.getString("id");
            String internshipName = internship.getString("name");
            System.out.println("internship : " + internshipName);

            this.internshipId = internshipId;
            this.filePathPrefix = filePathPrefix + "/" + internshipName;
            internshipPhase();
            //System.out.println(this.filePathPrefix);
        }
    }

    @Test
    public void internshipPhase() {
        String qstHost = getHost();
        String url = qstHost + "ourea/internship_phases/" + internshipId + "/company";
        String result = get(url);
        JSONObject jsonObject = JSON.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONArray rows = data.getJSONArray("rows");

        String filePathPrefix = this.filePathPrefix;
        for (int i = 0; i < rows.size(); i++) {
            JSONObject internshipPhase = rows.getJSONObject(i);

            String internshipPhaseId = internshipPhase.getString("id");
            String internshipPhaseName = internshipPhase.getString("name");
            System.out.println("internshipPhase : " + internshipPhaseName);

            this.phaseId = internshipPhaseId;
            this.filePathPrefix = filePathPrefix + "/" + internshipPhaseName;
            missionGroup();
        }
    }

    @Test
    public void missionGroup() {
        String qstHost = getHost();
        String url = qstHost + "ourea/internship_mission_groups?internshipId=" + internshipId + "&phaseId=" + phaseId;
        String result = get(url);
        JSONObject jsonObject = JSON.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONArray rows = data.getJSONArray("rows");
        String filePathPrefix = this.filePathPrefix;
        for (int i = 0; i < rows.size(); i++) {
            JSONObject missionGroup = rows.getJSONObject(i);

            String missionGroupId = missionGroup.getString("id");
            String missionGroupName = missionGroup.getString("name");
            System.out.println("missionGroup : " + missionGroupName);

            this.missionGroupId = missionGroupId;
            this.filePathPrefix = filePathPrefix + "/" + missionGroupName;
            System.out.println(this.filePathPrefix);
            mission();
        }


    }

    @Test
    public void mission() {
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
        long size = HttpUtil.downloadFile(fileUrl, FileUtil.file(filePathPrefix + "/" + fileName), new StreamProgress() {

            @Override
            public void start() {
                Console.log(fileName + " 开始下载。。。。");
            }

            @Override
            public void progress(long total, long progressSize) {
                Console.log("[ " + filePathPrefix + " ] [ " + fileName + " ]" + " 已下载: {}, {}, {}/{}", FileUtil.readableFileSize(progressSize), ((progressSize * 100) / total) + "%", index, totalCount);
            }

            @Override
            public void finish() {
                Console.log(fileName + " 下载完成！");
            }
        });
    }

    private String get(String url) {
        String result = HttpRequest.get(url)
                .header("X-Access-Token", "MGJjMGFhZGMtNzNjMy00MTRhLWE0NzEtZmY2OWMwNDc0ZmUx")
                .timeout(20000)//超时，毫秒
                .execute().body();
        return result;
    }

}
