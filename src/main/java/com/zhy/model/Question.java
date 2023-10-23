package com.zhy.model;

import lombok.Data;

/**
 * <p> </p>
 *
 * @author zhouhongyin
 * @since 2023/10/23 9:57
 */
@Data
public class Question {
    private String stageId;
    private Long endTimestamp;
    private String trackForm;
    private String stageName;
    private String trackFormTitle;
    private String questionStatus;
    private String trackName;
    private String questionName;
    private String trackId;
    private String questionId;
    private String competitionId;
    private String questionStatusTitle;
}
