package im.dto;

import lombok.Data;

/**
 * @author humm23693
 * @description TODO
 * @package im.dto
 * @date 2021/05/22
 */
@Data
public class FunctionDto {

    private String functionCode;

    private String functionName;

    private Integer useTimes;

    public FunctionDto() {
    }

    public FunctionDto(String functionCode, String functionName) {
        this.functionCode = functionCode;
        this.functionName = functionName;
    }
}
