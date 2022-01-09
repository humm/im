package im.model;

import im.model.base.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description 接口请求信息实体类
 * @package im.model
 * @date 2019/11/24
 */

@Data
public class SysInterfaceRequestModel extends BaseModel {

    @ApiModelProperty(value="业务日期", required = false)
    private String date;

    @ApiModelProperty(value="业务金额", required = false)
    private String amount;

    @ApiModelProperty(value="业务类型", required = false)
    private String type;

    @ApiModelProperty(value="业务子类型", required = false)
    private String subType;

    @ApiModelProperty(value="当前用户", required = false)
    private String user;

    @ApiModelProperty(value="目标对象", required = false)
    private String target;

    @ApiModelProperty(value="备注", required = false)
    private String memo;
}
