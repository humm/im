package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.config.bean.SystemConfigBean;
import com.hoomoomoo.im.model.base.ResultData;
import com.hoomoomoo.im.service.SysConsoleService;
import com.hoomoomoo.im.util.SysCommonUtils;
import com.hoomoomoo.im.util.SysLogUtils;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import static com.hoomoomoo.im.consts.BaseConst.REQUEST_URL;
import static com.hoomoomoo.im.consts.BaseCueConst.LOG_BUSINESS_TYPE_CONSOLE;
import static com.hoomoomoo.im.consts.BaseCueConst.LOG_OPERATE_TYPE_SELECT;

/**
 * @Author hoomoomoo
 * @Description 首页控制类
 * @package com.hoomoomoo.im.controller
 * @Date 2020/06/20
 */

@Controller
@RequestMapping("/")
public class SysConsoleController {

    private static final Logger logger = LoggerFactory.getLogger(SysSystemController.class);

    @Autowired
    private SystemConfigBean systemConfigBean;

    @Autowired
    private SysConsoleService sysConsoleService;

    /**
     * 跳转首页子页面
     *
     * @return
     */
    @ApiOperation("跳转首页子页面")
    @RequestMapping(value = "console", method = RequestMethod.GET)
    public String console(ModelMap modelMap, HttpServletRequest httpServletRequest) {
        modelMap.addAttribute(REQUEST_URL, SysCommonUtils.getConnectUrl(httpServletRequest, systemConfigBean.getAppName()));
        return "page/console";
    }

    @ApiOperation("查询首页信息")
    @RequestMapping(value = "selectConsoleData", method = RequestMethod.GET)
    @ResponseBody
    public ResultData selectConsoleData(HttpServletRequest httpServletRequest) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_CONSOLE, LOG_OPERATE_TYPE_SELECT);
        ResultData resultData = sysConsoleService.selectConsoleData(httpServletRequest);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_CONSOLE, LOG_OPERATE_TYPE_SELECT);
        return resultData;
    }
}
