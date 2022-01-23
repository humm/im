package com.hoomoomoo.im.dto;

import lombok.Data;

import java.util.ArrayList;
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

    public Integer orderNumValue;

    public String type;

    public String typeName;

    public ShoppingDto() {
        this.goodsDtoList = new ArrayList<>();
        this.orderNumValue = 0;
    }
}
