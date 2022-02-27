package im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description 饼图数据实体类
 * @package com.hoomoomoo.im.model
 * @date 2019/09/10
 */

@Data
public class SysReportPieModel {

    @ApiModelProperty(value = "名称", required = false)
    private String name;

    @ApiModelProperty(value = "数据", required = false)
    private SysReportPieSubModel[] data;
}
