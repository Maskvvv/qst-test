package com.zhy.alipayhk;

import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

/**
 * {@link <a href="https://docs.alipay.hk/alipayhkdocs/hk/gcmc/api-create_pass">...</a>}
 *
 {
     "merchantId": "2160400000000022",
     "userId": "2160220043214225",
     "passId": "2024120219027102160229300021201",
     "status": "REFUND",
     "dataInfo": {
         "$address_zh_HK$": "香港九龍彌敦道792-804號協成行太子中心703室test",
         "$address_en_US$": "香港九龍彌敦道792-804號協成行太子中心703室test",
         "$availableTimes_en_US$": 2,
         "$availableTimes_zh_HK$": 2,
         "$price_zh_HK$": {
             "cent": 800,
             "currency": "HKD"
         },
         "$price_en_US$": {
             "cent": 800,
             "currency": "HKD"
         },
         "$startTime_zh_HK$": "1735279592000",
         "$startTime_en_US$": "1735279592000",
         "$endTime_zh_HK$": "1737957992000",
         "$endTime_en_US$": "1737957992000"
     }
 }
 *
 * @author zhouhongyin
 * @since 2024/11/28 15:39
 */
@Data
public class AliPayHKUpdateTicketRequest implements Serializable {

    private String merchantId;

    private String userId;

    private String passId;

    /**
     * Update status of the redeemable card / ticket.
     *
     * USED: Move this pass to used page.
     * CLOSED: Remove this pass completely from the wallet.
     * UPDATE: Update the content of this pass.
     * REFUND: This pass is refunded.
     */
    private String status;

    /**
     * @see AdmissionTicket
     */
    private Object dataInfo;

    /**
     * UPDATE_AIR_TICKET
     * UPDATE_TRAIN_TICKET
     * UPDATE_BUS_TICKET
     * UPDATE_SHIP_TICKET
     * UPDATE_MOVIE
     * UPDATE_PASS
     */
    private String updateDetailType;

    private Long bizDate;

    @Data
    public static class AdmissionTicket implements Serializable {

        private String $ticketName_zh_HK$;
        private String $ticketName_en_US$;

        private String $address_zh_HK$;
        private String $address_en_US$;

        private Long $availableTimes_zh_HK$;
        private Long $availableTimes_en_US$;

        private Price $price_zh_HK$;
        private Price $price_en_US$;

        private Long $startTime_zh_HK$;
        private Long $startTime_en_US$;

        private Long $endTime_zh_HK$;
        private Long $endTime_en_US$;

        private String $platformName_zh_HK$ = "Ticketebay";
        private String $platformName_en_US$ = "Ticketebay";

        private String $platformLogo_zh_HK$;
        private String $platformLogo_en_US$;

        public void set$ticketName_zh_HK$(String $ticketName_zh_HK$) {
            this.$ticketName_zh_HK$ = $ticketName_zh_HK$;
        }

        public void set$ticketName_en_US$(String $ticketName_en_US$) {
            this.$ticketName_en_US$ = $ticketName_en_US$;
        }

        public void set$address_zh_HK$(String $address_zh_HK$) {
            this.$address_zh_HK$ = $address_zh_HK$;
        }

        public void set$address_en_US$(String $address_en_US$) {
            this.$address_en_US$ = $address_en_US$;
        }

        public void set$availableTimes_zh_HK$(Long $availableTimes_zh_HK$) {
            this.$availableTimes_zh_HK$ = $availableTimes_zh_HK$;
        }

        public void set$availableTimes_en_US$(Long $availableTimes_en_US$) {
            this.$availableTimes_en_US$ = $availableTimes_en_US$;
        }

        public void set$price_zh_HK$(Price $price_zh_HK$) {
            this.$price_zh_HK$ = $price_zh_HK$;
        }

        public void set$price_en_US$(Price $price_en_US$) {
            this.$price_en_US$ = $price_en_US$;
        }

        public void set$startTime_zh_HK$(Long $startTime_zh_HK$) {
            this.$startTime_zh_HK$ = $startTime_zh_HK$;
        }

        public void set$startTime_en_US$(Long $startTime_en_US$) {
            this.$startTime_en_US$ = $startTime_en_US$;
        }

        public void set$endTime_zh_HK$(Long $endTime_zh_HK$) {
            this.$endTime_zh_HK$ = $endTime_zh_HK$;
        }

        public void set$endTime_en_US$(Long $endTime_en_US$) {
            this.$endTime_en_US$ = $endTime_en_US$;
        }

        public void set$platformName_zh_HK$(String $platformName_zh_HK$) {
            this.$platformName_zh_HK$ = $platformName_zh_HK$;
        }

        public void set$platformName_en_US$(String $platformName_en_US$) {
            this.$platformName_en_US$ = $platformName_en_US$;
        }

        public void set$platformLogo_zh_HK$(String $platformLogo_zh_HK$) {
            this.$platformLogo_zh_HK$ = $platformLogo_zh_HK$;
        }

        public void set$platformLogo_en_US$(String $platformLogo_en_US$) {
            this.$platformLogo_en_US$ = $platformLogo_en_US$;
        }

        public String get$ticketName_zh_HK$() {
            return $ticketName_zh_HK$;
        }

        public String get$ticketName_en_US$() {
            return $ticketName_en_US$;
        }

        public String get$address_zh_HK$() {
            return $address_zh_HK$;
        }

        public String get$address_en_US$() {
            return $address_en_US$;
        }

        public Long get$availableTimes_zh_HK$() {
            return $availableTimes_zh_HK$;
        }

        public Long get$availableTimes_en_US$() {
            return $availableTimes_en_US$;
        }

        public Price get$price_zh_HK$() {
            return $price_zh_HK$;
        }

        public Price get$price_en_US$() {
            return $price_en_US$;
        }

        public Long get$startTime_zh_HK$() {
            return $startTime_zh_HK$;
        }

        public Long get$startTime_en_US$() {
            return $startTime_en_US$;
        }

        public Long get$endTime_zh_HK$() {
            return $endTime_zh_HK$;
        }

        public Long get$endTime_en_US$() {
            return $endTime_en_US$;
        }

        public String get$platformName_zh_HK$() {
            return $platformName_zh_HK$;
        }

        public String get$platformName_en_US$() {
            return $platformName_en_US$;
        }

        public String get$platformLogo_zh_HK$() {
            return $platformLogo_zh_HK$;
        }

        public String get$platformLogo_en_US$() {
            return $platformLogo_en_US$;
        }
    }

    @Data
    public static class Price implements Serializable {
        private String cent;
        private String currency;
    }

    @Getter
    public static enum Status {
        USED("USED"),
        CLOSED("CLOSED"),
        UPDATE("UPDATE"),
        REFUND("REFUND"),
        ;

        private final String value;

        Status(String value) {
            this.value = value;
        }
    }
}
