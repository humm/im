package im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hoomoomoo
 * @description 菜单新实体类
 * @package com.hoomoomoo.im.model
 * @date 2019/09/29
 */

@Data
public class SysMenuModel {

    @ApiModelProperty(value="菜单ID", required = false)
    private String menuId;

    @ApiModelProperty(value="菜单名称", required = false)
    private String menuTitle;

    @ApiModelProperty(value="菜单图标", required = false)
    private String menuIcon;

    @ApiModelProperty(value="菜单地址", required = false)
    private String menuUrl;

    @ApiModelProperty(value="父级菜单ID", required = false)
    private String parentId;

    @ApiModelProperty(value="菜单排序", required = false)
    private String menuOrder;

    @ApiModelProperty(value="是否启用", required = false)
    private String isEnable;

    @ApiModelProperty(value="菜单类型", required = false)
    private String menuType;

    @ApiModelProperty(value="子菜单", required = false)
    private List<SysMenuModel> children = new ArrayList<>();
}
