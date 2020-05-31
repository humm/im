package com.hoomoomoo.im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description 角色信息查询实体类
 * @package com.hoomoomoo.im.model
 * @date 2019/09/27
 */

@Data
public class SysRoleQueryModel extends QueryBaseModel {

    @ApiModelProperty(value="角色ID", required = false)
    private String roleId;

    @ApiModelProperty(value="角色代码", required = false)
    private String roleCode;

    @ApiModelProperty(value="角色名称", required = false)
    private String roleName;

}
