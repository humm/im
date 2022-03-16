package im.model.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * @author hoomoomoo
 * @description 查询dto
 * @package com.hoomoomoo.im.model.common
 * @date 2019/08/10
 */

@Data
public class QueryBaseModel implements Serializable {

    @ApiModelProperty(value = "当前页码", required = true)
    private Integer page;

    @ApiModelProperty(value = "每页显示记录数", required = true)
    private Integer limit;

    @ApiModelProperty(value = "排序字段", required = false)
    private String sort;

    @ApiModelProperty(value = "排序方式", required = false)
    private String order;

    @JsonIgnore
    @ApiModelProperty(value = "是否管理员", required = false)
    private Boolean isAdminData;

    @JsonIgnore
    @ApiModelProperty(value = "session用户ID", required = false)
    private String userKey;

    public String getSort() {
        if (this.sort == null) {
            return null;
        }
        StringBuffer convertSort = new StringBuffer();
        for (int i = 0; i < this.sort.length(); i++) {
            char single = this.sort.charAt(i);
            if (i != 0 && Character.isUpperCase(single)) {
                convertSort.append("_").append(single);
            } else {
                convertSort.append(single);
            }
        }
        return convertSort.toString().toLowerCase();
    }
}
