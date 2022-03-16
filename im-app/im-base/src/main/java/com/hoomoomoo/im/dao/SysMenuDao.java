package com.hoomoomoo.im.dao;

import com.hoomoomoo.im.model.SysMenuModel;
import com.hoomoomoo.im.model.SysMenuQueryModel;
import com.hoomoomoo.im.model.SysMenuTreeModel;
import com.hoomoomoo.im.model.SysMenuTreeQueryModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hoomoomoo
 * @description 菜单信息dao
 * @package com.hoomoomoo.im.dao
 * @date 2019/09/29
 */

@Mapper
public interface SysMenuDao {

    /**
     * 查询菜单树
     *
     * @return
     */
    List<SysMenuTreeModel> selectMenuTree(SysMenuTreeQueryModel sysMenuTreeQueryModel);

    /**
     * 查询数据权限
     *
     * @param sysMenuTreeQueryModel
     * @return
     */
    String selectDataAuthority(SysMenuTreeQueryModel sysMenuTreeQueryModel);

    /**
     * 查询数据权限
     *
     * @param sysMenuTreeQueryModel
     * @return
     */
    Boolean selectDataAuthorityByUserId(SysMenuTreeQueryModel sysMenuTreeQueryModel);

    /**
     * 查询菜单信息
     *
     * @param sysMenuQueryModel
     * @return
     */
    List<SysMenuModel> selectMenu(SysMenuQueryModel sysMenuQueryModel);

    /**
     * 查询所有菜单信息
     *
     * @param sysMenuQueryModel
     * @return
     */
    List<SysMenuModel> selectAllMenu(SysMenuQueryModel sysMenuQueryModel);

    /**
     * 查询所有菜单
     *
     * @return
     */
    List<SysMenuModel> selectUrlMenu();

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
