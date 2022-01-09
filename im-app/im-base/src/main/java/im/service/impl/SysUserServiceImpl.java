package im.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import im.dao.SysDictionaryDao;
import im.dao.SysRoleDao;
import im.dao.SysUserDao;
import im.model.*;
import im.model.base.PageModel;
import im.model.base.ResultData;
import im.model.base.SessionBean;
import im.model.base.ViewData;
import im.service.SysParameterService;
import im.service.SysUserService;
import im.service.SysSystemService;
import im.util.SysLogUtils;
import im.util.SysSessionUtils;
import im.util.SysCommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static im.consts.BaseConst.*;
import static im.consts.BaseCueConst.*;
import static im.consts.DictionaryConst.D009;
import static im.consts.ParameterConst.USER_DEFAULT_PASSWORD;

/**
 * @author hoomoomoo
 * @description 用户信息服务实现类
 * @package com.hoomoomoo.im.service.impl
 * @date 2019/08/11
 */

@Service
@Transactional
public class SysUserServiceImpl implements SysUserService {

    private static final Logger logger = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private SysSystemService sysSystemService;

    @Autowired
    private SysDictionaryDao sysDictionaryDao;

    @Autowired
    private SysRoleDao sysRoleDao;

    @Autowired
    private SysParameterService sysParameterService;

    /**
     * 查询用户信息
     *
     * @param sysUserQueryModel
     * @return
     */
    @Override
    public List<SysUserModel> selectSysUser(SysUserQueryModel sysUserQueryModel) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_SELECT);
        SysCommonUtils.setSessionInfo(sysUserQueryModel);
        SysLogUtils.parameter(logger, sysUserQueryModel);
        List<SysUserModel> sysUserList = sysUserDao.selectSysUser(sysUserQueryModel);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_SELECT);
        return sysUserList;
    }

    /**
     * 查询页面初始化相关数据
     *
     * @return
     */
    @Override
    public ResultData selectInitData() {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_SELECT_INIT);
        ViewData viewData = new ViewData();
        // 设置查询条件
        viewData.setViewType(BUSINESS_TYPE_USER);
        sysSystemService.setCondition(viewData);
        // 获取角色信息
        List<SysRoleModel> roleModelList = sysRoleDao.selectSysRole(null);
        viewData.setRoleList(roleModelList);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_SELECT_INIT);
        return new ResultData(true, SELECT_SUCCESS, viewData);
    }

    /**
     * 分页查询用户信息
     *
     * @param sysUserQueryModel
     * @return
     */
    @Override
    public PageModel<SysUserModel> selectPage(SysUserQueryModel sysUserQueryModel) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_SELECT_PAGE);
        SysLogUtils.parameter(logger, sysUserQueryModel);
        PageHelper.startPage(sysUserQueryModel.getPage(), sysUserQueryModel.getLimit());
        List<SysUserModel> sysUserModelList = sysUserDao.selectPage(sysUserQueryModel);
        // 创建PageInfo对象前 不能处理数据否则getTotal数据不正确
        PageInfo<SysUserModel> pageInfo = new PageInfo<>(sysUserModelList);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_SELECT_PAGE);
        return new PageModel(pageInfo.getTotal(), sysSystemService.transferData(pageInfo.getList(), SysUserModel.class));
    }

    /**
     * 删除用户信息
     *
     * @param userIds
     * @return
     */
    @Override
    public ResultData delete(String userIds) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_DELETE);
        List<SysUserModel> list = new ArrayList<>();
        List<SysConfigModel> configModelList = new ArrayList<>();
        if (StringUtils.isNotBlank(userIds)) {
            String[] userId = userIds.split(COMMA);
            for (String ele : userId) {
                SysUserModel sysUserModel = new SysUserModel();
                sysUserModel.setUserId(ele);
                list.add(sysUserModel);
                SysConfigModel sysConfigModel = new SysConfigModel();
                sysConfigModel.setUserId(ele);
                configModelList.add(sysConfigModel);
                // 删除字典项
                SysDictionaryModel sysDictionaryModel = new SysDictionaryModel();
                sysDictionaryModel.setDictionaryCode(D009);
                sysDictionaryModel.setDictionaryItem(ele);
                sysDictionaryDao.delete(sysDictionaryModel);
            }
            sysUserDao.delete(list);
            sysSystemService.delete(configModelList);
        }
        // 加载字典项
        sysSystemService.loadSysDictionaryCondition();
        SysLogUtils.parameter(logger, list);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_DELETE);
        return new ResultData(true, DELETE_SUCCESS, null);
    }

    /**
     * 查询用户信息
     *
     * @param userId
     * @param isTranslate
     * @return
     */
    @Override
    public ResultData selectOne(String userId, Boolean isTranslate) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_SELECT);
        SysUserQueryModel sysUserQueryModel = new SysUserQueryModel();
        sysUserQueryModel.setUserId(userId);
        SysLogUtils.parameter(logger, sysUserQueryModel);
        SysUserModel sysUserModel = sysUserDao.selectOne(sysUserQueryModel);
        if (isTranslate) {
            sysSystemService.transferData(sysUserModel, SysUserModel.class);
        }
        // 查询用户角色信息
        List<SysUserRoleModel> sysUserRoleModelList = sysUserDao.selectUserRole(sysUserQueryModel);
        String[] roles = new String[sysUserRoleModelList.size()];
        for (int i = 0; i < sysUserRoleModelList.size(); i++) {
            roles[i] = sysUserRoleModelList.get(i).getRoleId();
        }
        sysUserModel.setRoleId(StringUtils.join(roles, COMMA));
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_SELECT);
        return new ResultData(true, SELECT_SUCCESS, sysUserModel);
    }

    /**
     * 保存用户信息
     *
     * @param sysUserModel
     * @return
     */
    @Override
    public ResultData save(SysUserModel sysUserModel) {
        String operateType = sysUserModel.getUserId() == null ? LOG_OPERATE_TYPE_ADD : LOG_OPERATE_TYPE_UPDATE;
        String tipMsg = sysUserModel.getUserId() == null ? ADD_SUCCESS : UPDATE_SUCCESS;
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_USER, operateType);
        SysCommonUtils.setCreateUserInfo(sysUserModel);
        SysDictionaryModel sysDictionaryModel = new SysDictionaryModel();
        sysDictionaryModel.setDictionaryCode(D009);
        sysDictionaryModel.setDictionaryCaption(sysUserModel.getUserName());
        if (sysUserModel.getUserId() == null) {
            // 新增
            String userId = sysSystemService.getBusinessSerialNo(BUSINESS_TYPE_USER);
            sysUserModel.setUserId(userId);

            sysUserModel.setUserPassword(getPassword());
            // 新增字典项
            sysDictionaryModel.setDictionaryItem(userId);
            sysDictionaryModel.setUserId(userId);
            sysDictionaryDao.save(sysDictionaryModel);
        } else {
            // 修改字典项
            sysDictionaryModel.setUserId(sysUserModel.getUserId());
            sysDictionaryModel.setDictionaryItem(sysUserModel.getUserId());
            sysDictionaryDao.update(sysDictionaryModel);
        }
        SysLogUtils.parameter(logger, sysUserModel);
        sysUserDao.save(sysUserModel);
        // 加载字典项
        sysSystemService.loadSysDictionaryCondition();
        // 角色信息处理
        sysUserDao.deleteUserRole(sysUserModel);
        if (StringUtils.isNotBlank(sysUserModel.getRoleId())) {
            String[] roleId = sysUserModel.getRoleId().split(COMMA);
            for (String ele : roleId) {
                SysUserRoleModel sysUserRoleModel = new SysUserRoleModel();
                String userRoleId = sysSystemService.getBusinessSerialNo(BUSINESS_TYPE_USER_ROLE);
                sysUserRoleModel.setUserRoleId(userRoleId);
                sysUserRoleModel.setUserId(sysUserModel.getUserId());
                sysUserRoleModel.setRoleId(ele);
                sysUserDao.saveUserRole(sysUserRoleModel);
            }
        }
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_USER, operateType);
        return new ResultData(true, tipMsg, null);
    }

    /**
     * 校验userCode是否存在
     *
     * @param sysUserQueryModel
     * @return
     */
    @Override
    public ResultData checkUserCode(SysUserQueryModel sysUserQueryModel) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_CHECK);
        Boolean isExist = sysUserDao.checkUserCode(sysUserQueryModel);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_CHECK);
        return new ResultData(true, CHECK_SUCCESS, isExist);
    }

    /**
     * 重置用户密码
     *
     * @param userIds
     * @return
     */
    @Override
    public ResultData reset(String userIds) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_RESET_PASSWORD);
        List<SysUserModel> list = new ArrayList<>();
        if (StringUtils.isNotBlank(userIds)) {

            String[] userId = userIds.split(COMMA);
            for (String ele : userId) {
                SysUserModel sysUserModel = new SysUserModel();
                sysUserModel.setUserId(ele);
                list.add(sysUserModel);
            }
            sysUserDao.reset(getPassword(), list);
        }
        SysLogUtils.parameter(logger, list);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_RESET_PASSWORD);
        return new ResultData(true, RESET_PASSWORD_SUCCESS, null);
    }

    /**
     * 修改用户密码
     *
     * @param password
     * @return
     */
    @Override
    public ResultData changPassword(String password) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_UPDATE_PASSWORD);
        SessionBean sessionBean = SysSessionUtils.getSession();
        if(sessionBean != null){
            SysUserModel sysUserModel = new SysUserModel();
            sysUserModel.setUserId(sessionBean.getUserId());
            sysUserModel.setUserPassword(new StringBuffer(password).reverse().toString());
            sysUserDao.changPassword(sysUserModel);
            SysLogUtils.parameter(logger, sysUserModel);
        }
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_UPDATE_PASSWORD);
        return new ResultData(true, UPDATE_PASSWORD_SUCCESS, null);
    }

    /**
     * 获取用户密码
     *
     * @return
     */
    private String getPassword() {
        String password = sysParameterService.getParameterString(USER_DEFAULT_PASSWORD);
        return new StringBuffer(Base64.encodeBase64String(password.getBytes())).reverse().toString();
    }

}
