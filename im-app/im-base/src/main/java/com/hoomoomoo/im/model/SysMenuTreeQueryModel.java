package com.hoomoomoo.im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description 菜单树信息查询实体类
 * @package com.hoomoomoo.im.model
 * @date 2019/09/29
 */

@Data
public class SysMenuTreeQueryModel {

    @ApiModelProperty(value="父级菜单ID", required = false)
    private String parentId;

    @ApiModelProperty(value="是否父级菜单", required = false)
    private Boolean isParentId;

    @ApiModelProperty(value="是否禁用", required = false)
    private String disabled;

    @ApiModelProperty(value="角色ID", required = false)
    private String roleId;

    @ApiModelProperty(value="用户ID", required = false)
    private String userId;
}
