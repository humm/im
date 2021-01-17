package com.hoomoomoo.im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description 微信消息公共实体类
 * @package com.hoomoomoo.im.model
 * @date 2020/02/27
 */

@Data
public class SysWeChatBaseModel {

    @ApiModelProperty(value="消息ID", required = false)
    private String MsgId;

    @ApiModelProperty(value="接收方帐号(收到的OpenID)", required = false)
    private String ToUserName;

    @ApiModelProperty(value="开发者微信号", required = false)
    private String FromUserName;

    @ApiModelProperty(value="消息创建时间(整型)", required = false)
    private Long CreateTime;

    @ApiModelProperty(value="消息类型", required = false)
    private String MsgType;

    @ApiModelProperty(value="星标刚收到的消息", required = false)
    private Integer FuncFlag;

}
