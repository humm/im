package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.model.SysGiftModel;
import com.hoomoomoo.im.model.SysGiftQueryModel;
import com.hoomoomoo.im.model.base.PageModel;
import com.hoomoomoo.im.model.base.ResultData;
import com.hoomoomoo.im.service.SysGiftService;
import com.hoomoomoo.im.service.SysSystemService;
import com.hoomoomoo.im.util.SysLogUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.hoomoomoo.im.consts.BaseConst.HAS_BUTTON;
import static com.hoomoomoo.im.consts.BaseCueConst.*;

/**
 * @author hoomoomoo
 * @description 随礼信息控制类
 * @package com.hoomoomoo.im.controller
 * @date 2019/09/07
 */

@Controller
@RequestMapping("/gift")
public class SysGiftController {

    private static final Logger logger = LoggerFactory.getLogger(SysGiftController.class);

    @Autowired
    private SysGiftService sysGiftService;

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
        return "page/giftList";
    }


    /**
     * 跳转详情页面
     *
     * @return
     */
    @ApiOperation("跳转详情页面")
    @RequestMapping(value = "detail", method = RequestMethod.GET)
    public String detail() {
        return "page/giftDetail";
    }

    /**
     * 跳转新增页面
     *
     * @return
     */
    @ApiOperation("跳转新增页面")
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add() {
        return "page/giftAdd";
    }

    /**
     * 跳转修改页面
     *
     * @return
     */
    @ApiOperation("跳转修改页面")
    @RequestMapping(value = "update", method = RequestMethod.GET)
    public String update() {
        return "page/giftUpdate";
    }

    /**
     * 分页查询随礼信息
     *
     * @param sysGiftQueryModel
     * @return
     */
    @ApiOperation("分页查询随礼信息")
    @RequestMapping(value = "selectPage", method = RequestMethod.GET)
    @ResponseBody
    public PageModel<SysGiftModel> selectPage(SysGiftQueryModel sysGiftQueryModel) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_GIFT, LOG_OPERATE_TYPE_SELECT_PAGE);
        PageModel<SysGiftModel> sysGiftModelPageModel = sysGiftService.selectPage(sysGiftQueryModel);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_GIFT, LOG_OPERATE_TYPE_SELECT_PAGE);
        return sysGiftModelPageModel;
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
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_GIFT, LOG_OPERATE_TYPE_SELECT_INIT);
        ResultData resultData = sysGiftService.selectInitData();
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_GIFT, LOG_OPERATE_TYPE_SELECT_INIT);
        return resultData;
    }

    /**
     * 删除随礼信息
     *
     * @param giftIds
     * @return
     */
    @ApiOperation("删除随礼信息")
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public ResultData delete(
            @ApiParam(value = "随礼信息ID", required = true)
            @RequestParam String giftIds) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_GIFT, LOG_OPERATE_TYPE_DELETE);
        ResultData resultData = sysGiftService.delete(giftIds);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_GIFT, LOG_OPERATE_TYPE_DELETE);
        return resultData;
    }

    /**
     * 查询随礼信息
     *
     * @param giftId
     * @return
     */
    @ApiOperation("查询随礼信息")
    @RequestMapping(value = "selectOne", method = RequestMethod.GET)
    @ResponseBody
    public ResultData selectOne(
            @ApiParam(value = "随礼信息ID", required = true)
            @RequestParam String giftId,
            @ApiParam(value = "是否翻译", required = true)
            @RequestParam Boolean isTranslate) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_GIFT, LOG_OPERATE_TYPE_SELECT);
        ResultData resultData = sysGiftService.selectOne(giftId, isTranslate);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_GIFT, LOG_OPERATE_TYPE_SELECT);
        return resultData;
    }

    /**
     * 保存随礼信息
     *
     * @param sysIncomeModel
     * @return
     */
    @ApiOperation("保存随礼信息")
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public ResultData save(SysGiftModel sysIncomeModel) {
        String operateType = sysIncomeModel.getGiftId() == null ? LOG_OPERATE_TYPE_ADD : LOG_OPERATE_TYPE_UPDATE;
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_INCOME, operateType);
        ResultData resultData = sysGiftService.save(sysIncomeModel);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_INCOME, operateType);
        return resultData;
    }
}
