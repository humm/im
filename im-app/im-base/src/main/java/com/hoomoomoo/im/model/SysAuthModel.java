package com.hoomoomoo.im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description 首页权限实体类
 * im.model
 * @date 2020/03/22
 */

@Data
public class SysAuthModel {

    @ApiModelProperty(value="收入信息", required = false)
    private Boolean income;

    @ApiModelProperty(value="随礼信息", required = false)
    private Boolean gift;

    @ApiModelProperty(value="通知信息", required = false)
    private Boolean notice;

}
