package com.hoomoomoo.im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hoomoomoo
 * @description 校验结果实体类
 * @package com.hoomoomoo.im.model
 * @date 2020/02/09
 */

@Data
public class SysCheckResultModel {

    @ApiModelProperty(value="校验结果", required = false)
    private Boolean result;

    @ApiModelProperty(value="提示信息", required = false)
    private List<String> message = new ArrayList<>();

    public SysCheckResultModel() {
    }

    public SysCheckResultModel(Boolean result) {
        this.result = result;
    }
}
