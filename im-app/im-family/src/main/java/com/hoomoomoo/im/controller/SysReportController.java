package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.model.base.ResultData;
import com.hoomoomoo.im.service.SysReportService;
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

import static com.hoomoomoo.im.consts.BaseCueConst.*;

/**
 * @author hoomoomoo
 * @description 报表控制类
 * @package com.hoomoomoo.im.controller
 * @date 2019/09/08
 */

@Controller
@RequestMapping("/report")
public class SysReportController {

    private static final Logger logger = LoggerFactory.getLogger(SysReportController.class);

    @Autowired
    private SysReportService sysReportService;

    /**
     * 跳转收入信息报表页面
     *
     * @return
     */
    @ApiOperation("跳转收入信息报表页面")
    @RequestMapping(value = "income", method = RequestMethod.GET)
    public String incomeReport() {
        return "page/incomeReport";
    }

    /**
     * 跳转送礼信息报表页面
     *
     * @return
     */
    @ApiOperation("跳转送礼信息报表页面")
    @RequestMapping(value = "giftSend", method = RequestMethod.GET)
    public String giftSendReport() {
        return "page/giftSendReport";
    }

    /**
     * 跳转收礼信息报表页面
     *
     * @return
     */
    @ApiOperation("跳转收礼信息报表页面")
    @RequestMapping(value = "giftReceive", method = RequestMethod.GET)
    public String giftReceiveReport() {
        return "page/giftReceiveReport";
    }

    /**
     * 查询报表数据
     *
     * @return
     */
    @ApiOperation("查询报表数据")
    @RequestMapping(value = "initData", method = RequestMethod.GET)
    @ResponseBody
    public ResultData initData(
            @ApiParam(value = "报表模式", required = true)
            @RequestParam String reportMode,
            @ApiParam(value = "报表类型", required = true)
            @RequestParam String reportType,
            @ApiParam(value = "报表子类型", required = true)
            @RequestParam String reportSubType,
            @ApiParam(value = "报表选值", required = false)
            @RequestParam(required = false) String reportValue) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_REPORT, LOG_OPERATE_TYPE_SELECT);
        ResultData resultData = sysReportService.initData(reportMode, reportType, reportSubType, reportValue);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_REPORT, LOG_OPERATE_TYPE_SELECT);
        return resultData;
    }
}
