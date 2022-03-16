package com.hoomoomoo.im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description 微信用户操作实体类
 * @package com.hoomoomoo.im.model
 * @date 2020/02/28
 */

@Data
public class SysWeChatOperateModel {

    @ApiModelProperty(value="操作代码", required = false)
    private String operateFlow;

    @ApiModelProperty(value="操作时间", required = false)
    private Long operateTime;
}
