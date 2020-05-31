package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.model.SysUserModel;
import com.hoomoomoo.im.model.SysUserQueryModel;
import com.hoomoomoo.im.model.PageModel;
import com.hoomoomoo.im.model.ResultData;
import com.hoomoomoo.im.service.SysUserService;
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

import static com.hoomoomoo.im.consts.BusinessConst.PASSWORD;
import static com.hoomoomoo.im.consts.CueConst.*;

/**
 * @author hoomoomoo
 * @description 用户信息控制类
 * @package com.hoomoomoo.im.controller
 * @date 2019/09/21
 */

@Controller
@RequestMapping("/user")
public class SysUserController {

    private static final Logger logger = LoggerFactory.getLogger(SysUserController.class);

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysSystemService sysSystemService;

    /**
     * 跳转列表页面
     *
     * @return
     */
    @ApiOperation("跳转列表页面")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String list() {
        return "page/userList";
    }


    /**
     * 跳转详情页面
     *
     * @return
     */
    @ApiOperation("跳转详情页面")
    @RequestMapping(value = "detail", method = RequestMethod.GET)
    public String detail() {
        return "page/userDetail";
    }

    /**
     * 跳转新增页面
     *
     * @return
     */
    @ApiOperation("跳转新增页面")
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add() {
        return "page/userAdd";
    }

    /**
     * 跳转修改页面
     *
     * @return
     */
    @ApiOperation("跳转修改页面")
    @RequestMapping(value = "update", method = RequestMethod.GET)
    public String update() {
        return "page/userUpdate";
    }

    /**
     * 跳转修改密码页面
     *
     * @return
     */
    @ApiOperation("跳转修改密码页面")
    @RequestMapping(value = "password", method = RequestMethod.GET)
    public String password(ModelMap modelMap) {
        modelMap.addAttribute(PASSWORD, sysSystemService.selectUserPassword());
        return "page/password";
    }


    /**
     * 分页查询用户信息
     *
     * @param sysUserQueryModel
     * @return
     */
    @ApiOperation("分页查询用户信息")
    @RequestMapping(value = "selectPage", method = RequestMethod.GET)
    @ResponseBody
    public PageModel<SysUserModel> selectPage(SysUserQueryModel sysUserQueryModel) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_SELECT_PAGE);
        PageModel<SysUserModel> page = sysUserService.selectPage(sysUserQueryModel);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_SELECT_PAGE);
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
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_SELECT_INIT);
        ResultData resultData = sysUserService.selectInitData();
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_SELECT_INIT);
        return resultData;
    }

    /**
     * 删除用户信息
     *
     * @param userIds
     * @return
     */
    @ApiOperation("删除用户信息")
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public ResultData delete(
            @ApiParam(value = "用户信息ID", required = true)
            @RequestParam String userIds) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_DELETE);
        ResultData resultData = sysUserService.delete(userIds);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_DELETE);
        return resultData;
    }


    /**
     * 查询用户信息
     *
     * @param userId
     * @return
     */
    @ApiOperation("查询用户信息")
    @RequestMapping(value = "selectOne", method = RequestMethod.GET)
    @ResponseBody
    public ResultData selectOne(
            @ApiParam(value = "用户信息ID", required = true)
            @RequestParam String userId,
            @ApiParam(value = "是否翻译", required = true)
            @RequestParam Boolean isTranslate) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_SELECT);
        ResultData resultData = sysUserService.selectOne(userId, isTranslate);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_SELECT);
        return resultData;
    }

    /**
     * 保存用户信息
     *
     * @param sysUserModel
     * @return
     */
    @ApiOperation("保存用户信息")
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public ResultData save(SysUserModel sysUserModel) {
        String operateType = sysUserModel.getUserId() == null ? LOG_OPERATE_TYPE_ADD : LOG_OPERATE_TYPE_UPDATE;
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_USER, operateType);
        ResultData resultData = sysUserService.save(sysUserModel);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_USER, operateType);
        return resultData;
    }

    /**
     * 校验用户信息
     *
     * @param sysUserQueryModel
     * @return
     */
    @ApiOperation("校验用户信息")
    @RequestMapping(value = "checkUserCode", method = RequestMethod.GET)
    @ResponseBody
    public ResultData checkUserCode(SysUserQueryModel sysUserQueryModel) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_CHECK);
        ResultData resultData = sysUserService.checkUserCode(sysUserQueryModel);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_CHECK);
        return resultData;
    }

    /**
     * 重置用户密码
     *
     * @param userIds
     * @return
     */
    @ApiOperation("重置用户密码")
    @RequestMapping(value = "reset", method = RequestMethod.POST)
    @ResponseBody
    public ResultData reset(
            @ApiParam(value = "用户信息ID", required = true)
            @RequestParam String userIds) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_RESET_PASSWORD);
        ResultData resultData = sysUserService.reset(userIds);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_RESET_PASSWORD);
        return resultData;
    }

    /**
     * 修改用户密码
     *
     * @param password
     * @return
     */
    @ApiOperation("修改用户密码")
    @RequestMapping(value = "changPassword", method = RequestMethod.POST)
    @ResponseBody
    public ResultData changPassword(
            @ApiParam(value = "用户信息密码", required = true)
            @RequestParam String password) {
        SysLogUtils.controllerStart(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_UPDATE_PASSWORD);
        ResultData resultData = sysUserService.changPassword(password);
        SysLogUtils.controllerEnd(logger, LOG_BUSINESS_TYPE_USER, LOG_OPERATE_TYPE_UPDATE_PASSWORD);
        return resultData;
    }

}
