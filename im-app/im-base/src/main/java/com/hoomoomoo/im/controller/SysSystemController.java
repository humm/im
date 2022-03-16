package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.model.SysModuleModel;
import com.hoomoomoo.im.model.base.ResultData;
import com.hoomoomoo.im.util.SysBeanUtils;
import com.hoomoomoo.im.util.SysLogUtils;
import com.hoomoomoo.im.util.SysSessionUtils;
import com.hoomoomoo.im.service.SysSystemService;
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

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.BaseCueConst.*;


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
    private SysSystemService sysSystemService;

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
     * 跳转模块编辑页面
     *
     * @return
     */
    @ApiOperation("跳转模块编辑页面")
    @RequestMapping(value = "module", method = RequestMethod.GET)
    public String module(ModelMap modelMap) {
        SysModuleModel sysModuleModel = sysSystemService.selectConfigModule();
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
    @RequestMapping(value = "module/save", method = RequestMethod.POST)
    @ResponseBody
    public ResultData save(SysModuleModel sysModuleModel) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_CONSOLE, LOG_OPERATE_TYPE_UPDATE);
        ResultData resultData = sysSystemService.save(sysModuleModel);
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
