package im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hoomoomoo
 * @description 接口信息处理返回结果实体类
 * @package com.hoomoomoo.im.model
 * @date 2020/03/05
 */

@Data
public class SysInterfaceResponseModel {

    @ApiModelProperty(value="处理结果", required = false)
    private Boolean result;

    @ApiModelProperty(value="业务流水号", required = false)
    private List<String> businessNo;

    @ApiModelProperty(value="提示信息", required = false)
    private String message;

    public SysInterfaceResponseModel() {
        this.result = true;
        this.businessNo = new ArrayList<>();
    }
}
