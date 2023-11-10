package com.hoomoomoo.im.dto;

import lombok.Data;

@Data
public class ColumnDto extends BaseDto{

    private String columnCode;

    private String columnName;

    private String columnType;

    // 数据库类型
    private String columnDataType;

    // 数据库精度
    private String columnDataPrecision;

    // 数据库小数位
    private String columnDataScale;

    // 数据库默认值
    private String columnDataDefault;

    // 忽略标识
    private boolean skip;

    // 主键标识
    private boolean primary;

}
