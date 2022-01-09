package im.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import im.model.base.QueryBaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author hoomoomoo
 * @description 消息通知查询实体类
 * @package im.model
 * @date 2019/10/26
 */

@Data
public class SysNoticeQueryModel extends QueryBaseModel {

    @ApiModelProperty(value="通知ID", required = false)
    private String noticeId;

    @ApiModelProperty(value="用户ID", required = false)
    private String userId;

    @ApiModelProperty(value="业务ID", required = false)
    private String businessId;

    @ApiModelProperty(value="业务类型", required = false)
    private String businessType;

    @ApiModelProperty(value="业务子类型", required = false)
    private String businessSubType;

    @ApiModelProperty(value="业务日期", required = false)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date businessDate;

    @ApiModelProperty(value="业务金额", required = false)
    private String businessAmount;

    @ApiModelProperty(value="通知状态", required = false)
    private String noticeStatus;

    @ApiModelProperty(value="通知类型", required = false)
    private String noticeType;

    @ApiModelProperty(value="阅读状态", required = false)
    private String readStatus;
}
