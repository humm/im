package im.service;

import im.model.SysUserModel;
import im.model.base.ResultData;
import im.model.base.SessionBean;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author hoomoomoo
 * @description 用户登录服务类
 * @package im.service
 * @date 2019/10/14
 */

public interface SysLoginService {

    /**
     * 用户登录
     *
     * @param request
     * @param response
     * @param sysUserModel
     * @return
     */
    ResultData login(HttpServletRequest request, HttpServletResponse response, SysUserModel sysUserModel);

    /**
     * 登出
     * @param request
     * @param sessionStatus
     * @return
     */
    ResultData logout(HttpServletRequest request, SessionStatus sessionStatus);

    /**
     * 设置sessionBean信息
     *
     * @param sysUserModel
     * @return
     */
    SessionBean setSessionBeanInfo(SysUserModel sysUserModel);
}
