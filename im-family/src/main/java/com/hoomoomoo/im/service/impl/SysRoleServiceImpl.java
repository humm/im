package com.hoomoomoo.im.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hoomoomoo.im.config.WebSocketServerConfig;
import com.hoomoomoo.im.dao.SysRoleDao;
import com.hoomoomoo.im.model.*;
import com.hoomoomoo.im.model.PageModel;
import com.hoomoomoo.im.model.ResultData;
import com.hoomoomoo.im.model.common.ViewData;
import com.hoomoomoo.im.service.SysMenuService;
import com.hoomoomoo.im.service.SysRoleService;
import com.hoomoomoo.im.service.SysSystemService;
import com.hoomoomoo.im.util.SysLogUtils;
import com.hoomoomoo.im.util.SysCommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.hoomoomoo.im.consts.BusinessConst.*;
import static com.hoomoomoo.im.consts.BusinessCueConst.*;

/**
 * @author hoomoomoo
 * @description 角色信息服务实现类
 * @package com.hoomoomoo.im.service.impl
 * @date 2019/09/27
 */

@Service
@Transactional
public class SysRoleServiceImpl implements SysRoleService {

    private static final Logger logger = LoggerFactory.getLogger(SysRoleServiceImpl.class);

    @Autowired
    private SysRoleDao sysRoleDao;

    @Autowired
    private SysSystemService sysSystemService;

    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 查询角色信息
     *
     * @param sysRoleQueryModel
     * @return
     */
    @Override
    public List<SysRoleModel> selectSysRole(SysRoleQueryModel sysRoleQueryModel) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_ROLE, LOG_OPERATE_TYPE_SELECT);
        SysCommonUtils.setSessionInfo(sysRoleQueryModel);
        SysLogUtils.parameter(logger, sysRoleQueryModel);
        List<SysRoleModel> sysRoleModelList = sysRoleDao.selectSysRole(sysRoleQueryModel);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_ROLE, LOG_OPERATE_TYPE_SELECT);
        return sysRoleModelList;
    }

    /**
     * 查询页面初始化相关数据
     *
     * @param disabled
     * @param roleId
     * @return
     */
    @Override
    public ResultData selectInitData(String disabled, String roleId) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_ROLE, LOG_OPERATE_TYPE_SELECT_INIT);
        ViewData viewData = new ViewData();
        // 设置查询条件
        viewData.setViewType(BUSINESS_TYPE_ROLE);
        sysSystemService.setCondition(viewData);
        // 设置菜单信息
        viewData.setMenuList(sysMenuService.selectMenuTree(disabled, roleId));
        // 设置数据权限信息
        viewData.setDataAuthority(sysMenuService.selectDataAuthority(roleId));
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_ROLE, LOG_OPERATE_TYPE_SELECT_INIT);
        return new ResultData(true, SELECT_SUCCESS, viewData);
    }

    /**
     * 分页查询角色信息
     *
     * @param sysRoleQueryModel
     * @return
     */
    @Override
    public PageModel<SysRoleModel> selectPage(SysRoleQueryModel sysRoleQueryModel) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_ROLE, LOG_OPERATE_TYPE_SELECT_PAGE);
        SysLogUtils.parameter(logger, sysRoleQueryModel);
        PageHelper.startPage(sysRoleQueryModel.getPage(), sysRoleQueryModel.getLimit());
        List<SysRoleModel> sysRoleModelList = sysRoleDao.selectPage(sysRoleQueryModel);
        // 创建PageInfo对象前 不能处理数据否则getTotal数据不正确
        PageInfo<SysRoleModel> pageInfo = new PageInfo<>(sysRoleModelList);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_ROLE, LOG_OPERATE_TYPE_SELECT_PAGE);
        return new PageModel(pageInfo.getTotal(), sysSystemService.transferData(pageInfo.getList(), SysRoleModel.class));

    }

    /**
     * 删除角色信息
     *
     * @param roleIds
     * @return
     */
    @Override
    public ResultData delete(String roleIds) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_ROLE, LOG_OPERATE_TYPE_DELETE);
        List<SysRoleModel> list = new ArrayList<>();
        if (StringUtils.isNotBlank(roleIds)) {
            String[] roleId = roleIds.split(COMMA);
            SysRoleMenuModel sysRoleMenuModel = new SysRoleMenuModel();
            SysUserModel sysUserModel = new SysUserModel();
            sysRoleDao.deleteRoleMenu(sysRoleMenuModel);
            for (String ele : roleId) {
                SysRoleModel sysRoleModel = new SysRoleModel();
                sysRoleModel.setRoleId(ele);
                list.add(sysRoleModel);
                sysRoleMenuModel.setRoleId(sysRoleModel.getRoleId());
                sysUserModel.setRoleId(sysRoleModel.getRoleId());
                sysRoleDao.deleteRoleMenu(sysRoleMenuModel);
                sysRoleDao.deleteRoleUser(sysUserModel);
            }
            sysRoleDao.delete(list);
        }
        SysLogUtils.parameter(logger, list);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_ROLE, LOG_OPERATE_TYPE_DELETE);
        return new ResultData(true, DELETE_SUCCESS, null);
    }

    /**
     * 查询角色信息
     *
     * @param roleId
     * @param isTranslate
     * @return
     */
    @Override
    public ResultData selectOne(String roleId, Boolean isTranslate) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_ROLE, LOG_OPERATE_TYPE_SELECT);
        SysRoleQueryModel sysRoleQueryModel = new SysRoleQueryModel();
        sysRoleQueryModel.setRoleId(roleId);
        SysLogUtils.parameter(logger, sysRoleQueryModel);
        SysRoleModel sysRoleModel = sysRoleDao.selectOne(sysRoleQueryModel);
        if (isTranslate) {
            sysSystemService.transferData(sysRoleModel, SysRoleModel.class);
        }
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_ROLE, LOG_OPERATE_TYPE_SELECT);
        return new ResultData(true, SELECT_SUCCESS, sysRoleModel);
    }

    /**
     * 保存角色信息
     *
     * @param sysRoleModel
     * @return
     */
    @Override
    public ResultData save(SysRoleModel sysRoleModel) {
        String operateType = sysRoleModel.getRoleId() == null ? LOG_OPERATE_TYPE_ADD : LOG_OPERATE_TYPE_UPDATE;
        String tipMsg = sysRoleModel.getRoleId() == null ? ADD_SUCCESS : UPDATE_SUCCESS;
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_ROLE, operateType);
        SysCommonUtils.setCreateUserInfo(sysRoleModel);
        if (sysRoleModel.getRoleId() == null) {
            // 新增
            String roleId = sysSystemService.getBusinessSerialNo(BUSINESS_TYPE_ROLE);
            sysRoleModel.setRoleId(roleId);
        } else {
            // 修改
        }
        // 处理角色菜单信息
        SysRoleMenuModel sysRoleMenuModel = new SysRoleMenuModel();
        sysRoleMenuModel.setRoleId(sysRoleModel.getRoleId());
        sysRoleDao.deleteRoleMenu(sysRoleMenuModel);
        String menuIds = sysRoleModel.getMenuId();
        if(StringUtils.isNotBlank(menuIds) && !STR_KEY_MEND_ID_SKIP.equals(menuIds)){
            for(String menuId : menuIds.split(COMMA)){
                sysRoleMenuModel.setMenuId(menuId);
                sysRoleMenuModel.setRoleMenuId(sysSystemService.getBusinessSerialNo(BUSINESS_TYPE_ROLE_MENU));
                sysRoleDao.saveRoleMenu(sysRoleMenuModel);
            }
        }
        // 处理数据权限信息
        if(SWITCH_ON.equals(sysRoleModel.getDataAuthority())){
            sysRoleMenuModel.setMenuId(DATA_AUTHORITY_ID);
            sysRoleMenuModel.setRoleMenuId(sysSystemService.getBusinessSerialNo(BUSINESS_TYPE_ROLE_MENU));
            sysRoleDao.saveRoleMenu(sysRoleMenuModel);
        }
        SysLogUtils.parameter(logger, sysRoleModel);
        sysRoleDao.save(sysRoleModel);
        // 加载字典项
        sysSystemService.loadSysDictionaryCondition();
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_ROLE, operateType);
        WebSocketServerConfig.sendMessageInfo(WEBSOCKET_TOPIC_NAME_CONSOLE, LOG_BUSINESS_TYPE_ROLE);
        return new ResultData(true, tipMsg, null);
    }

    /**
     * 校验roleCode是否存在
     *
     * @param sysRoleQueryModel
     * @return
     */
    @Override
    public ResultData checkRoleCode(SysRoleQueryModel sysRoleQueryModel) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_ROLE, LOG_OPERATE_TYPE_CHECK);
        Boolean isExist = sysRoleDao.checkRoleCode(sysRoleQueryModel);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_ROLE, LOG_OPERATE_TYPE_CHECK);
        return new ResultData(true, CHECK_SUCCESS, isExist);
    }

}
