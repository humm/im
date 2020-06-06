package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.model.SysIncomeModel;
import com.hoomoomoo.im.model.SysLoginLogQueryModel;
import com.hoomoomoo.im.model.PageModel;
import com.hoomoomoo.im.model.ResultData;
import com.hoomoomoo.im.service.SysLoginLogService;
import com.hoomoomoo.im.util.SysLogUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.hoomoomoo.im.consts.BusinessCueConst.*;


/**
 * @author hoomoomoo
 * @description 登入日志控制类
 * @package com.hoomoomoo.im.controller
 * @date 2019/10/26
 */

@Controller
@RequestMapping("/loginLog")
public class SysLoginLogController {

    private static final Logger logger = LoggerFactory.getLogger(SysLoginLogController.class);

    @Autowired
    private SysLoginLogService sysLoginLogService;

    /**
     * 跳转列表页面
     *
     * @return
     */
    @ApiOperation("跳转列表页面")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String list() {
        return "page/loginLogList";
    }

    /**
     * 跳转详情页面
     *
     * @return
     */
    @ApiOperation("跳转详情页面")
    @RequestMapping(value = "detail", method = RequestMethod.GET)
    public String detail() {
        return "page/loginLogDetail";
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
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_LOGIN_LOG, LOG_OPERATE_TYPE_SELECT_INIT);
        ResultData resultData = sysLoginLogService.selectInitData();
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_LOGIN_LOG, LOG_OPERATE_TYPE_SELECT_INIT);
        return resultData;
    }

    /**
     * 分页查询登入日志信息
     *
     * @param sysLoginLogQueryModel
     * @return
     */
    @ApiOperation("分页查询登入日志信息")
    @RequestMapping(value = "selectPage", method = RequestMethod.GET)
    @ResponseBody
    public PageModel<SysIncomeModel> selectPage(SysLoginLogQueryModel sysLoginLogQueryModel) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_LOGIN_LOG, LOG_OPERATE_TYPE_SELECT_PAGE);
        PageModel pageModel = sysLoginLogService.selectPage(sysLoginLogQueryModel);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_LOGIN_LOG, LOG_OPERATE_TYPE_SELECT_PAGE);
        return pageModel;
    }

    /**
     * 查询登入日志信息
     *
     * @param logId
     * @return
     */
    @ApiOperation("查询登入日志信息")
    @RequestMapping(value = "selectOne", method = RequestMethod.GET)
    @ResponseBody
    public ResultData selectOne(
            @ApiParam(value = "登入日志信息ID", required = true)
            @RequestParam String logId,
            @ApiParam(value = "是否翻译", required = true)
            @RequestParam Boolean isTranslate) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_LOGIN_LOG, LOG_OPERATE_TYPE_SELECT);
        ResultData resultData = sysLoginLogService.selectOne(logId, isTranslate);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_LOGIN_LOG, LOG_OPERATE_TYPE_SELECT);
        return resultData;
    }
}
