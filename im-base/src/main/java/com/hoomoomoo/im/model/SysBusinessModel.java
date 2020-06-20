package com.hoomoomoo.im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hoomoomoo
 * @description 业务数据实体类
 * @package com.hoomoomoo.im.model
 * @date 2019/10/27
 */

@Data
public class SysBusinessModel {


    @ApiModelProperty(value="标题", required = false)
    private String title;

    @ApiModelProperty(value="收入数据", required = false)
    private List<SysItemModel> income           = new ArrayList<>();

    @ApiModelProperty(value="送礼数据", required = false)
    private List<SysItemModel> giftSend         = new ArrayList<>();

    @ApiModelProperty(value="收礼数据", required = false)
    private List<SysItemModel> giftReceive      = new ArrayList<>();

}
