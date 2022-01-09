package im.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import im.model.base.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description 用户信息实体类
 * @package com.hoomoomoo.im.model
 * @date 2019/08/11
 */

@Data
public class SysUserModel extends BaseModel {

    @ApiModelProperty(value="用户ID", required = false)
    private String userId;

    @ApiModelProperty(value="用户代码", required = false)
    private String userCode;

    @ApiModelProperty(value="用户名称", required = false)
    private String userName;

    @JsonIgnore
    @ApiModelProperty(value="用户密码", required = false)
    private String userPassword;

    @ApiModelProperty(value="用户状态", required = false)
    private String userStatus;

    @ApiModelProperty(value="用户状态", required = false)
    private String userStatusCode;

    @ApiModelProperty(value="备注", required = false)
    private String userMemo;

    @ApiModelProperty(value="角色代码", required = false)
    private String roleId;

    @ApiModelProperty(value="记住密码", required = false)
    private String rememberPassword;

}
