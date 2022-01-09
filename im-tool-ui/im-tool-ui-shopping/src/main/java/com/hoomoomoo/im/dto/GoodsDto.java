package com.hoomoomoo.im.dto;

import lombok.Data;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.dto
 * @date 2022/1/9
 */
@Data
public class GoodsDto extends BaseDto{

    private String orderId;

    private String goodsId;

    private String goodsName;

    private String status;

    private String appraiseInfo;

    private String appraiseImgUrl;
}
