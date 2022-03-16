package com.hoomoomoo.im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hoomoomoo
 * @description 业务数据实体类
 * @package com.hoomoomoo.im.model
 * @date 2019/10/27
 */

@Data
@NoArgsConstructor
public class SysItemModel{

    @ApiModelProperty(value="标题", required = false)
    private String title;

    @ApiModelProperty(value="数据值", required = false)
    private String value;

    @ApiModelProperty(value="链接地址", required = false)
    private String href;

    public SysItemModel(String title, String value, String href) {
        this.title = title;
        this.value = value;
        this.href = href;
    }
}
