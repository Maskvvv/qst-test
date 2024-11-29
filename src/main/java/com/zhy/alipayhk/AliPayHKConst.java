package com.zhy.alipayhk;

/**
 * @Author: 娄须强
 * @CreateTime: 2024-11-06  15:00
 */
public class AliPayHKConst {

    public static String CLIENT_ID = "";

    public static String DOMAIN_PRE = "https://business.dl.alipaydev.com";
    public static String DOMAIN_PROD = "https://open-id-pre.alipay.com";

    /*********************** 签名参数 ****************************/
    /**请求端**/
    public static String CONTENT_TYPE = "Content-Type";
    public static String CLIENT_ID_REQUEST_SIDE = "Client-Id";
    public static String REQUEST_TIME_REQUEST_SIDE = "Request-Time";

    /**响应端**/
    public static String CLIENT_ID_RESPONSE_SIDE = "client-id";
    public static String RESPONSE_TIME_RESPONSE_SIDE = "response-time";

    public static String SIGNATURE = "Signature";


    public static final String CREATE_TEMPLATE_URL = "/api/open/alipay/v1/ipass/template/manage/createTemplate";
    public static final String UPDATE_TEMPLATE_URL = "/api/open/alipay/v1/ipass/template/manage/updateTemplate";
    public static final String QUERY_TEMPLATE_URL = "/api/open/alipay/v1/ipass/template/manage/detail";
    public static final String CREATE_TICKET_URL = "/api/open/alipay/v1/ipass/pass/manage/createPass";
}
