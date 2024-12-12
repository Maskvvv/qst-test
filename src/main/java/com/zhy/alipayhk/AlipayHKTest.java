package com.zhy.alipayhk;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.marketing.pass.models.AlipayPassTemplateAddResponse;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePrecreateResponse;
import com.alipay.global.api.AlipayClient;
import com.alipay.global.api.DefaultAlipayClient;
import com.alipay.global.api.model.ams.ProductCodeType;
import com.alipay.global.api.request.ams.pay.AlipayPayRequest;
import com.alipay.global.api.response.ams.pay.AlipayPayResponse;
import com.alipay.global.api.tools.SignatureTool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

import com.alipay.easysdk.factory.Factory;

/**
 * @author zhouhongyin
 * @since 2024/11/27 20:08
 */
@Slf4j
public class AlipayHKTest {


    String signatureCipher(String httpMethod, String uri, String clientId, String requestTime, String httpBody) {

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
    }

    ;

    HttpHeaders httpHeaders(String clientId, String requestTime, String signatureText) {
        HttpHeaders requestHeaders = new HttpHeaders();

        requestHeaders.add(AliPayHKConst.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        requestHeaders.add(AliPayHKConst.CLIENT_ID_REQUEST_SIDE, clientId);
        requestHeaders.add(AliPayHKConst.REQUEST_TIME_REQUEST_SIDE, requestTime);
        requestHeaders.add(AliPayHKConst.SIGNATURE, signatureText);
        return requestHeaders;
    }

    Map<String, String> httpHeaderMap(String clientId, String requestTime, String signatureText) {
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
        String host = AliPayHKConst.DOMAIN_PRE + uri;
        log.info("Host generated: {}", host); // 补充日志
        return host;
    }


    //ResponseEntity<String> sendHttpRequest(String clientId, String uri, String requestBody, String signatureCipher, String requestTime) {
    //    HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders(clientId, requestTime, signatureCipher));
    //    String host = getHost(uri);
    //    RestTemplate restTemplate = new RestTemplate();
    //    ResponseEntity<String> response = restTemplate.postForEntity(host, entity, String.class);
    //    if (!HttpStatus.OK.equals(response.getStatusCode())) {
    //        log.error("Failed to invoke Alipay HK server. Status: {}, Response: {}", response.getStatusCode(), response.getBody()); // 补充日志
    //        throw new RuntimeException("translateFunction().apply(I18nKey.EXCEPTION_REMOTE_REQUEST_ERROR)");
    //    }
    //    return response;
    //}

    ResponseEntity<String> sendHttpRequest(String clientId, String uri, String requestBody, String signatureCipher, String requestTime) {
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders(clientId, requestTime, signatureCipher));
        String host = getHost(uri);


        HttpResponse execute = HttpRequest.post(host)
                .header(AliPayHKConst.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(AliPayHKConst.CLIENT_ID_REQUEST_SIDE, clientId)
                .header(AliPayHKConst.REQUEST_TIME_REQUEST_SIDE, requestTime)
                .header(AliPayHKConst.SIGNATURE, signatureCipher)
                .body(requestBody)
                .timeout(20000)//超时，毫秒
                .execute();

        Map<String, List<String>> headers = execute.headers();
        System.out.println(JSON.toJSONString(headers));


        System.out.println("tracerId: " + headers.get("tracerId"));
        System.out.println(JSON.toJSONString(execute.body()));



        return null;
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
    public void createTemplate1() throws Exception {

        //Factory.Marketing.Pass().createTemplate("00100101", );
    }


    private String createTicketJson = "{\n" +
            "    \"merchantId\": \"2160400000000022\",\n" +
            "    \"userId\": \"2160220043214225\",\n" +
            "    \"templateCode\": \"22024112800135606000000001424755\",\n" +
            "    \"bizSerialId\": \"000000003\",\n" +
            "    \"bizSerialType\": \"OUT_PLAT_FORM\",\n" +
            "    \"startDate\": 1735279592000,\n" +
            "    \"endDate\": 1735538792000,\n" +
            "    \"type\": \"TICKET\",\n" +
            "    \"product\": \"PASS\",\n" +
            "    \"codeInfo\": {\n" +
            "        \"$codemsg$\": \"\",\n" +
            "        \"$codevalue$\": \"\"\n" +
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
            "    \"product\": \"PASS\",\n" +
            "    \"codeType\": \"none\",\n" +
            "    \"codeStandard\": \"QRCODE\",\n" +
            "    \"imageUrl\": \"https://imgbeta.ticketebay.com/crossPoster/1d5fde6b68eeaefee27f44cdd5b7eade62704126.png\",\n" +
            "    \"button\": {\n" +
            "        \"btnType\": \"none\",\n" +
            "        \"urlType\": \"CODE_PAY_BTN\"\n" +
            "    },\n" +
            "    \"localeInfo\": {\n" +
            "        \"zh_HK\": {\n" +
            "            \"name\": \"香港迪士尼樂園一日入場票test\",\n" +
            "            \"subName\": \"香港迪士尼樂園特價票test\",\n" +
            "            \"description\": \"1.請向入口職員展示以上電子票2.每張電子票只限一人使用及使用一次2.持電子票人士可於指定特別通道換領幸運卡乙張\",\n" +
            "            \"brandName\": \"香港迪士尼樂園\"\n" +
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


    private static String createTemplateJson = "{\n" +
            "    \"requestId\": \"1212121112010003\",\n" +
            "    \"merchantId\": \"2160400000000022\",\n" +
            "    \"startDate\": 1732851779692,\n" +
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
            "            \"name\": \"【新加坡】Atarashii Gakko！ World Tour in Singapore新學校領袖演唱會\",\n" +
            "            \"subName\": \"【新加坡】Atarashii Gakko！ World Tour in Singapore新學校領袖演唱會\",\n" +
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


}
