package com.hoomoomoo.im.service;

import com.hoomoomoo.im.model.SysRoleModel;
import com.hoomoomoo.im.model.SysRoleQueryModel;
import com.hoomoomoo.im.model.base.PageModel;
import com.hoomoomoo.im.model.base.ResultData;

import java.util.List;

/**
 * @author hoomoomoo
 * @description 角色信息服务类
 * @package com.hoomoomoo.im.service
 * @date 2019/09/27
 */

public interface SysRoleService {

    /**
     * 查询角色信息
     *
     * @param sysRoleQueryModel
     * @return
     */
    List<SysRoleModel> selectSysRole(SysRoleQueryModel sysRoleQueryModel);

    /**
     * 查询页面初始化相关数据
     *
     * @param disabled
     * @param roleId
     * @return
     */
    ResultData selectInitData(String disabled, String roleId);

    /**
     * 分页查询角色信息
     *
     * @param sysRoleQueryModel
     * @return
     */
    PageModel<SysRoleModel> selectPage(SysRoleQueryModel sysRoleQueryModel);

    /**
     * 删除角色信息
     *
     * @param roleIds
     * @return
     */
    ResultData delete(String roleIds);

    /**
     * 查询角色信息
     *
     * @param roleId
     * @return
     */
    ResultData selectOne(String roleId, Boolean isTranslate);

    /**
     * 保存角色信息
     *
     * @param sysRoleModel
     * @return
     */
    ResultData save(SysRoleModel sysRoleModel);

    /**
     * 校验roleCode是否存在
     *
     * @param sysRoleQueryModel
     * @return
     */
    ResultData checkRoleCode(SysRoleQueryModel sysRoleQueryModel);
}
