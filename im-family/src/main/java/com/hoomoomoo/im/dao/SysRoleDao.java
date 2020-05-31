package com.hoomoomoo.im.dao;

import com.hoomoomoo.im.model.SysRoleMenuModel;
import com.hoomoomoo.im.model.SysRoleModel;
import com.hoomoomoo.im.model.SysRoleQueryModel;
import com.hoomoomoo.im.model.SysUserModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hoomoomoo
 * @description 角色信息dao
 * @package com.hoomoomoo.im.dao
 * @date 2019/09/27
 */

@Mapper
public interface SysRoleDao {

    /**
     * 查询角色信息
     *
     * @param sysRoleQueryModel
     * @return
     */
    List<SysRoleModel> selectSysRole(SysRoleQueryModel sysRoleQueryModel);

    /**
     * 分页查询角色信息
     *
     * @param sysRoleQueryModel
     * @return
     */
    List<SysRoleModel> selectPage(SysRoleQueryModel sysRoleQueryModel);

    /**
     * 删除角色信息
     *
     * @param sysRoleModelList
     * @return
     */
    void delete(List<SysRoleModel> sysRoleModelList);

    /**
     * 查询角色信息
     *
     * @param sysRoleQueryModel
     * @return
     */
    SysRoleModel selectOne(SysRoleQueryModel sysRoleQueryModel);

    /**
     * 保存角色信息
     *
     * @param sysRoleModel
     */
    void save(SysRoleModel sysRoleModel);


    /**
     * 校验roleCode是否存在
     *
     * @param sysRoleQueryModel
     * @return
     */
    Boolean checkRoleCode(SysRoleQueryModel sysRoleQueryModel);

    /**
     * 删除角色菜单信息
     *
     * @param sysRoleMenuModel
     */
    void deleteRoleMenu(SysRoleMenuModel sysRoleMenuModel);

    /**
     * 保存角色菜单信息
     *
     * @param sysRoleMenuModel
     */
    void saveRoleMenu(SysRoleMenuModel sysRoleMenuModel);

    /**
     * 删除角色用户信息
     *
     * @param sysUserModel
     */
    void deleteRoleUser(SysUserModel sysUserModel);
}
