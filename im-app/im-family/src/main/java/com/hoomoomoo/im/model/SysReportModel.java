package com.hoomoomoo.im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


/**
 * @author hoomoomoo
 * @description 报表信息实体类
 * @package com.hoomoomoo.im.model
 * @date 2019/09/08
 */

@Data
public class SysReportModel {

    @ApiModelProperty(value = "报表标题", required = false)
    private String title;

    @ApiModelProperty(value = "报表子标题", required = false)
    private String subTitle;

    @ApiModelProperty(value = "报表子标题", required = false)
    private String[] legendData;

    @ApiModelProperty(value = "x轴数据", required = false)
    private String[] xAxisData;

    @ApiModelProperty(value = "y轴数据", required = false)
    private List<SysReportYaxisModel> yAxisData;

    @ApiModelProperty(value = "饼图数据", required = false)
    private List<SysReportPieModel> pieData;

    @ApiModelProperty(value = "用户ID", required = false)
    private List<String> userList;

    @ApiModelProperty(value = "报表数据", required = false)
    private Double reportNum;

    @ApiModelProperty(value = "报表时间", required = false)
    private String reportDate;

    @ApiModelProperty(value = "报表名称", required = false)
    private String reportName;

}
