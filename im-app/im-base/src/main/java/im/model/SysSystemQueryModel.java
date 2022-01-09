package im.model;

import im.model.base.QueryBaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description 系统级别公用查询实体类
 * @package im.model
 * @date 2019/10/13
 */

@Data
public class SysSystemQueryModel extends QueryBaseModel {

    @ApiModelProperty(value="菜单ID", required = false)
    private String menuId;

    @ApiModelProperty(value="用户ID", required = false)
    private String userId;
}
