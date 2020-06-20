package com.hoomoomoo.im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hoomoomoo
 * @description 首页数据实体类
 * @package com.hoomoomoo.im.model
 * @date 2019/10/27
 */

@Data
public class SysConsoleModel {

    @ApiModelProperty(value="用户业务信息", required = false)
    private List<SysBusinessModel> user                  = new ArrayList<>();

    @ApiModelProperty(value="登入日志数据", required = false)
    private List<SysItemModel> login                     = new ArrayList<>();

    @ApiModelProperty(value="版本信息", required = false)
    private List<SysItemModel> version                   = new ArrayList<>();

    @ApiModelProperty(value="统计开始时间", required = false)
    private List<SysItemModel> tips                      = new ArrayList<>();

    @ApiModelProperty(value="用户信息", required = false)
    private List<SysItemModel> register                  = new ArrayList<>();

    @ApiModelProperty(value="模块控制信息", required = false)
    private SysModuleModel sysConfig;

    @ApiModelProperty(value="未读消息通知", required = false)
    private String readNum;

    @ApiModelProperty(value="权限信息", required = false)
    private SysAuthModel sysAuthModel;

}
