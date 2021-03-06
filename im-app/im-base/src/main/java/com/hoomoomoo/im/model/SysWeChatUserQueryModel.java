package com.hoomoomoo.im.model;

import com.hoomoomoo.im.model.base.QueryBaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description 微信用户信息查询实体类
 * @package com.hoomoomoo.im.model
 * @date 2020/02/28
 */

@Data
public class SysWeChatUserQueryModel extends QueryBaseModel {

    @ApiModelProperty(value="微信用户ID", required = false)
    private String weChatUserId;

    @ApiModelProperty(value="微信公众号ID", required = false)
    private String weChatPublicId;

    @ApiModelProperty(value="是否身份验证", required = false)
    private String isAuth;

    @ApiModelProperty(value="用户ID", required = false)
    private String userId;
}
