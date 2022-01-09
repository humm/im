package com.hoomoomoo.im.dto;

import lombok.Data;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.dto
 * @date 2021/11/18
 */
@Data
public class ColumnInfoDto implements Comparable<ColumnInfoDto>{

    private String column;
    private String columnUnderline;
    private String columnType;
    private String columnName;
    private String columnDict;
    private String columnMulti;
    private String columnRequired;
    private String columnPrecision;
    private String columnOrder;

    @Override
    public int compareTo(ColumnInfoDto o) {
        return Integer.valueOf(this.getColumnOrder()) - Integer.valueOf(o.getColumnOrder());
    }
}
