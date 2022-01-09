package im.model;

import im.model.base.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description TODO
 * @package im.model
 * @date 2019/08/11
 */

@Data
public class  DemoModel extends BaseModel {

    @ApiModelProperty(value = "代码", required = false)
    private String code;

    @ApiModelProperty(value = "消息", required = false)
    private String msg;
}
