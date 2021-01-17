package com.hoomoomoo.im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description 报表信息查询实体类
 * @package com.hoomoomoo.im.model
 * @date 2019/09/08
 */

@Data
public class SysReportQueryModel {

    @ApiModelProperty(value="用户ID", required = false)
    private String userId;

    @ApiModelProperty(value="用户名称", required = false)
    private String userName;

    @ApiModelProperty(value="送礼人", required = false)
    private String giftSender;

    @ApiModelProperty(value="收礼人", required = false)
    private String giftReceiver;

    @ApiModelProperty(value="随礼模式", required = false)
    private String giftMode;

    @ApiModelProperty(value="报表模式", required = false)
    private String reportMode;

    @ApiModelProperty(value="报表类型", required = false)
    private String reportType;

    @ApiModelProperty(value="报表子类型", required = false)
    private String reportSubType;

    @ApiModelProperty(value="报表选值", required = false)
    private String reportValue;
}
