package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.model.SysRoleModel;
import com.hoomoomoo.im.model.SysRoleQueryModel;
import com.hoomoomoo.im.model.PageModel;
import com.hoomoomoo.im.model.ResultData;
import com.hoomoomoo.im.service.SysRoleService;
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

import static com.hoomoomoo.im.consts.CueConst.*;

/**
 * @author hoomoomoo
 * @description 角色信息
 * @package com.hoomoomoo.im.controller
 * @date 2019/09/27
 */

@Controller
@RequestMapping("/role")
public class SysRoleController {

    private static final Logger logger = LoggerFactory.getLogger(SysRoleController.class);

    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 跳转列表页面
     *
     * @return
     */
    @ApiOperation("跳转列表页面")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String list() {
        return "page/roleList";
    }


    /**
     * 跳转详情页面
     *
     * @return
     */
    @ApiOperation("跳转详情页面")
    @RequestMapping(value = "detail", method = RequestMethod.GET)
    public String detail() {
        return "page/roleDetail";
    }

    /**
     * 跳转新增页面
     *
     * @return
     */
    @ApiOperation("跳转新增页面")
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add() {
        return "page/roleAdd";
    }

    /**
     * 跳转修改页面
     *
     * @return
     */
    @ApiOperation("跳转修改页面")
    @RequestMapping(value = "update", method = RequestMethod.GET)
    public String update() {
        return "page/roleUpdate";
    }


    /**
     * 分页查询角色信息
     *
     * @param sysRoleQueryModel
     * @return
     */
    @ApiOperation("分页查询角色信息")
    @RequestMapping(value = "selectPage", method = RequestMethod.GET)
    @ResponseBody
    public PageModel<SysRoleModel> selectPage(SysRoleQueryModel sysRoleQueryModel) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_ROLE, LOG_OPERATE_TYPE_SELECT_PAGE);
        PageModel<SysRoleModel> page = sysRoleService.selectPage(sysRoleQueryModel);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_ROLE, LOG_OPERATE_TYPE_SELECT_PAGE);
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
    public ResultData selectInitData(@ApiParam(value = "是否禁用", required = false)
                                     @RequestParam(required = false) String disabled,
                                     @ApiParam(value = "角色信息ID", required = false)
                                     @RequestParam(required = false) String roleId) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_ROLE, LOG_OPERATE_TYPE_SELECT_INIT);
        ResultData resultData = sysRoleService.selectInitData(disabled, roleId);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_ROLE, LOG_OPERATE_TYPE_SELECT_INIT);
        return resultData;
    }

    /**
     * 删除角色信息
     *
     * @param roleIds
     * @return
     */
    @ApiOperation("删除角色信息")
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public ResultData delete(
            @ApiParam(value = "角色信息ID", required = true)
            @RequestParam String roleIds) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_ROLE, LOG_OPERATE_TYPE_DELETE);
        ResultData resultData = sysRoleService.delete(roleIds);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_ROLE, LOG_OPERATE_TYPE_DELETE);
        return resultData;
    }


    /**
     * 查询角色信息
     *
     * @param roleId
     * @return
     */
    @ApiOperation("查询角色信息")
    @RequestMapping(value = "selectOne", method = RequestMethod.GET)
    @ResponseBody
    public ResultData selectOne(
            @ApiParam(value = "角色信息ID", required = true)
            @RequestParam String roleId,
            @ApiParam(value = "是否翻译", required = true)
            @RequestParam Boolean isTranslate) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_ROLE, LOG_OPERATE_TYPE_SELECT);
        ResultData resultData = sysRoleService.selectOne(roleId, isTranslate);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_ROLE, LOG_OPERATE_TYPE_SELECT);
        return resultData;
    }

    /**
     * 保存角色信息
     *
     * @param sysRoleModel
     * @return
     */
    @ApiOperation("保存角色信息")
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public ResultData save(SysRoleModel sysRoleModel) {
        String operateType = sysRoleModel.getRoleId() == null ? LOG_OPERATE_TYPE_ADD : LOG_OPERATE_TYPE_UPDATE;
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_ROLE, operateType);
        ResultData resultData = sysRoleService.save(sysRoleModel);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_ROLE, operateType);
        return resultData;
    }

    /**
     * 校验角色信息
     *
     * @param sysRoleQueryModel
     * @return
     */
    @ApiOperation("校验角色信息")
    @RequestMapping(value = "checkRoleCode", method = RequestMethod.GET)
    @ResponseBody
    public ResultData checkRoleCode(SysRoleQueryModel sysRoleQueryModel) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_ROLE, LOG_OPERATE_TYPE_CHECK);
        ResultData resultData = sysRoleService.checkRoleCode(sysRoleQueryModel);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_ROLE, LOG_OPERATE_TYPE_CHECK);
        return resultData;
    }
}
