package com.zhy.competition;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.lang.Console;
import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.zhy.model.Competition;
import com.zhy.model.Delivery;
import com.zhy.model.Question;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
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

    public static void main(String[] args) throws Exception {

        System.out.print("1. 请输入token：");
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


        System.out.print("2. 请输入需要下载的竞赛序号：");
        int competitionIndex = scanner.nextInt();
        if (competitionIndex <= 0 || competitionIndex > competitionList.size()) {
            System.out.println("竞赛不存在");
            return;
        }
        competitionId = competitionList.get(competitionIndex - 1).getId();
        System.out.println("3. 请在弹窗中选择下载目录");
        String dir = baseDir();
        userDir = dir + File.separator + competitionList.get(competitionIndex - 1).getName();
        System.out.println("文件将下载在 [" + userDir + "] 路径下，输入 y 开始下载");

        scanner.nextLine();
        String y = scanner.nextLine();
        if (!"y".equalsIgnoreCase(y)) {
            return;
        }

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

                String filePath = String.join(File.separator, userDir, questionPath, deliveryPath);
                if (new File(filePath + File.separator + delivery.getDeliverableName()).exists()) {
                    System.out.println(delivery.getDeliverableName() + " 以下载！");
                    continue;
                }
                download(signedUrl, filePath, delivery.getDeliverableName());
            }
        }

        System.out.println("竞赛成果物下载完成");

    }

    public static void download(String fileUrl, String filePath, String fileName) {
        String finalFileName = filePath + File.separator + fileName;
        String tempFileName = filePath + File.separator + "未确认 " + MD5.create().digestHex(finalFileName.substring(userDir.length())) + ".tempdownload";

        //将文件下载后保存在E盘，返回结果为下载文件大小
        long size = HttpUtil.downloadFile(fileUrl, FileUtil.file(tempFileName), new StreamProgress() {

            @Override
            public void start() {
                Console.log(finalFileName + " 开始下载。。。。");
            }

            @Override
            public void progress(long total, long progressSize) {
                Console.log("[ " + finalFileName + " ]" + " 已下载: {}, {}", FileUtil.readableFileSize(progressSize), ((progressSize * 100) / total) + "%");
            }

            @Override
            public void finish() {
                Console.log(finalFileName + " 下载完成！");

                rename(tempFileName, finalFileName);

                System.out.println();
            }
        });
    }

    private static String baseDir() throws InterruptedException {
        //使用系统的文件管理器
        if (UIManager.getLookAndFeel().isSupportedLookAndFeel()) {
            final String platform = UIManager.getSystemLookAndFeelClassName();
            // If the current Look & Feel does not match the platform Look & Feel,
            // change it so it does.
            if (!UIManager.getLookAndFeel().getName().equals(platform)) {
                try {
                    UIManager.setLookAndFeel(platform);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }


        FileSystemView fsv = FileSystemView.getFileSystemView();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(fsv.getHomeDirectory());
        fileChooser.setDialogTitle("请选择要下载的目录");
        fileChooser.setApproveButtonText("确定");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = fileChooser.showOpenDialog(null);
        Thread.sleep(10);

        if (JFileChooser.APPROVE_OPTION == result) {
            String path = fileChooser.getSelectedFile().getPath();
            return path;
        }

        return System.getProperty("user.dir");

    }

    public static void rename(String oldName, String newName) {
        File oldFile = new File(oldName);
        File newFile = new File(newName);

        oldFile.renameTo(newFile);
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
