package com.hoomoomoo.im.controller;

/**
 * @author hoomoomoo
 * @description 修订信息控制类
 * @package com.hoomoomoo.im.controller
 * @date 2019/11/23
 */

import com.hoomoomoo.im.model.base.ResultData;
import com.hoomoomoo.im.util.SysLogUtils;
import com.hoomoomoo.im.service.SysParameterService;
import com.hoomoomoo.im.service.SysVersionService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.hoomoomoo.im.consts.BaseConst.BUSINESS_TYPE_VERSION;
import static com.hoomoomoo.im.consts.ParameterConst.VERSION;
import static com.hoomoomoo.im.consts.BaseCueConst.*;

@Controller
@RequestMapping("/version")
public class SysVersionController {

    private static final Logger logger = LoggerFactory.getLogger(SysVersionController.class);

    @Autowired
    private SysVersionService sysVersionService;

    @Autowired
    private SysParameterService sysParameterService;

    /**
     * 跳转列表页面
     *
     * @return
     */
    @ApiOperation("跳转列表页面")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String list(ModelMap modelMap) {
        modelMap.addAttribute(BUSINESS_TYPE_VERSION, sysParameterService.getParameterString(VERSION));
        return "page/versionList";
    }

    /**
     * 查询修订信息
     *
     * @return
     */
    @ApiOperation("查询修订信息")
    @RequestMapping(value = "selectList", method = RequestMethod.GET)
    @ResponseBody
    public ResultData selectList() {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_VERSION, LOG_OPERATE_TYPE_SELECT);
        ResultData resultData = sysVersionService.selectList();
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_VERSION, LOG_OPERATE_TYPE_SELECT);
        return resultData;
    }

}
