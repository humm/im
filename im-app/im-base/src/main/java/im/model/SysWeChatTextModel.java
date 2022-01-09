package im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description 微信文本消息实体类
 * @package im.model
 * @date 2020/02/27
 */

@Data
public class SysWeChatTextModel extends SysWeChatBaseModel{

    @ApiModelProperty(value="文本内容", required = false)
    private String Content;
}
