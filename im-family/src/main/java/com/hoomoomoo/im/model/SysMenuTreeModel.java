package com.hoomoomoo.im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hoomoomoo
 * @description 菜单树信息实体类
 * @package com.hoomoomoo.im.model
 * @date 2019/09/29
 */

@Data
public class SysMenuTreeModel extends BaseModel {

    @ApiModelProperty(value="菜单ID", required = false)
    private String id;

    @ApiModelProperty(value="菜单名称", required = false)
    private String title;

    @ApiModelProperty(value="是否选中", required = false)
    private Boolean checked;

    @ApiModelProperty(value="是否展开", required = false)
    private Boolean spread;

    @ApiModelProperty(value="是否禁用", required = false)
    private Boolean disabled;

    @ApiModelProperty(value="菜单地址", required = false)
    private String href;

    @ApiModelProperty(value="子菜单", required = false)
    private List<SysMenuTreeModel> children = new ArrayList<>();
}
