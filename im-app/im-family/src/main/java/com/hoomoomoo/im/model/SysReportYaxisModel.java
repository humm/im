package com.hoomoomoo.im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description 报表信息Y轴实体类
 * @package com.hoomoomoo.im.model
 * @date 2019/09/08
 */

@Data
public class SysReportYaxisModel {

    @ApiModelProperty(value = "名称", required = false)
    private String name;

    @ApiModelProperty(value = "数据", required = false)
    private Double[] data;
}
