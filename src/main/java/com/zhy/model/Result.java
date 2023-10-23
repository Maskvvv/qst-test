package com.zhy.model;

import lombok.Data;

/**
 * <p> </p>
 *
 * @author zhouhongyin
 * @since 2023/10/23 9:53
 */
@Data
public class Result<T> {
    private Integer status;
    private Boolean success;
    private String message;
    private String tracer;
    private T data;
    private Integer code;

}
