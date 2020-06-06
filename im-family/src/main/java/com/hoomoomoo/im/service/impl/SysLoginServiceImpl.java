package com.hoomoomoo.im.service.impl;

import com.hoomoomoo.im.dao.SysUserDao;
import com.hoomoomoo.im.model.SysLoginLogModel;
import com.hoomoomoo.im.model.SysUserModel;
import com.hoomoomoo.im.model.SysUserQueryModel;
import com.hoomoomoo.im.model.ResultData;
import com.hoomoomoo.im.model.SessionBean;
import com.hoomoomoo.im.service.*;
import com.hoomoomoo.im.util.SysLogUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

import static com.hoomoomoo.im.consts.BusinessConst.*;
import static com.hoomoomoo.im.consts.BusinessCueConst.*;
import static com.hoomoomoo.im.consts.DictionaryConst.D002;
import static com.hoomoomoo.im.consts.ParameterConst.COOKIE_TIMEOUT;

/**
 * @author hoomoomoo
 * @description 用户登录服务实现类
 * @package com.hoomoomoo.im.service.impl
 * @date 2019/10/14
 */

@Service
@Transactional
public class SysLoginServiceImpl implements SysLoginService {

    private static final Logger logger = LoggerFactory.getLogger(SysLoginServiceImpl.class);

    @Autowired
    private SysUserDao SysUserDao;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysSystemService sysSystemService;

    @Autowired
    private SysLoginLogService sysLoginLogService;

    @Autowired
    private SysParameterService sysParameterService;

    /**
     * 用户登录
     *
     * @param request
     * @param response
     * @param sysUserModel
     * @return
     */
    @Override
    public ResultData login(HttpServletRequest request, HttpServletResponse response, SysUserModel sysUserModel) {
        ResultData resultData = null;
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_SELECT);
        SysLogUtils.parameter(logger, sysUserModel);
        SysUserQueryModel sysUserQueryModel = new SysUserQueryModel();
        SysLoginLogModel sysLoginLogModel= new SysLoginLogModel();
        sysLoginLogModel.setLogId(sysSystemService.getBusinessSerialNo(BUSINESS_TYPE_LOGIN_LOG));
        sysLoginLogModel.setLoginDate(new Date());
        sysUserQueryModel.setUserCode(sysUserModel.getUserCode());
        List<SysUserModel> sysUserModelList = SysUserDao.selectSysUser(sysUserQueryModel);
        if (CollectionUtils.isEmpty(sysUserModelList)) {
            // 用户不存在
            resultData = new ResultData(false, USER_LOGON_ACCOUNT_NOT_EXIST, USER_LOGON_ACCOUNT_NOT_EXIST);
            sysLoginLogModel.setLoginStatus(new StringBuffer(D002).append(MINUS).append(STR_2).toString());
            sysLoginLogModel.setLoginMessage(USER_LOGON_ACCOUNT_NOT_EXIST);
        } else {
            SysUserModel sysUser = sysUserModelList.get(0);
            sysLoginLogModel.setUserId(sysUser.getUserId());
            boolean flag = true;
            // 用户已冻结
            if (USER_STATUS_FREEZE.equals(sysUser.getUserStatus())) {
                flag = false;
                resultData = new ResultData(false, USER_LOGON_ACCOUNT_FREEZE, USER_LOGON_ACCOUNT_FREEZE);
                sysLoginLogModel.setLoginStatus(new StringBuffer(D002).append(MINUS).append(STR_2).toString());
                sysLoginLogModel.setLoginMessage(USER_LOGON_ACCOUNT_FREEZE);
            }
            String inputPassword = sysUserModel.getUserPassword();
            String savePassword = new StringBuffer(sysUser.getUserPassword()).reverse().toString();
            if (flag && inputPassword.equals(savePassword)) {
                // 登录成功
                // 设置session信息
                HttpSession session = request.getSession();
                SessionBean sessionBean = setSessionBeanInfo(sysUser);
                session.setAttribute(SESSION_BEAN, sessionBean);
                //  设置cookie信息
                if (SWITCH_ON.equals(sysUserModel.getRememberPassword())) {
                    response.addCookie(setCookiInfo(COOKIE_USER_CODE, sysUserModel.getUserCode()));
                    response.addCookie(setCookiInfo(COOKIE_USER_PASSWORD, sysUserModel.getUserPassword()));
                    response.addCookie(setCookiInfo(COOKIE_REMEMBER_PASSWORD, sysUserModel.getRememberPassword()));
                } else {
                    clearCookieInfo(request, response);
                }
                resultData = new ResultData(true, USER_LOGON_SUCCESS, sessionBean);
                sysLoginLogModel.setLoginStatus(new StringBuffer(D002).append(MINUS).append(STR_1).toString());
                sysLoginLogModel.setLoginMessage(USER_LOGON_SUCCESS);
            } else if (flag) {
                // 密码错误
                resultData = new ResultData(false, USER_LOGON_PASSWORD_ERROR, USER_LOGON_PASSWORD_ERROR);
                sysLoginLogModel.setLoginStatus(new StringBuffer(D002).append(MINUS).append(STR_2).toString());
                sysLoginLogModel.setLoginMessage(USER_LOGON_PASSWORD_ERROR);
            }
        }
        sysLoginLogService.save(sysLoginLogModel);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_SELECT);
        return resultData;
    }

    /**
     * 登出
     *
     * @param request
     * @param sessionStatus
     * @return
     */
    @Override
    public ResultData logout(HttpServletRequest request, SessionStatus sessionStatus) {
        ResultData resultData = new ResultData();
        SessionBean sessionBean = (SessionBean) request.getSession().getAttribute(SESSION_BEAN);
        if(sessionBean != null){
            // 更新登录日志
            SysLoginLogModel sysLoginLogModel= new SysLoginLogModel();
            sysLoginLogModel.setUserId(sessionBean.getUserId());
            sysLoginLogModel.setLogoutDate(new Date());
            sysLoginLogService.update(sysLoginLogModel);

            // 清除session
            sessionStatus.setComplete();
            request.getSession().removeAttribute(SESSION_BEAN);
            resultData = new ResultData(true, USER_LOGOUT_SUCCESS, USER_LOGOUT_SUCCESS);
        }
        return resultData;
    }

    /**
     * 设置sessionBean信息
     *
     * @param sysUserModel
     * @return
     */
    @Override
    public SessionBean setSessionBeanInfo(SysUserModel sysUserModel) {
        SessionBean sessionBean = new SessionBean();
        sessionBean.setUserId(sysUserModel.getUserId());
        sessionBean.setUserCode(sysUserModel.getUserCode());
        sessionBean.setUserName(sysUserModel.getUserName());
        sessionBean.setUserStatus(sysUserModel.getUserStatus());
        if (ADMIN_CODE.equals(sysUserModel.getUserCode())) {
            sessionBean.setIsAdminData(true);
        } else {
            sessionBean.setIsAdminData(sysMenuService.selectDataAuthorityByUserId(sysUserModel.getUserId()));
        }
        return sessionBean;
    }

    /**
     * 设置cookie信息
     *
     * @param key
     * @param value
     * @return
     */
    private Cookie setCookiInfo(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath(SLASH);
        cookie.setMaxAge(sysParameterService.getParameterInteger(COOKIE_TIMEOUT) * 24 * 60 * 60);
        return cookie;
    }

    /**
     * 删除cookie信息
     *
     * @param request
     * @param response
     * @return
     */
    private void clearCookieInfo(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies=request.getCookies();
        for(Cookie cookie: cookies){
            cookie.setValue(null);
            cookie.setPath(SLASH);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }
}
