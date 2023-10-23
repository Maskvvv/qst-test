package com.zhy.model;

import lombok.Data;

/**
 * <p> </p>
 *
 * @author zhouhongyin
 * @since 2023/10/23 10:21
 */
@Data
public class Delivery {
    private String deliverableId;
    private String schoolName;
    private String customerId;
    private String areaName;
    private String leaderName;
    private Long committedTime;
    private String deliverablePath;
    private String questionId;
    private String deliverableName;
}
