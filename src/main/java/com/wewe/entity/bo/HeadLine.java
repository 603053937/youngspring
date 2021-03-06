package com.wewe.entity.bo;


import lombok.Data;

import java.util.Date;
/**
 *  首页头条显示
 */
@Data
public class HeadLine {
    private Long lineId;
    private String lineName;
    private  String lineLink;
    private String lineImg;
    private Integer priority;
    private Integer enableStatus;
    private Date createTime;
    private Date lastEditTime;
}
