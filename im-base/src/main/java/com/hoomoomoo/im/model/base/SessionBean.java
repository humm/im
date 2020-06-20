package com.hoomoomoo.im.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description Session信息
 * @package com.hoomoomoo.im.model.common
 * @date 2019/08/08
 */

@Data
public class SessionBean {

    @ApiModelProperty(value="用户ID", required = false)
    private String userId;

    @ApiModelProperty(value="用户代码", required = false)
    private String userCode;

    @ApiModelProperty(value="用户名称", required = false)
    private String userName;

    @ApiModelProperty(value="用户状态", required = false)
    private String userStatus;

    @ApiModelProperty(value="是否管理员数据权限", required = false)
    private Boolean isAdminData;

}
