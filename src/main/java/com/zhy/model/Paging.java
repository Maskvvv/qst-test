package com.zhy.model;

import lombok.Data;

import java.util.List;

/**
 * <p> </p>
 *
 * @author zhouhongyin
 * @since 2023/10/23 9:58
 */
@Data
public class Paging<T> {
    private Integer limit;
    private Integer total;
    private List<T> rows;
    private Integer pageSize;
    private Integer offset;
    private Integer pageNumber;
}
