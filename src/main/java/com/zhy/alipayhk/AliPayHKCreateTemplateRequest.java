package com.zhy.alipayhk;


import cn.hutool.core.collection.ListUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * {@link <a href="https://docs.alipay.hk/alipayhkdocs/hk/gcmc/api-create_template">...</a>}
 *
  {
      "requestId": "1212121112010010",
      "merchantId": "2160400000000022",
      "startDate": 1732777002163,
      "endDate": 1738043155000,
      "type": "TICKET",
      "product": "PASS",
      "codeType": "none",
      "codeStandard": "QRCODE",
      "imageUrl": "https://imgbeta.ticketebay.com/crossPoster/1d5fde6b68eeaefee27f44cdd5b7eade62704126.png",
      "button": {
          "btnType": "none",
          "urlType": "CODE_PAY_BTN"
      },
      "localeInfo": {
          "zh_HK": {
              "name": "香港迪士尼樂園一日入場票",
              "subName": "香港迪士尼樂園特價票",
              "description": "1.請向入口職員展示以上電子票2.每張電子票只限一人使用及使用一次2.持電子票人士可於指定特別通道換領幸運卡乙張",
             "brandName": "香港迪士尼樂園"
         }
     },
     "merchantLogo": "https://opbeta.ticketebay.com/img/logo.f20dabd.png",
     "orderPageLink": "alipayhk://platformapi/startapp?appId=2102020211542667&page=pages/activity/detail/index&query=id%3D211802",
     "detailLink": "alipayhk://platformapi/startapp?appId=2102020211542667&page=pages/activity/detail/index&query=id%3D211802",
     "categories": [
         "0002000100020009"
     ],
     "exposure": "exposed"
 }

 *
 * @author zhouhongyin
 * @since 2024/11/28 15:39
 */
@Data
public class AliPayHKCreateTemplateRequest implements Serializable {

    private String requestId;

    private String merchantId;

    private Long startDate;

    private Long endDate;

    /**
     * Type of template.
     * <p>
     * COUPON: Redeemable card scenarios.
     * TICKET: Ticket scenarios.
     */
    private String type = "TICKET";

    /**
     * COUPON
     * <p>
     * GIFT_CARD: Redeemable cards, i.e. coupon / gift card / membership card.
     * <p>
     * TICKET
     * <p>
     * PASS: Admission ticket.
     * <p>
     * MOVIE: Movie ticket.
     * <p>
     * AIR_TICKET: Flight ticket.
     * <p>
     * TRAIN_TICKET: Train ticket.
     * <p>
     * BUS_TICKET: Bus ticket.
     * <p>
     * SHIP_TICKET: Ship ticket.
     */
    private String product = "PASS";

    private String codeType = "none";

    private String codeStandard = "";

    private String imageUrl;

    private Button button = new Button();

    private LocaleInfo localeInfo;

    private String merchantLogo;

    private Integer currentAmountCent;

    private Integer originalAmountCent;

    private String currencyCode;

    private String stock;

    private String orderPageLink;

    private String detailLink;

    private List<String> categories = ListUtil.of("0002000100020009");

    private String bizMids;

    private String exposure ="hidden";


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
        private String btnType = "none";

        private String btnUrl;

        private String urlType = "CODE_PAY_BTN";

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

        private String description = "";

        private String brandName = "Ticketebay";

    }

    @Data
    public static class LocaleInfoENUS implements Serializable {

        private String name;

        private String subName;

        private String description;

        private String brandName;

    }

}
