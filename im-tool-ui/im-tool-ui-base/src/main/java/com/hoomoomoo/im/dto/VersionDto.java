package com.hoomoomoo.im.dto;

import lombok.Data;

@Data
public class VersionDto extends BaseDto{

    private String code;
    private String name;
    private String closeDate;
    private String publishDate;
    private String endData;
    private String closeInterval;
    private String publishInterval;
    private String memo;
    private String orderNo;
    private String customOrderNo;
}
