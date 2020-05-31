package com.hoomoomoo.im.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author hoomoomoo
 * @description 登入日志查询实体类
 * @package com.hoomoomoo.im.model
 * @date 2019/10/26
 */

@Data
public class SysLoginLogQueryModel extends QueryBaseModel {

    @ApiModelProperty(value="日志ID", required = false)
    private String logId;

    @ApiModelProperty(value="用户ID", required = false)
    private String userId;

    @ApiModelProperty(value="登入时间", required = false)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date loginDate;

    @ApiModelProperty(value="登出时间", required = false)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date logoutDate;

    @ApiModelProperty(value="登入状态", required = false)
    private String loginStatus;

    @ApiModelProperty(value="登入消息", required = false)
    private String loginMessage;
}
