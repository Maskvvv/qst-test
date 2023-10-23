package com.zhy.competition;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.lang.Console;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.zhy.model.Competition;
import com.zhy.model.Delivery;
import com.zhy.model.Question;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * <p> </p>
 *
 * @author zhouhongyin
 * @since 2023/10/23 9:43
 */
public class DownLoadWork {

    public static String token = "";
    //public static String host = "https://tev-athena-admin.qstcloud.net/api/athena/admin";
    public static String host = "https://admin-a.eduplus.net/api/athena/admin";
    public static String competitionId = "e7a081f48cec4edc928bb71f16501710";
    public static String userDir;

    public static void main(String[] args) throws IOException {

        System.out.print("请输入token：");
        Scanner scanner = new Scanner(System.in);
        token = scanner.nextLine();


        String competitionJson = get("/competition_list/uplus");
        List<Competition> competitionList = JSON.parseObject(competitionJson).getJSONObject("data").getJSONArray("rows").stream().map(o -> JSON.parseObject(o.toString(), Competition.class)).collect(Collectors.toList());

        System.out.println("----------------------------------------------------------------");
        System.out.println(String.format("%-5s%-30s", "序号", "竞赛名称"));
        for (int i = 0; i < competitionList.size(); i++) {
            String out = String.format("%-5d %-30s", (i + 1), competitionList.get(i).getName());
            System.out.println(out);
        }
        System.out.println("----------------------------------------------------------------");


        System.out.print("请输入需要下载的竞赛序号：");
        int competitionIndex = scanner.nextInt();
        if (competitionIndex <= 0 || competitionIndex > competitionList.size()) {
            System.out.println("竞赛不存在");
            return;
        }
        competitionId = competitionList.get(competitionIndex - 1).getId();
        userDir = System.getProperty("user.dir") + "/" + competitionList.get(competitionIndex - 1).getName();

        String questionJson = get("/questions_area/competition/list?competitionId=" + competitionId);
        List<Question> questionsList = JSON.parseObject(questionJson).getJSONObject("data").getJSONArray("rows").stream().map(o -> JSON.parseObject(o.toString(), Question.class)).collect(Collectors.toList());

        for (Question question : questionsList) {
            String questionPath = String.join("-", question.getQuestionName(), question.getTrackName(), question.getStageName());

            String deliveryUri = "/competition_question_deliverables/personal/committed?stageId=" + question.getStageId() + "&questionId=" + question.getQuestionId();
            String deliveryJson = get(deliveryUri);
            List<Delivery> deliveryList = JSON.parseObject(deliveryJson).getJSONObject("data").getJSONArray("rows").stream().map(o -> JSON.parseObject(o.toString(), Delivery.class)).collect(Collectors.toList());

            for (Delivery delivery : deliveryList) {
                if (StringUtils.isBlank(delivery.getDeliverablePath())) continue;
                String deliveryPath = String.join("-", delivery.getLeaderName(), delivery.getSchoolName());

                Map<String, String> body = new HashMap<>();
                body.put("fileName", delivery.getDeliverableName());
                body.put("objectKey", delivery.getDeliverablePath());
                body.put("resourceType", "competitionDeliverable");
                String downJson = post("/obs/web/download", JSON.toJSONString(body));
                String signedUrl = JSON.parseObject(downJson).getJSONObject("data").getString("signedUrl");

                String filePath = String.join(File.separator, userDir, questionPath, deliveryPath, delivery.getDeliverableName());
                download(signedUrl, filePath);
            }
        }

        System.out.println("竞赛成果物下载完成");

    }

    public static void download(String fileUrl, String fileName) {
        //将文件下载后保存在E盘，返回结果为下载文件大小
        long size = HttpUtil.downloadFile(fileUrl, FileUtil.file(fileName), new StreamProgress() {

            @Override
            public void start() {
                Console.log(fileName + " 开始下载。。。。");
            }

            @Override
            public void progress(long total, long progressSize) {
                Console.log("[ " + fileName + " ]" + " 已下载: {}, {}", FileUtil.readableFileSize(progressSize), ((progressSize * 100) / total) + "%");
            }

            @Override
            public void finish() {
                Console.log(fileName + " 下载完成！");
                System.out.println();
            }
        });
    }


    private static String get(String url) {
        String result = HttpRequest.get(host + url)
                .header("Authentication", token)
                .timeout(20000)//超时，毫秒
                .execute().body();
        return result;
    }

    private static String post(String url, String body) {
        String result = HttpRequest.post(host + url)
                .body(body)
                .header("Authentication", token)
                .timeout(20000)//超时，毫秒
                .execute().body();
        return result;
    }

}