package com.hoomoomoo.im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description 字典信息实体类
 * @package com.hoomoomoo.im.model
 * @date 2019/08/11
 */

@Data
public class SysDictionaryModel extends BaseModel {

    @ApiModelProperty(value="字典代码", required = false)
    private String dictionaryCode;

    @ApiModelProperty(value="字典选值", required = false)
    private String dictionaryItem;

    @ApiModelProperty(value="字典描述", required = false)
    private String dictionaryCaption;

    @ApiModelProperty(value="选值排序", required = false)
    private String itemOrder;

    @ApiModelProperty(value="代码排序", required = false)
    private String codeOrder;

    @ApiModelProperty(value="用户ID", required = false)
    private String userId;

    @ApiModelProperty(value="是否开放", required = false)
    private String isOpen;
}
