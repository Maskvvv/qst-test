package com.zhy.model;

import lombok.Data;

import java.util.Date;

/**
 * @author zhouhongyin
 * @since 2024/3/25 10:02
 */
@Data
public class MailType {

    private int typeId;// 自增主键
    private short rank;// 优先级
    private int channel; //短信通道
    private String subject; //邮件标题
    private String template; //邮件模板
    private String memo;// 描述
    private Date addTime;// 添加时间


}
