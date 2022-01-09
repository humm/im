package im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author hoomoomoo
 * @description 邮件实体类
 * @package im.model
 * @date 2019/08/04
 */

@Data
public class SysMailModel {

    @ApiModelProperty(value="邮件主题", required = false)
    private String subject;

    @ApiModelProperty(value="邮件内容", required = false)
    private String content;

    @ApiModelProperty(value="邮件ID", required = false)
    private String mailId;

    @ApiModelProperty(value="收件人", required = false)
    private String to;

    @ApiModelProperty(value="发件人", required = false)
    private String from;

    @ApiModelProperty(value="收件人", required = false)
    private List<String> filePath;

}
