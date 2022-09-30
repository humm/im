package com.hoomoomoo.im.dto;

import lombok.Data;

import static com.hoomoomoo.im.consts.BaseConst.STR_0;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.dto
 * @date 2021/05/22
 */
@Data
public class FunctionDto extends BaseDto{

    private String functionCode;

    private String functionName;

    private String submitTimes;

    private String firstTime;

    private String lastTime;

    public FunctionDto() {
    }

    public FunctionDto(String functionCode, String functionName) {
        this.functionCode = functionCode;
        this.functionName = functionName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FunctionDto that = (FunctionDto) o;

        return functionCode.equals(that.functionCode);
    }

    @Override
    public int hashCode() {
        return functionCode.hashCode();
    }
}
