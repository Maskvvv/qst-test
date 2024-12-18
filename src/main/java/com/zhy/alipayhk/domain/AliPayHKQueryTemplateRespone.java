package com.zhy.alipayhk.domain;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * {@link <a href="https://docs.alipay.hk/alipayhkdocs/hk/gcmc/api-create_template">...</a>}
 *
 *
 * @author zhouhongyin
 * @since 2024/11/28 15:39
 */
@Data
public class AliPayHKQueryTemplateRespone implements Serializable {

    private AlipayHKResultResp result;

    private String templateCode;

    private Long startDate;

    private Long endDate;

    private String status;

    private String codeType;

    private String codeStandard;

    private String imageUrl;

    private Button button = new Button();

    private LocaleInfo localeInfo;

    private String merchantLogo;

    private Integer currentAmountCent;

    private Integer originalAmountCent;

    private String currencyCode;

    private String discount;

    private Long stock;

    private Long availableStock;

    private String orderPageLink;

    private String detailLink;

    private List<String> categories;

    private String bizMids;

    /**
     * @see AlipayHkMerchantRecord.Exposure
     */
    private String exposure;

    /**
     * @see PassExtInfo
     */
    private Object passExtInfo;


    @Data
    public static class Button implements Serializable {


        private Boolean browserOpen;

        /**
         * Type of the button.
         * <p>
         * usage: Use immediately.
         * payment: Redirect to wallet payment QR code page.
         * none: Do not display button.
         */
        private String btnType;

        private String btnUrl;

        private String urlType ;

    }

    @Data
    public static class LocaleInfo implements Serializable {

        private LocaleInfoZhHK zh_HK;

        private LocaleInfoENUS en_US;

    }

    @Data
    public static class LocaleInfoZhHK implements Serializable {

        private String name;

        private String subName;

        private String description;

        private String brandName;

    }

    @Data
    public static class LocaleInfoENUS implements Serializable {

        private String name;

        private String subName;

        private String description;

        private String brandName;

    }

    @Data
    public static class PassExtInfo implements Serializable {

        private List<String> tags;

        private String country;

        private String province;

        private String city;

        private String district;

        private String businessArea;

        private Long soldNum;

        private Long userNum;

        private String rating;

        private Long rateNum;

    }

}
