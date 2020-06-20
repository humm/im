package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.model.SysIncomeModel;
import com.hoomoomoo.im.model.SysIncomeQueryModel;
import com.hoomoomoo.im.model.base.PageModel;
import com.hoomoomoo.im.model.base.ResultData;
import com.hoomoomoo.im.service.SysIncomeService;
import com.hoomoomoo.im.service.SysSystemService;
import com.hoomoomoo.im.util.SysLogUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;


import static com.hoomoomoo.im.consts.BaseConst.HAS_BUTTON;
import static com.hoomoomoo.im.consts.BaseCueConst.*;

/**
 * @author hoomoomoo
 * @description 收入信息控制类
 * @package com.hoomoomoo.im.controller
 * @date 2019/08/10
 */

@Controller
@RequestMapping("/income")
public class SysIncomeController {

    private static final Logger logger = LoggerFactory.getLogger(SysIncomeController.class);

    @Autowired
    private SysIncomeService sysIncomeService;

    @Autowired
    private SysSystemService sysSystemService;

    /**
     * 跳转列表页面
     *
     * @return
     */
    @ApiOperation("跳转列表页面")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String list(ModelMap modelMap,
                           @ApiParam(value = "菜单ID", required = true)
                           @RequestParam String menuId) {
        modelMap.addAttribute(HAS_BUTTON, sysSystemService.selectButtonAuthority(menuId));
        return "page/incomeList";
    }


    /**
     * 跳转详情页面
     *
     * @return
     */
    @ApiOperation("跳转详情页面")
    @RequestMapping(value = "detail", method = RequestMethod.GET)
    public String detail() {
        return "page/incomeDetail";
    }

    /**
     * 跳转新增页面
     *
     * @return
     */
    @ApiOperation("跳转新增页面")
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add() {
        return "page/incomeAdd";
    }

    /**
     * 跳转修改页面
     *
     * @return
     */
    @ApiOperation("跳转修改页面")
    @RequestMapping(value = "update", method = RequestMethod.GET)
    public String update() {
        return "page/incomeUpdate";
    }


    /**
     * 分页查询收入信息
     *
     * @param sysIncomeQueryModel
     * @return
     */
    @ApiOperation("分页查询收入信息")
    @RequestMapping(value = "selectPage", method = RequestMethod.GET)
    @ResponseBody
    public PageModel<SysIncomeModel> selectPage(SysIncomeQueryModel sysIncomeQueryModel) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_INCOME, LOG_OPERATE_TYPE_SELECT_PAGE);
        PageModel<SysIncomeModel> page = sysIncomeService.selectPage(sysIncomeQueryModel);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_INCOME, LOG_OPERATE_TYPE_SELECT_PAGE);
        return page;
    }

    /**
     * 查询页面初始化信息
     *
     * @return
     */
    @ApiOperation("查询页面初始化信息")
    @RequestMapping(value = "selectInitData", method = RequestMethod.GET)
    @ResponseBody
    public ResultData selectInitData() {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_INCOME, LOG_OPERATE_TYPE_SELECT_INIT);
        ResultData resultData = sysIncomeService.selectInitData();
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_INCOME, LOG_OPERATE_TYPE_SELECT_INIT);
        return resultData;
    }

    /**
     * 删除收入信息
     *
     * @param incomeIds
     * @return
     */
    @ApiOperation("删除收入信息")
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public ResultData delete(
            @ApiParam(value = "收入信息ID", required = true)
            @RequestParam String incomeIds) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_INCOME, LOG_OPERATE_TYPE_DELETE);
        ResultData resultData = sysIncomeService.delete(incomeIds);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_INCOME, LOG_OPERATE_TYPE_DELETE);
        return resultData;
    }


    /**
     * 查询收入信息
     *
     * @param incomeId
     * @return
     */
    @ApiOperation("查询收入信息")
    @RequestMapping(value = "selectOne", method = RequestMethod.GET)
    @ResponseBody
    public ResultData selectOne(
            @ApiParam(value = "收入信息ID", required = true)
            @RequestParam String incomeId,
            @ApiParam(value = "是否翻译", required = true)
            @RequestParam Boolean isTranslate) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_INCOME, LOG_OPERATE_TYPE_SELECT);
        ResultData resultData = sysIncomeService.selectOne(incomeId, isTranslate);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_INCOME, LOG_OPERATE_TYPE_SELECT);
        return resultData;
    }

    /**
     * 保存收入信息
     *
     * @param sysIncomeModel
     * @return
     */
    @ApiOperation("保存收入信息")
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public ResultData save(SysIncomeModel sysIncomeModel) {
        String operateType = sysIncomeModel.getIncomeId() == null ? LOG_OPERATE_TYPE_ADD : LOG_OPERATE_TYPE_UPDATE;
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_INCOME, operateType);
        ResultData resultData = sysIncomeService.save(sysIncomeModel);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_INCOME, operateType);
        return resultData;
    }

}
