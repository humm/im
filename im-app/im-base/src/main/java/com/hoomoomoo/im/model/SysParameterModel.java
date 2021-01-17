package com.hoomoomoo.im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description 参数信息实体类
 * @package com.hoomoomoo.im.model
 * @date 2019/10/20
 */

@Data
public class SysParameterModel{

    @ApiModelProperty(value = "参数代码", required = false)
    private String parameterCode;

    @ApiModelProperty(value = "参数描述", required = false)
    private String parameterCaption;

    @ApiModelProperty(value = "参数值", required = false)
    private String parameterValue;

    @ApiModelProperty(value = "参数源值", required = false)
    private String parameterOldValue;

    @ApiModelProperty(value = "参数排序", required = false)
    private String parameterOrder;

    @ApiModelProperty(value = "参数类型", required = false)
    private String parameterType;

    @ApiModelProperty(value = "参数扩展值", required = false)
    private String parameterExt;

    @ApiModelProperty(value = "是否显示", required = false)
    private String isShow;

    @ApiModelProperty(value = "是否编辑", required = false)
    private String isEdit;

    @ApiModelProperty(value = "参数分组", required = false)
    private String parameterGroup;
}
