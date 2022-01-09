package im.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import im.model.base.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author hoomoomoo
 * @description 消息通知实体类
 * @package com.hoomoomoo.im.model
 * @date 2019/09/06
 */

@Data
public class SysNoticeModel extends BaseModel {

    @ApiModelProperty(value="通知ID", required = false)
    private String noticeId;

    @ApiModelProperty(value="用户ID", required = false)
    private String userId;

    @ApiModelProperty(value="业务ID", required = false)
    private String businessId;

    @ApiModelProperty(value="业务类型", required = false)
    private String businessType;

    @ApiModelProperty(value="业务类型代码", required = false)
    private String businessTypeCode;

    @ApiModelProperty(value="业务子类型", required = false)
    private String businessSubType;

    @ApiModelProperty(value="业务子类型代码", required = false)
    private String businessSubTypeCode;

    @ApiModelProperty(value="业务日期", required = false)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date businessDate;

    @ApiModelProperty(value="业务子类型", required = false)
    private String businessAmount;

    @ApiModelProperty(value="通知状态", required = false)
    private String noticeStatus;

    @ApiModelProperty(value="通知状态代码", required = false)
    private String noticeStatusCode;

    @ApiModelProperty(value="通知类型", required = false)
    private String noticeType;

    @ApiModelProperty(value="通知类型代码", required = false)
    private String noticeTypeCode;

    @ApiModelProperty(value="阅读状态", required = false)
    private String readStatus;

    @ApiModelProperty(value="阅读状态代码", required = false)
    private String readStatusCode;
}
