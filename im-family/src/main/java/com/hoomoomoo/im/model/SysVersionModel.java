package com.hoomoomoo.im.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author hoomoomoo
 * @description 修订信息实体类
 * @package com.hoomoomoo.im.model
 * @date 2019/11/23
 */

@Data
public class SysVersionModel{

    @ApiModelProperty(value="修订ID", required = false)
    private String versionId;

    @ApiModelProperty(value="修订内容", required = false)
    private String versionContent;

    @ApiModelProperty(value="修订日期", required = false)
    @JsonFormat(pattern = "yyyy年MM月dd日", timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy年MM月dd日")
    private Date versionDate;

    @ApiModelProperty(value="修订排序", required = false)
    private String versionOrder;

    @ApiModelProperty(value="修订类型", required = false)
    private String versionType;
}
