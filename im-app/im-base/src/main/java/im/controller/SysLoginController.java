package im.controller;

import im.config.bean.SystemConfigBean;
import im.model.SysUserModel;
import im.model.base.ResultData;
import im.service.SysLoginService;
import im.service.SysParameterService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static im.consts.ParameterConst.APP_HELP;
import static im.consts.ParameterConst.VERSION;

/**
 * @author hoomoomoo
 * @description 用户登录控制类
 * @package im.controller
 * @date 2019/10/14
 */

@Controller
@RequestMapping("/")
public class SysLoginController {

    @Autowired
    private SysLoginService sysLoginService;

    @Autowired
    private SysParameterService sysParameterService;

    @Autowired
    private SystemConfigBean systemConfigBean;


    /**
     * 跳转登入页面
     *
     * @return
     */
    @ApiOperation("跳转登入页面")
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String login(ModelMap modelMap) {
        modelMap.addAttribute(VERSION, sysParameterService.getParameterString(VERSION));
        modelMap.addAttribute(APP_HELP, systemConfigBean.getAppHelp());
        return "page/home/login";
    }

    /**
     * 登入
     *
     * @param request
     * @param sysUserModel
     * @return
     */
    @ApiOperation("用户登入")
    @RequestMapping(value = "user/login", method = RequestMethod.POST)
    @ResponseBody
    public ResultData login(HttpServletRequest request, HttpServletResponse response, SysUserModel sysUserModel) {
        return sysLoginService.login(request, response, sysUserModel);
    }

    /**
     * 登出
     *
     * @param request
     * @param sessionStatus
     * @return
     */
    @ApiOperation("用户登出")
    @RequestMapping(value = "user/logout", method = RequestMethod.POST)
    @ResponseBody
    public ResultData logout(HttpServletRequest request, SessionStatus sessionStatus) {
        return sysLoginService.logout(request, sessionStatus);
    }
}
