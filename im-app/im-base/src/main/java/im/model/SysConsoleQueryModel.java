package im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description 首页数据查询实体类
 * @package im.model
 * @date 2019/10/27
 */

@Data
public class SysConsoleQueryModel {

    @ApiModelProperty(value="用户ID", required = false)
    private String userId;

    @ApiModelProperty(value="年度开始时间", required = false)
    private String yearStartDate;

    @ApiModelProperty(value="用户名称", required = false)
    private String userName;

    @ApiModelProperty(value="是否计算", required = false)
    private String isCalc;

    @ApiModelProperty(value="查询条件", required = false)
    private String queryCondition;


}
