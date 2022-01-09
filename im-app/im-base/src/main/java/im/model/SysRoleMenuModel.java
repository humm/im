package im.model;

import im.model.base.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description 角色菜单实体类
 * @package im.model
 * @date 2019/10/08
 */

@Data
public class SysRoleMenuModel extends BaseModel {

    @ApiModelProperty(value="角色菜单ID", required = false)
    private String RoleMenuId;

    @ApiModelProperty(value="菜单ID", required = false)
    private String menuId;

    @ApiModelProperty(value="角色ID", required = false)
    private String roleId;
}
