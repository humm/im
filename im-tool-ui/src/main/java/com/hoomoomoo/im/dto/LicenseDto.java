package com.hoomoomoo.im.dto;

import lombok.Data;

import java.util.List;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.dto
 * @date 2021/05/22
 */
@Data
public class LicenseDto extends BaseDto {

    private String effectiveDate;

    private List<FunctionDto> function;
}
