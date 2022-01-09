package im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description 配置信息实体类
 * @package im.model
 * @date 2019/11/26
 */

@Data
public class SysConfigModel {

    @ApiModelProperty(value="模块组代码", required = false)
    private String moduleGroupCode;

    @ApiModelProperty(value="模块组名称", required = false)
    private String moduleGroupName;

    @ApiModelProperty(value="模块代码", required = false)
    private String moduleCode;

    @ApiModelProperty(value="模块名称", required = false)
    private String moduleName;

    @ApiModelProperty(value="模块状态", required = false)
    private String moduleStatus;

    @ApiModelProperty(value="模块扩展参数", required = false)
    private String moduleExt;

    @ApiModelProperty(value="用户ID", required = false)
    private String userId;
}
