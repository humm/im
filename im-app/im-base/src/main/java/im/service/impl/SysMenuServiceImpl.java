package im.service.impl;

import im.dao.SysMenuDao;
import im.model.SysMenuModel;
import im.model.SysMenuQueryModel;
import im.model.SysMenuTreeModel;
import im.model.SysMenuTreeQueryModel;
import im.model.base.ResultData;
import im.model.base.SessionBean;
import im.service.SysMenuService;
import im.util.SysLogUtils;
import im.util.SysSessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static im.consts.BaseConst.ADMIN_CODE;
import static im.consts.BaseConst.WELL;
import static im.consts.BaseCueConst.SELECT_SUCCESS;
import static im.consts.BaseCueConst.*;

/**
 * @author hoomoomoo
 * @description 菜单信息服务实现类
 * @package com.hoomoomoo.im.service.impl
 * @date 2019/09/29
 */

@Service
@Transactional
public class SysMenuServiceImpl implements SysMenuService {

    private static final Logger logger = LoggerFactory.getLogger(SysMenuServiceImpl.class);

    @Autowired
    private SysMenuDao sysMenuDao;

    /**
     * 查询菜单树
     *
     * @param roleId
     * @param disabled
     * @return
     */
    @Override
    public List<SysMenuTreeModel> selectMenuTree(String disabled, String roleId) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_MENU, LOG_OPERATE_TYPE_SELECT);
        SysMenuTreeQueryModel sysMenuTreeQueryModel = new SysMenuTreeQueryModel();
        sysMenuTreeQueryModel.setIsParentId(true);
        sysMenuTreeQueryModel.setDisabled(disabled);
        sysMenuTreeQueryModel.setRoleId(roleId);
        SysLogUtils.parameter(logger, sysMenuTreeQueryModel);
        // 获取父级菜单
        List<SysMenuTreeModel> sysMenuTreeModelList = sysMenuDao.selectMenuTree(sysMenuTreeQueryModel);
        // 获取子级菜单
        for(SysMenuTreeModel sysMenuTreeModel : sysMenuTreeModelList){
            getChildMenuTree(sysMenuTreeModel, sysMenuTreeQueryModel);
        }
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_MENU, LOG_OPERATE_TYPE_SELECT);
        return sysMenuTreeModelList;
    }

    /**
     * 查询数据权限
     *
     * @param roleId
     * @return
     */
    @Override
    public String selectDataAuthority(String roleId) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_DATA_AUTHORITY, LOG_OPERATE_TYPE_SELECT);
        SysMenuTreeQueryModel sysMenuTreeQueryModel = new SysMenuTreeQueryModel();
        sysMenuTreeQueryModel.setRoleId(roleId);
        SysLogUtils.parameter(logger, sysMenuTreeQueryModel);
        String result = sysMenuDao.selectDataAuthority(sysMenuTreeQueryModel);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_DATA_AUTHORITY, LOG_OPERATE_TYPE_SELECT);
        return result;
    }

    /**
     * 查询数据权限
     *
     * @param userId
     * @return
     */
    @Override
    public Boolean selectDataAuthorityByUserId(String userId) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_DATA_AUTHORITY, LOG_OPERATE_TYPE_SELECT);
        SysMenuTreeQueryModel sysMenuTreeQueryModel = new SysMenuTreeQueryModel();
        sysMenuTreeQueryModel.setUserId(userId);
        SysLogUtils.parameter(logger, sysMenuTreeQueryModel);
        Boolean result = sysMenuDao.selectDataAuthorityByUserId(sysMenuTreeQueryModel);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_DATA_AUTHORITY, LOG_OPERATE_TYPE_SELECT);
        return result;
    }

    /**
     * 查询子菜单树
     *
     * @param sysMenuTreeModel
     * @param sysMenuTreeQueryModel
     */
    private void getChildMenuTree(SysMenuTreeModel sysMenuTreeModel, SysMenuTreeQueryModel sysMenuTreeQueryModel){
        sysMenuTreeQueryModel.setIsParentId(false);
        sysMenuTreeQueryModel.setParentId(sysMenuTreeModel.getId());
        List<SysMenuTreeModel> sysMenuTreeModelList = sysMenuDao.selectMenuTree(sysMenuTreeQueryModel);
        sysMenuTreeModel.setChildren(sysMenuTreeModelList);
        for(SysMenuTreeModel subSysMenuTreeModel: sysMenuTreeModelList){
            if(WELL.equals(subSysMenuTreeModel.getHref())){
                getChildMenuTree(subSysMenuTreeModel, sysMenuTreeQueryModel);
            }
        }
    }

    /**
     * 查询菜单信息
     *
     * @return
     */
    @Override
    public ResultData selectMenu() {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_MENU, LOG_OPERATE_TYPE_SELECT);
        SysMenuQueryModel sysMenuQueryModel = new SysMenuQueryModel();
        sysMenuQueryModel.setIsParentId(true);
        SessionBean sessionBean = SysSessionUtils.getSession();
        if(sessionBean != null){
            sysMenuQueryModel.setUserId(sessionBean.getUserId());
            sysMenuQueryModel.setUserCode(sessionBean.getUserCode());
        }
        SysLogUtils.parameter(logger, sysMenuQueryModel);
        // 获取父级菜单
        List<SysMenuModel> sysMenuModelList = null;
        if(ADMIN_CODE.equals(sysMenuQueryModel.getUserCode())){
            sysMenuModelList= sysMenuDao.selectAllMenu(sysMenuQueryModel);
        }else{
            sysMenuModelList= sysMenuDao.selectMenu(sysMenuQueryModel);
        }
        // 获取子级菜单
        for(SysMenuModel sysMenuModel : sysMenuModelList){
            getChildMenu(sysMenuModel, sysMenuQueryModel);
        }
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_MENU, LOG_OPERATE_TYPE_SELECT);
        return new ResultData(true, SELECT_SUCCESS, sysMenuModelList);
    }

    /**
     * 更新菜单信息
     *
     * @param sysMenuModel
     */
    @Override
    public void updateMenu(SysMenuModel sysMenuModel) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_MENU, LOG_OPERATE_TYPE_UPDATE);
        SysLogUtils.parameter(logger, sysMenuModel);
        sysMenuDao.updateMenu(sysMenuModel);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_MENU, LOG_OPERATE_TYPE_UPDATE);
    }

    @Override
    public Boolean selectMenuAuthority(SysMenuQueryModel sysMenuQueryModel) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_MENU, LOG_OPERATE_TYPE_SELECT);
        SysLogUtils.parameter(logger, sysMenuQueryModel);
        boolean exist = sysMenuDao.selectMenuAuthority(sysMenuQueryModel);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_MENU, LOG_OPERATE_TYPE_SELECT);
        return exist;
    }

    /**
     * 查询子菜单信息
     *
     * @param sysMenuModel
     * @param sysMenuQueryModel
     */
    private void getChildMenu(SysMenuModel sysMenuModel, SysMenuQueryModel sysMenuQueryModel){
        sysMenuQueryModel.setIsParentId(false);
        sysMenuQueryModel.setParentId(sysMenuModel.getMenuId());
        List<SysMenuModel> sysMenuModelList = null;
        if(ADMIN_CODE.equals(sysMenuQueryModel.getUserCode())){
            sysMenuModelList= sysMenuDao.selectAllMenu(sysMenuQueryModel);
        }else{
            sysMenuModelList= sysMenuDao.selectMenu(sysMenuQueryModel);
        }
        sysMenuModel.setChildren(sysMenuModelList);
        for(SysMenuModel subSysMenuModel: sysMenuModelList){
            if(WELL.equals(subSysMenuModel.getMenuUrl())){
                getChildMenu(subSysMenuModel, sysMenuQueryModel);
            }
        }
    }

}
