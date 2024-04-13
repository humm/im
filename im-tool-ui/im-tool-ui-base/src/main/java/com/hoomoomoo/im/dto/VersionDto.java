package com.hoomoomoo.im.dto;

import lombok.Data;

@Data
public class VersionDto extends BaseDto{

    private String code;
    private String closeDate;
    private String publishDate;
    private String clientName;
    private String closeInterval;
    private String publishInterval;
    private String memo;
    private String orderNo;
}
