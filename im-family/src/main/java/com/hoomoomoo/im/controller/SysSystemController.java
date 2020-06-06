package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.config.bean.SystemConfigBean;
import com.hoomoomoo.im.model.SysModuleModel;
import com.hoomoomoo.im.model.ResultData;
import com.hoomoomoo.im.service.SysConsoleService;
import com.hoomoomoo.im.util.SysBeanUtils;
import com.hoomoomoo.im.util.SysLogUtils;
import com.hoomoomoo.im.util.SysSessionUtils;
import com.hoomoomoo.im.util.SysCommonUtils;
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

import java.util.Iterator;
import java.util.Map;

import static com.hoomoomoo.im.consts.BusinessConst.*;
import static com.hoomoomoo.im.consts.BusinessCueConst.*;
import static com.hoomoomoo.im.consts.BusinessCueConst.LOG_OPERATE_TYPE_SELECT;


/**
 * @author hoomoomoo
 * @description 系统控制类
 * @package com.hoomoomoo.im.controller
 * @date 2019/08/09
 */

@Controller
@RequestMapping("/")
public class SysSystemController {

    private static final Logger logger = LoggerFactory.getLogger(SysSystemController.class);

    @Autowired
    private SystemConfigBean systemConfigBean;

    @Autowired
    private SysConsoleService sysConsoleService;

    /**
     * 跳转首页
     *
     * @return
     */
    @ApiOperation("跳转首页")
    @RequestMapping(value = "index", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        modelMap.addAttribute(USER_NAME, SysSessionUtils.getSession().getUserName());
        return "index";
    }

    /**
     * 跳转首页子页面
     *
     * @return
     */
    @ApiOperation("跳转首页子页面")
    @RequestMapping(value = "console", method = RequestMethod.GET)
    public String console(ModelMap modelMap, HttpServletRequest httpServletRequest) {
        modelMap.addAttribute(REQUEST_URL, SysCommonUtils.getConnectUrl(httpServletRequest, systemConfigBean.getAppName()));
        return "page/home/console";
    }

    @ApiOperation("查询首页信息")
    @RequestMapping(value = "home/selectConsoleData", method = RequestMethod.GET)
    @ResponseBody
    public ResultData selectConsoleData(HttpServletRequest httpServletRequest) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_CONSOLE, LOG_OPERATE_TYPE_SELECT);
        ResultData resultData = sysConsoleService.selectConsoleData(httpServletRequest);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_CONSOLE, LOG_OPERATE_TYPE_SELECT);
        return resultData;
    }

    /**
     * 跳转模块编辑页面
     *
     * @return
     */
    @ApiOperation("跳转模块编辑页面")
    @RequestMapping(value = "home/module", method = RequestMethod.GET)
    public String module(ModelMap modelMap) {
        SysModuleModel sysModuleModel = sysConsoleService.selectConfigModule();
        Map module = SysBeanUtils.beanToMap(sysModuleModel);
        Iterator<String> iterator = module.keySet().iterator();
        while(iterator.hasNext()){
            String key = iterator.next();
            modelMap.addAttribute(key, module.get(key));
        }
        return "page/home/module";
    }

    /**
     * 保存模块信息
     *
     * @param sysModuleModel
     * @return
     */
    @ApiOperation("保存模块信息")
    @RequestMapping(value = "home/module/save", method = RequestMethod.POST)
    @ResponseBody
    public ResultData save(SysModuleModel sysModuleModel) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_CONSOLE, LOG_OPERATE_TYPE_UPDATE);
        ResultData resultData = sysConsoleService.save(sysModuleModel);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_CONSOLE, LOG_OPERATE_TYPE_UPDATE);
        return resultData;
    }

    /**
     * 跳转404页面
     *
     * @return
     */
    @ApiOperation("跳转404页面")
    @RequestMapping(value = "error/404", method = RequestMethod.GET)
    public String error404() {
        return "page/error/404";
    }

    /**
     * 跳转error页面
     *
     * @return
     */
    @ApiOperation("跳转error页面")
    @RequestMapping(value = "error/error", method = RequestMethod.GET)
    public String error(HttpServletRequest httpServletRequest, ModelMap modelMap) {
        Object message = httpServletRequest.getAttribute(MESSAGE);
        if (message != null) {
            modelMap.addAttribute(MESSAGE, message);
        } else {
            modelMap.addAttribute(MESSAGE, TIPS_ERROR);
        }
        return "page/error/error";
    }

    /**
     * 跳转icon页面
     *
     * @return
     */
    @ApiOperation("跳转icon页面")
    @RequestMapping(value = "icon/list", method = RequestMethod.GET)
    public String icon() {
        return "page/iconList";
    }

}
