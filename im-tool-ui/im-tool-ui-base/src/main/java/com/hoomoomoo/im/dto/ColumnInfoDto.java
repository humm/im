package com.hoomoomoo.im.dto;

import lombok.Data;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.dto
 * @date 2021/11/18
 */
@Data
public class ColumnInfoDto extends BaseDto implements Comparable<ColumnInfoDto>{

    private String columnCode;
    private String columnUnderline;
    private String columnType;
    private String columnName;
    private String columnDict;
    private String columnMulti;
    private String columnDate;
    private String columnRequired;
    private String columnPrecision;
    private String columnOrder;
    private String columnDefault;
    private String columnWidth;
    private String columnQuery;
    private String columnUpdate;
    private String columnQueryOrder;
    private String columnQueryOrderType;
    private String columnBatchUpdate;
    private String columnQueryStat;
    private String columnMultiSingle;

    @Override
    public int compareTo(ColumnInfoDto o) {
        return Integer.valueOf(this.getColumnOrder()) - Integer.valueOf(o.getColumnOrder());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ColumnInfoDto that = (ColumnInfoDto) o;
        return columnCode.equals(that.columnCode);
    }

    @Override
    public int hashCode() {
        return columnCode.hashCode();
    }
}
