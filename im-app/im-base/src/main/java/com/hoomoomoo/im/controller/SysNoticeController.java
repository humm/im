package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.config.bean.SystemConfigBean;
import com.hoomoomoo.im.model.SysNoticeModel;
import com.hoomoomoo.im.model.SysNoticeQueryModel;
import com.hoomoomoo.im.model.base.PageModel;
import com.hoomoomoo.im.model.base.ResultData;
import com.hoomoomoo.im.util.SysCommonUtils;
import com.hoomoomoo.im.util.SysLogUtils;
import com.hoomoomoo.im.service.SysNoticeService;
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

import javax.servlet.http.HttpServletRequest;

import static com.hoomoomoo.im.consts.BaseConst.REQUEST_URL;
import static com.hoomoomoo.im.consts.BaseCueConst.*;

/**
 * @author hoomoomoo
 * @description 消息通知控制类
 * @package com.hoomoomoo.im.controller
 * @date 2019/10/26
 */

@Controller
@RequestMapping("/notice")
public class SysNoticeController {

    private static final Logger logger = LoggerFactory.getLogger(SysNoticeController.class);
    
    @Autowired
    private SysNoticeService sysNoticeService;

    @Autowired
    private SystemConfigBean systemConfigBean;

    /**
     * 跳转列表页面
     *
     * @return
     */
    @ApiOperation("跳转列表页面")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String list(ModelMap modelMap, HttpServletRequest httpServletRequest) {
        modelMap.addAttribute(REQUEST_URL, SysCommonUtils.getConnectUrl(httpServletRequest, systemConfigBean.getAppName()));
        return "page/noticeList";
    }

    /**
     * 跳转详情页面
     *
     * @return
     */
    @ApiOperation("跳转详情页面")
    @RequestMapping(value = "detail", method = RequestMethod.GET)
    public String detail() {
        return "page/noticeDetail";
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
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_NOTICE, LOG_OPERATE_TYPE_SELECT_INIT);
        ResultData resultData = sysNoticeService.selectInitData();
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_NOTICE, LOG_OPERATE_TYPE_SELECT_INIT);
        return resultData;
    }

    /**
     * 分页查询消息通知信息
     *
     * @param sysNoticeQueryModel
     * @return
     */
    @ApiOperation("分页查询消息通知信息")
    @RequestMapping(value = "selectPage", method = RequestMethod.GET)
    @ResponseBody
    public PageModel<SysNoticeModel> selectPage(SysNoticeQueryModel sysNoticeQueryModel) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_NOTICE, LOG_OPERATE_TYPE_SELECT_PAGE);
        PageModel pageModel = sysNoticeService.selectPage(sysNoticeQueryModel);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_NOTICE, LOG_OPERATE_TYPE_SELECT_PAGE);
        return pageModel;
    }

    /**
     * 查询消息通知信息
     *
     * @param noticeId
     * @return
     */
    @ApiOperation("查询消息通知信息")
    @RequestMapping(value = "selectOne", method = RequestMethod.GET)
    @ResponseBody
    public ResultData selectOne(
            @ApiParam(value = "消息通知信息ID", required = true)
            @RequestParam String noticeId,
            @ApiParam(value = "是否翻译", required = true)
            @RequestParam Boolean isTranslate) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_NOTICE, LOG_OPERATE_TYPE_SELECT);
        ResultData resultData = sysNoticeService.selectOne(noticeId, isTranslate);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_NOTICE, LOG_OPERATE_TYPE_SELECT);
        return resultData;
    }

    /**
     * 修改消息通知信息
     *
     * @param isAll
     * @param noticeIds
     * @return
     */
    @ApiOperation("修改消息通知信息")
    @RequestMapping(value = "updateReadStatus", method = RequestMethod.POST)
    @ResponseBody
    public ResultData selectOne(@ApiParam(value = "是否全部已读", required = false)String isAll,
                                @ApiParam(value = "通知消息ID", required = false)String noticeIds){
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_NOTICE, LOG_OPERATE_TYPE_UPDATE);
        ResultData resultData = sysNoticeService.updateReadStatus(isAll, noticeIds);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_NOTICE, LOG_OPERATE_TYPE_UPDATE);
        return resultData;
    }
}
