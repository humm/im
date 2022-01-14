package com.hoomoomoo.im.dto;

import lombok.Data;

import java.util.List;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.dto
 * @date 2022/1/14
 */
@Data
public class ShoppingDto extends BaseDto{

    public List<GoodsDto> goodsDtoList;

    public Integer orderNumValue = 0;
}
