package com.hoomoomoo.im.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hoomoomoo.im.model.base.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author hoomoomoo
 * @description 随礼信息实体类
 * @package com.hoomoomoo.im.model
 * @date 2019/09/07
 */

@Data
public class SysGiftModel extends BaseModel {

    @ApiModelProperty(value="随礼ID", required = false)
    private String giftId;

    @ApiModelProperty(value="随礼类型", required = false)
    private String giftType;

    @ApiModelProperty(value="随礼类型", required = false)
    private String giftTypeCode;

    @ApiModelProperty(value="送礼人", required = false)
    private String giftSender;

    @ApiModelProperty(value="收礼人", required = false)
    private String giftReceiver;

    @ApiModelProperty(value="随礼日期", required = false)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date giftDate;

    @ApiModelProperty(value="随礼金额", required = false)
    private String giftAmount;

    @ApiModelProperty(value="随礼备注", required = false)
    private String giftMemo;

}
