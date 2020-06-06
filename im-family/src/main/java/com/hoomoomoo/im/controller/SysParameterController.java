package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.model.SysParameterModel;
import com.hoomoomoo.im.model.SysParameterQueryModel;
import com.hoomoomoo.im.model.ResultData;
import com.hoomoomoo.im.service.SysParameterService;
import com.hoomoomoo.im.util.SysLogUtils;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.hoomoomoo.im.consts.BusinessCueConst.*;

/**
 * @author hoomoomoo
 * @description 系统参数控制类
 * @package com.hoomoomoo.im.controller
 * @date 2019/10/20
 */

@Controller
@RequestMapping("/parameter")
public class SysParameterController {

    private static final Logger logger = LoggerFactory.getLogger(SysParameterController.class);

    @Autowired
    private SysParameterService sysParameterService;

    /**
     * 跳转列表页面
     *
     * @return
     */
    @ApiOperation("跳转列表页面")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String list() {
        return "page/parameterList";
    }

    /**
     * 跳转修改页面
     *
     * @return
     */
    @ApiOperation("跳转列表页面")
    @RequestMapping(value = "update", method = RequestMethod.GET)
    public String update() {
        return "page/parameterUpdate";
    }

    /**
     * 查询参数信息
     *
     * @param sysParameterQueryModel
     * @return
     */
    @ApiOperation("查询参数信息")
    @RequestMapping(value = "selectList", method = RequestMethod.GET)
    @ResponseBody
    public ResultData selectList(SysParameterQueryModel sysParameterQueryModel) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_PARAMETER, LOG_OPERATE_TYPE_SELECT);
        ResultData resultData = sysParameterService.selectList(sysParameterQueryModel);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_PARAMETER, LOG_OPERATE_TYPE_SELECT);
        return resultData;
    }

    /**
     * 保存参数信息
     *
     * @param sysParameterModel
     * @return
     */
    @ApiOperation("保存参数信息")
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public ResultData save(SysParameterModel sysParameterModel) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_PARAMETER, LOG_OPERATE_TYPE_UPDATE);
        ResultData resultData = sysParameterService.save(sysParameterModel);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_PARAMETER, LOG_OPERATE_TYPE_UPDATE);
        return resultData;
    }

}
