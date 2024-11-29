package com.zhy.alipayhk;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

/**
 * @author zd.h
 * @date 10/15/24 14:56
 */
@Slf4j
public class AliPayHkSignatureUtils {

    private static final String sign_template = "%s %s\n" +
        "%s.%s.%s";

    public static final String SIGN_TEMPLATE_HEADER = "algorithm=RSA256, keyVersion=0, signature=%s";


    /**
     * ticket ebay的私钥 和 支付宝控制台【Merchant Public Key】相对应
     */
    public static String PRIVATE_KEY = "";
    /**
     * 支付宝的公钥 取自支付宝控制台【Platform Public Key】
     */
    public static String PUBLIC_KEY_OF_ALIPAY = "";

    /**
     * 解密验签
     */
    public static boolean decryptedAndVerified(String signatureCipher, String httpMethod, String uri, String clientId, String requestTime, String httpBody) {
        if (StringUtils.isBlank(signatureCipher)) {
            return false;
        }
        if (StringUtils.isBlank(httpMethod)) {
            return false;
        }
        if (StringUtils.isBlank(uri)) {
            return false;
        }
        if (StringUtils.isBlank(clientId)) {
            return false;
        }
        if (StringUtils.isBlank(requestTime)) {
            return false;
        }
        return verify(paramsJoin(httpMethod, uri, clientId, requestTime, httpBody), PUBLIC_KEY_OF_ALIPAY, signatureCipher);
    }

    /**
     * 加密
     */
    public static String encrypting(String httpMethod, String uri, String clientId, String requestTime, String httpBody) {
        return sign(paramsJoin(httpMethod, uri, clientId, requestTime, httpBody), PRIVATE_KEY);
    }

    /**
     * 进行验证
     *
     * @param signatureToBeVerified 数字签名
     * @param preSignString         待验数据
     **/

    public static boolean verify(String preSignString, String publicKey, String signatureToBeVerified) {
        try {
            java.security.Signature signature = java.security.Signature.getInstance("SHA256withRSA");
            PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(
                new X509EncodedKeySpec(Base64.getDecoder().decode((publicKey.getBytes("UTF-8")))));
            signature.initVerify(pubKey);
            signature.update(preSignString.getBytes("UTF-8"));
            return signature.verify(Base64.getDecoder().decode(URLDecoder.decode(signatureToBeVerified, "UTF-8").getBytes("UTF-8")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static String sign(String contentToBeSigned, String privateKey) {
        try {
            java.security.Signature signature = java.security.Signature.getInstance("SHA256withRSA");
            PrivateKey priKey = KeyFactory.getInstance("RSA").generatePrivate(
                new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey.getBytes("UTF-8"))));
            signature.initSign(priKey);
            signature.update(contentToBeSigned.getBytes("UTF-8"));
            byte[] signed = signature.sign();
            String generatedSignature = URLEncoder.encode(new String(Base64.getEncoder().encode(signed), "UTF-8"), "UTF-8");
            return String.format(SIGN_TEMPLATE_HEADER, generatedSignature);
        } catch (Exception e) {
            log.error("香港支付宝小程序签名失败. ", e);
            return null;
        }
    }


    private static String paramsJoin(String httpMethod, String uri, String clientId, String requestTime, String httpBody) {
        Objects.requireNonNull(httpMethod, "httpMethod must not null");
        Objects.requireNonNull(uri, "uri must not null");
        Objects.requireNonNull(clientId, "clientId must not null");
        Objects.requireNonNull(requestTime, "requestTime must not null");
        Objects.requireNonNull(httpBody, "httpBody must not null");
        return String.format(sign_template, httpMethod, uri, clientId, requestTime, httpBody);
    }

}
