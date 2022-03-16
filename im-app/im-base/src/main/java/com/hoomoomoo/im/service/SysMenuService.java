package com.hoomoomoo.im.service;

import com.hoomoomoo.im.model.SysMenuModel;
import com.hoomoomoo.im.model.SysMenuQueryModel;
import com.hoomoomoo.im.model.SysMenuTreeModel;
import com.hoomoomoo.im.model.base.ResultData;

import java.util.List;

/**
 * @author hoomoomoo
 * @description 菜单信息服务类
 * @package com.hoomoomoo.im.service
 * @date 2019/09/29
 */

public interface SysMenuService {

    /**
     * 查询菜单树
     *
     * @param roleId
     * @param disabled
     * @return
     */
    List<SysMenuTreeModel> selectMenuTree(String disabled, String roleId);

    /**
     * 查询数据权限
     *
     * @param roleId
     * @return
     */
    String selectDataAuthority(String roleId);

    /**
     * 查询数据权限
     *
     * @param userId
     * @return
     */
    Boolean selectDataAuthorityByUserId(String userId);

    /**
     * 查询菜单信息
     *
     * @return
     */
    ResultData selectMenu();

    /**
     * 更新菜单信息
     *
     * @param sysMenuModel
     */
    void updateMenu(SysMenuModel sysMenuModel);

    /**
     * 查询菜单权限
     *
     * @param sysMenuQueryModel
     * @return
     */
    Boolean selectMenuAuthority(SysMenuQueryModel sysMenuQueryModel);
}
