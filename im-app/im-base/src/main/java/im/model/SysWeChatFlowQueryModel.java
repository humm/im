package im.model;

import im.model.base.QueryBaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description 微信流程步骤查询实体类
 * @package com.hoomoomoo.im.model
 * @date 2020/02/29
 */

@Data
public class SysWeChatFlowQueryModel extends QueryBaseModel {

    @ApiModelProperty(value="流程步骤ID", required = false)
    private String flowId;

    @ApiModelProperty(value="流程步骤序号", required = false)
    private String flowNum;

    @ApiModelProperty(value="流程步骤代码", required = false)
    private String flowCode;

    @ApiModelProperty(value="流程步骤描述", required = false)
    private String flowDescribe;

    @ApiModelProperty(value="流程步骤提示信息", required = false)
    private String flowTips;

    @ApiModelProperty(value="下级流程步骤代码", required = false)
    private String nextFlowCode;

    @ApiModelProperty(value="流程步骤类型", required = false)
    private String flowType;

    @ApiModelProperty(value="流程步骤排序", required = false)
    private String flowOrder;

    @ApiModelProperty(value="是否显示服务", required = false)
    private String isShow;
}
