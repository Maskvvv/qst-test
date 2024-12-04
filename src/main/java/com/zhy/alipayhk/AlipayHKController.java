package com.zhy.alipayhk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.global.api.tools.SignatureTool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

/**
 * @author zhouhongyin
 * @since 2024/11/27 20:08
 */
@Slf4j
@RestController
@RequestMapping("alipayhk")
public class AlipayHKController {

    @Autowired
    private RestTemplate restTemplate;


     String signatureCipher(String httpMethod, String uri, String clientId, String requestTime, String httpBody){

         try {
             String sign = SignatureTool.sign(httpMethod, uri, clientId, requestTime, httpBody, AliPayHkSignatureUtils.PRIVATE_KEY);
             return String.format(AliPayHkSignatureUtils.SIGN_TEMPLATE_HEADER, sign);

         } catch (Exception e) {
             throw new RuntimeException(e);
         }

         //String encrypting = AliPayHkSignatureUtils.encrypting(httpMethod,
         //        uri,
         //        clientId,
         //        requestTime,
         //        httpBody);
         //
         //System.out.println(encrypting);
         //return encrypting;
    };

     HttpHeaders httpHeaders(String clientId, String requestTime, String signatureText){
        HttpHeaders requestHeaders = new HttpHeaders();

        requestHeaders.add(AliPayHKConst.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        requestHeaders.add(AliPayHKConst.CLIENT_ID_REQUEST_SIDE, clientId);
        requestHeaders.add(AliPayHKConst.REQUEST_TIME_REQUEST_SIDE, requestTime);
        requestHeaders.add(AliPayHKConst.SIGNATURE, signatureText);
        return requestHeaders;
    }

     Map<String, String> httpHeaderMap(String clientId, String requestTime, String signatureText){
         Map<String, String> requestHeaders = new HashMap<>();

        requestHeaders.put(AliPayHKConst.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        requestHeaders.put(AliPayHKConst.CLIENT_ID_REQUEST_SIDE, clientId);
        requestHeaders.put(AliPayHKConst.REQUEST_TIME_REQUEST_SIDE, requestTime);
        requestHeaders.put(AliPayHKConst.SIGNATURE, signatureText);
        return requestHeaders;
    }


     String generateSignature(String clientId, String uri, String requestBody, String requestTime) {
        String signatureCipher = signatureCipher(HttpMethod.POST.name(), uri, clientId, requestTime, requestBody);
        if (StringUtils.isBlank(signatureCipher)) {
            log.error("Signature generation failed for requestBody: {}", requestBody); // 补充日志
            throw new RuntimeException("translateFunction().apply(I18nKey.EXCEPTION_PARAM_SIGNED_ERROR)");
        }
        return signatureCipher;
    }

    Resp<String> gotRemoteResp(String clientId, String uri, String requestBody) {
        String requestTime = ZonedDateTime.now().format(ISO_OFFSET_DATE_TIME); // 统一生成时间戳
        try {
            String signatureCipher = generateSignature(clientId, uri, requestBody, requestTime);
            ResponseEntity<String> response = sendHttpRequest(clientId, uri, requestBody, signatureCipher, requestTime);
            System.out.println(response.getBody());
            validateResponse(response, uri);
            return remoteRespBody(response.getBody());
        } catch (Exception e) {
            log.error("Error processing Alipay HK request", e);
            return Resp.fail("exception()");
        }
    }

     void validateResponse(ResponseEntity<String> response, String uri) {
        HttpHeaders responseHeaders = response.getHeaders();
         log.info("headers: {}", JSON.toJSONString(responseHeaders));
        String signature = extractSignature(responseHeaders);
        String clientId = extractHeaderValue(responseHeaders, AliPayHKConst.CLIENT_ID_RESPONSE_SIDE);
        String requestTime = extractHeaderValue(responseHeaders, AliPayHKConst.RESPONSE_TIME_RESPONSE_SIDE);

        boolean verify = AliPayHkSignatureUtils.decryptedAndVerified(
                signature, HttpMethod.POST.name(), uri, clientId, requestTime, response.getBody());
        if (!verify) {
            log.error("Signature verification failed for response body: {}", response.getBody()); // 补充日志
            throw new RuntimeException("illegal()");
        }
    }

     Resp<String> remoteRespBody(String responseBody) {
        if (StringUtils.isBlank(responseBody)) { // 补充对 responseBody 为空的检查
            log.error("Response body is empty");
            return Resp.fail("exception()");
        }
        JSONObject jsonObject = JSON.parseObject(responseBody);
        if (Objects.isNull(jsonObject) || !jsonObject.containsKey("result")
                || Objects.isNull(jsonObject.getJSONObject("result"))
                || !"S".equalsIgnoreCase(jsonObject.getJSONObject("result").getString("resultStatus"))) {
            log.error("Invalid response: {}", responseBody);
            return Resp.fail("exception()");
        }
        return Resp.success(responseBody);
    }

     String extractSignature(HttpHeaders headers) {
        List<String> signatureOriginList = headers.get("Signature");
        if (CollectionUtils.isEmpty(signatureOriginList)) {
            log.error("Signature header is missing or empty"); // 补充日志
            throw new IllegalArgumentException("Signature header is empty");
        }
        Map<String, String> respHeaderMap = Arrays.stream(signatureOriginList.get(0).split(","))
                .map(pair -> pair.split("="))
                .filter(keyValue -> keyValue.length == 2)
                .collect(Collectors.toMap(keyValue -> keyValue[0], keyValue -> keyValue[1]));
        String signature = respHeaderMap.get("signature");
        if (StringUtils.isBlank(signature)) {
            log.error("Extracted signature is empty"); // 补充日志
            throw new IllegalArgumentException("Signature is empty");
        }
        return signature;
    }

     String extractHeaderValue(HttpHeaders headers, String headerName) {
        List<String> headerValues = headers.get(headerName);
        if (CollectionUtils.isEmpty(headerValues)) {
            log.error("{} header is missing or empty", headerName); // 补充日志
            throw new IllegalArgumentException(headerName + " is empty");
        }
        return headerValues.get(0);
    }

     String getHost(String uri) {
        //String host = ConfigManager.isDevelop() ? AliPayHKConst.DOMAIN_PRE + uri : AliPayHKConst.DOMAIN_PROD + uri;
        String host = AliPayHKConst.DOMAIN_PRE + uri ;
        log.info("Host generated: {}", host); // 补充日志
        return host;
    }





     ResponseEntity<String> sendHttpRequest(String clientId, String uri, String requestBody, String signatureCipher, String requestTime) {
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders(clientId, requestTime, signatureCipher));
        String host = getHost(uri);
         RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(host, entity, String.class);
        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            log.error("Failed to invoke Alipay HK server. Status: {}, Response: {}", response.getStatusCode(), response.getBody()); // 补充日志
            throw new RuntimeException("translateFunction().apply(I18nKey.EXCEPTION_REMOTE_REQUEST_ERROR)");
        }
        return response;
    }






    @Test
    public void test() {
        System.out.println(JSON.parseObject(createTemplateJson, AliPayHKCreateTemplateRequest.class));
    }


    @Test
    public void createTemplate() {
        Resp<String> signatureAndVerifyResp = gotRemoteResp(AliPayHKConst.CLIENT_ID,
                AliPayHKConst.CREATE_TEMPLATE_URL,
                createTemplateJson);

        System.out.println(signatureAndVerifyResp);
    }



    @Test
    public void updateTemplate() {
        Resp<String> signatureAndVerifyResp = gotRemoteResp(AliPayHKConst.CLIENT_ID,
                AliPayHKConst.UPDATE_TEMPLATE_URL,
                updateTemplateJson);

        System.out.println(signatureAndVerifyResp);
    }

    @Test
    public void queryTemplate() {
        Resp<String> signatureAndVerifyResp = gotRemoteResp(AliPayHKConst.CLIENT_ID,
                AliPayHKConst.QUERY_TEMPLATE_URL,
                queryJson);
        System.out.println(signatureAndVerifyResp);
    }

    @Test
    public void createTicket() {
        Resp<String> signatureAndVerifyResp = gotRemoteResp(AliPayHKConst.CLIENT_ID,
                AliPayHKConst.CREATE_TICKET_URL,
                createTicketJson);
        System.out.println(signatureAndVerifyResp);
    }

    @Test
    public void updateTicket() {
        Resp<String> signatureAndVerifyResp = gotRemoteResp(AliPayHKConst.CLIENT_ID,
                AliPayHKConst.UPDATE_TICKET_URL,
                updateTicketJson1);
        System.out.println(signatureAndVerifyResp);
    }






    private String updateTicketJson = "{\n" +
            "    \"merchantId\": \"2160400000000022\",\n" +
            "    \"userId\": \"2160220043214225\",\n" +
            "    \"passId\": \"2024120219027102160229300021201\",\n" +
            "    \"status\": \"REFUND\"\n" +
            "}";

    private String updateTicketJson1 = "{\n" +
            "    \"merchantId\": \"2160400000000022\",\n" +
            "    \"userId\": \"2160220043214225\",\n" +
            "    \"passId\": \"2024120219027102160220500021203\",\n" +
            "    \"status\": \"REFUND\",\n" +
            "    \"dataInfo\": {\n" +
            "        \"$address_zh_HK$\": \"香港九龍彌敦道792-804號協成行太子中心703室test\",\n" +
            "        \"$address_en_US$\": \"香港九龍彌敦道792-804號協成行太子中心703室test\",\n" +
            "        \"$availableTimes_en_US$\": 2,\n" +
            "        \"$availableTimes_zh_HK$\": 2,\n" +
            "        \"$price_zh_HK$\": {\n" +
            "            \"cent\": 800,\n" +
            "            \"currency\": \"HKD\"\n" +
            "        },\n" +
            "        \"$price_en_US$\": {\n" +
            "            \"cent\": 800,\n" +
            "            \"currency\": \"HKD\"\n" +
            "        },\n" +
            "        \"$startTime_zh_HK$\": \"1735279592000\",\n" +
            "        \"$startTime_en_US$\": \"1735279592000\",\n" +
            "        \"$endTime_zh_HK$\": \"1737957992000\",\n" +
            "        \"$endTime_en_US$\": \"1737957992000\"\n" +
            "    }\n" +
            "}";

    private String createTicketJson = "{\n" +
            "    \"merchantId\": \"2160400000000022\",\n" +
            "    \"userId\": \"2160220043214225\",\n" +
            "    \"templateCode\": \"22024120100135606000000001443495\",\n" +
            "    \"bizSerialId\": \"000000006\",\n" +
            "    \"bizSerialType\": \"OUT_PLAT_FORM\",\n" +
            "    \"startDate\": 1735279592000,\n" +
            "    \"endDate\": 1735538792000,\n" +
            "    \"type\": \"TICKET\",\n" +
            "    \"product\": \"PASS\",\n" +
            "    \"codeInfo\": {\n" +
            "        \"$codemsg$\": \"1\",\n" +
            "        \"$codevalue$\": \"1\"\n" +
            "    },\n" +
            "    \"bizCreate\": 1732787128015,\n" +
            "    \"dataInfo\": {\n" +
            "        \"$address_zh_HK$\": \"香港九龍彌敦道792-804號協成行太子中心703室\",\n" +
            "        \"$address_en_US$\": \"香港九龍彌敦道792-804號協成行太子中心703室\",\n" +
            "        \"$availableTimes_en_US$\": 2,\n" +
            "        \"$availableTimes_zh_HK$\": 2,\n" +
            "        \"$price_zh_HK$\": {\n" +
            "            \"cent\": 800,\n" +
            "            \"currency\": \"HKD\"\n" +
            "        },\n" +
            "        \"$price_en_US$\": {\n" +
            "            \"cent\": 800,\n" +
            "            \"currency\": \"HKD\"\n" +
            "        },\n" +
            "        \"$startTime_zh_HK$\": \"1735279592000\",\n" +
            "        \"$startTime_en_US$\": \"1735279592000\",\n" +
            "        \"$endTime_zh_HK$\": \"1737957992000\",\n" +
            "        \"$endTime_en_US$\": \"1737957992000\"\n" +
            "    }\n" +
            "}";

    private String queryJson = "{\n" +
            "  \"merchantId\": \"2160400000000022\",\n" +
            "  \"templateCode\": \"22024112800135606000000001424755\"\n" +
            "}";

    private String updateTemplateJson = "{\n" +
            "    \"templateCode\": \"22024112800135606000000001424755\",\n" +
            "    \"merchantId\": \"2160400000000022\",\n" +
            "    \"startDate\": 1732851779692,\n" +
            "    \"endDate\": 1735710355000,\n" +
            "    \"type\": \"TICKET\",\n" +
            "    \"codeType\": \"none\",\n" +
            "    \"codeStandard\": \"QRCODE\",\n" +
            "    \"imageUrl\": \"https://imgbeta.ticketebay.com/crossPoster/1d5fde6b68eeaefee27f44cdd5b7eade62704126.png\",\n" +
            "    \"button\": {\n" +
            "        \"btnType\": \"none\",\n" +
            "        \"urlType\": \"CODE_PAY_BTN\"\n" +
            "    },\n" +
            "    \"localeInfo\": {\n" +
            "        \"zh_HK\": {\n" +
            "            \"name\": \"演唱會（zhy測試测试）\",\n" +
            "            \"subName\": \"演唱會（zhy測試测试）\",\n" +
            "            \"description\": \"演出時長：120 minute\\n入場時間：入場時間入場時間入場時間\\n限購說明：每單限購6張\\n座位類型：請按門票對應座位，有序對號入座\\n兒童入場提示：1.2公尺以上憑票入場，1.2公尺以下謝絕入場\\n演出語言：演出語言演出語言演出語言\\n演出形式：演出形式演出形式演出形式\\n其他說明：其他說明其他說明其他說明其他說明其他說明\\n實體票：本項目支持憑實體票入場，支持以下取票方式：\\n- 快遞配送：需支付郵費，具體金額以訂單頁展示爲準，順豐發貨。\\n- 現場取票：工作人員將在演出開場前1小時至現場派票。工作人員聯繫方式、具體取票地址將在演出當天以短信或郵箱通知爲準。\\n\\n電子票：\\n\",\n" +
            "            \"brandName\": \"Ticketebay\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"merchantLogo\": \"https://opbeta.ticketebay.com/img/logo.f20dabd.png\",\n" +
            "    \"orderPageLink\": \"alipayhk://platformapi/startapp?appId=2102020211542667&page=pages/activity/detail/index&query=id%3D211802\",\n" +
            "    \"detailLink\": \"alipayhk://platformapi/startapp?appId=2102020211542667&page=pages/activity/detail/index&query=id%3D211802\",\n" +
            "    \"categories\": [\n" +
            "        \"0002000100020009\"\n" +
            "    ],\n" +
            "\n" +
            "    \"exposure\": \"hidden\"  \n" +
            "}";


    private String createTemplateJson = "{\n" +
            "    \"requestId\": \"1212121112010011\",\n" +
            "    \"merchantId\": \"2160400000000022\",\n" +
            "    \"startDate\": 1733107363121,\n" +
            "    \"endDate\": 1735710355000,\n" +
            "    \"type\": \"TICKET\",\n" +
            "    \"product\": \"PASS\",\n" +
            "    \"codeType\": \"none\",\n" +
            "    \"codeStandard\": \"\",\n" +
            "    \"imageUrl\": \"https://imgbeta.ticketebay.com/crossPoster/1d5fde6b68eeaefee27f44cdd5b7eade62704126.png\",\n" +
            "    \"button\": {\n" +
            "        \"btnType\": \"none\",\n" +
            "        \"urlType\": \"CODE_PAY_BTN\"\n" +
            "    },\n" +
            "    \"localeInfo\": {\n" +
            "        \"zh_HK\": {\n" +
            "            \"name\": \"【新加坡】zhy test\",\n" +
            "            \"subName\": \"【新加坡】zhy test\",\n" +
            "            \"description\": \"1.請向入口職員展示以上電子票2.每張電子票只限一人使用及使用一次2.持電子票人士可於指定特別通道換領幸運卡乙張\",\n" +
            "            \"brandName\": \"Ticketebay\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"merchantLogo\": \"https://opbeta.ticketebay.com/img/logo.f20dabd.png\",\n" +
            "    \"orderPageLink\": \"alipayhk://platformapi/startapp?appId=2102020211542667&page=pages/activity/detail/index&query=id%3D211802\",\n" +
            "    \"detailLink\": \"alipayhk://platformapi/startapp?appId=2102020211542667&page=pages/activity/detail/index&query=id%3D211802\",\n" +
            "    \"categories\": [\n" +
            "        \"0002000100020009\"\n" +
            "    ],\n" +
            "\n" +
            "    \"exposure\": \"exposed\"  \n" +
            "}";




    @Test
    public void test1() {


        // 创建一个 Date 对象，这里假设你已经有了一个 Date 对象
        Date date = DateUtils.addMinutes(new Date(1733128200000L), 15); // 示例中使用当前时间，你可以替换为你自己的 Date 对象

        // 创建 SimpleDateFormat 对象，并设置格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

        //// 设置时区为 +08:30
        //sdf.setTimeZone(TimeZone.getDefault());

        // 格式化日期
        String formattedDate = sdf.format(date);

        // 输出结果
        System.out.println(formattedDate);

    }


}
