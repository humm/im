package im.model.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author hoomoomoo
 * @description 实体dto
 * @package com.hoomoomoo.im.model.common
 * @date 2019/08/08
 */

@Data
public class BaseModel {

    @ApiModelProperty(value="创建人", required = false)
    private String createUser;

    @ApiModelProperty(value="创建时间", required = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createDate;

    @ApiModelProperty(value="修改人", required = false)
    private String modifyUser;

    @ApiModelProperty(value="修改时间", required = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifyDate;

    @ApiModelProperty(value="数据来源", required = false)
    private String dataType;
}
