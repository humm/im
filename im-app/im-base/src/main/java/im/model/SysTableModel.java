package im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description 数据表字段信息实体类
 * @package com.hoomoomoo.im.model
 * @date 2019/11/29
 */

@Data
public class SysTableModel {

    @ApiModelProperty(value="字段代码", required = false)
    private String columnCode;

    @ApiModelProperty(value="字段代码", required = false)
    private String columnComments;

    @ApiModelProperty(value="字段类型", required = false)
    private String columnType;

    @ApiModelProperty(value="数据数量", required = false)
    private Long dataCount;
}
