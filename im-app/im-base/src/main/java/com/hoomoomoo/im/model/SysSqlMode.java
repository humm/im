package com.hoomoomoo.im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hoomoomoo
 * @description 配置sql实体类
 * @package com.hoomoomoo.im.model
 * @date 2019/11/23
 */

@Data
@NoArgsConstructor
public class SysSqlMode {

    @ApiModelProperty(value="id", required = false)
    private String id;

    @ApiModelProperty(value="模式", required = false)
    private String mode;

    @ApiModelProperty(value="sql值", required = false)
    private String value;

    public SysSqlMode(String id, String mode, String value) {
        this.id = id;
        this.mode = mode;
        this.value = value;
    }
}
