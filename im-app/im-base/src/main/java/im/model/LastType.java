package im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description 最近一次操作类型
 * @package im.model.common
 * @date 2019/09/01
 */

@Data
public class LastType {

    @ApiModelProperty(value="收入类型", required = false)
    private String incomeType;

    @ApiModelProperty(value="随礼类型", required = false)
    private String giftType;

    @ApiModelProperty(value="收入来源", required = false)
    private String incomeCompany;
}
