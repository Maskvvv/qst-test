package com.zhy.utils;

import lombok.Data;

/**
 * @author zhouhongyin
 * @since 2023/5/15 15:11
 */
@Data
public class InternshipExcel {

    private String sort;
    private String companyName;
    private String name;
    private String contact ;
    private String id;
    private Integer duplicates = 0;

    public void incrDuplicates () {
        duplicates++;
    }
}
