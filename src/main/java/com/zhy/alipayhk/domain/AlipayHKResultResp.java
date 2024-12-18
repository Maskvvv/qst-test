package com.zhy.alipayhk.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class AlipayHKResultResp implements Serializable {
    public final static String SUCCESS_RESULT_STATUS = "S";

    private String resultCode;
    private String resultStatus;
    private String resultMessage;

    public boolean isSuccess() {
        return SUCCESS_RESULT_STATUS.equals(this.resultStatus);
    }
}
