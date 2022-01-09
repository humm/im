package im.controller;

import im.model.base.ResultData;
import im.service.SysMenuService;
import im.util.SysLogUtils;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static im.consts.BaseCueConst.*;

/**
 * @author hoomoomoo
 * @description 菜单信息控制类
 * @package com.hoomoomoo.im.controller
 * @date 2019/10/11
 */

@RestController
@RequestMapping("/menu")
public class SysMenuController {

    private static final Logger logger = LoggerFactory.getLogger(SysMenuController.class);

    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 查询菜单数据
     *
     * @return
     */
    @ApiOperation("查询菜单数据")
    @RequestMapping(value = "initMenu", method = RequestMethod.GET)
    public ResultData initMenu(){
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_MENU, LOG_OPERATE_TYPE_SELECT);
        ResultData resultData = sysMenuService.selectMenu();
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_MENU, LOG_OPERATE_TYPE_SELECT);
        return resultData;
    }
}
