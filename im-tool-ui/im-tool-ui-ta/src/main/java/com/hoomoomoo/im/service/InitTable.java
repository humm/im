package com.hoomoomoo.im.service;

import com.alibaba.fastjson.JSONObject;
import com.hoomoomoo.im.dto.ColumnInfoDto;
import com.hoomoomoo.im.dto.GenerateCodeDto;
import com.hoomoomoo.im.utils.CommonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.service.generate
 * @date 2021/10/15
 */
public class InitTable {

    public static void init(GenerateCodeDto generateCodeDto) throws Exception {
        String table = generateCodeDto.getTable();
        int tableStart = table.indexOf(SYMBOL_BRACKETS_LEFT);
        String[] tableName = table.substring(0, tableStart).split(SYMBOL_S_SLASH);
        generateCodeDto.setTableName(tableName[tableName.length - 1]);

        Map<String, ColumnInfoDto> tableColumn = getColumn(generateCodeDto, table, true);
        generateCodeDto.setColumnMap(tableColumn);

        String asyTable = generateCodeDto.getAsyTable();
        if (StringUtils.isNotEmpty(asyTable)) {
            Map<String, ColumnInfoDto> asyTableColumn = getColumn(generateCodeDto, asyTable, false);
            generateCodeDto.setAsyColumnMap(asyTableColumn);
        }

        List<ColumnInfoDto> column = generateCodeDto.getColumn();
        if (CollectionUtils.isNotEmpty(column)) {
            for (ColumnInfoDto item : column) {
                String columnCode = item.getColumn();
                ColumnInfoDto tableColumnConfig = tableColumn.get(columnCode);
                if (tableColumnConfig != null) {
                    tableColumn.get(columnCode).setColumnName(item.getColumnName());
                    tableColumn.get(columnCode).setColumnDict(item.getColumnDict());
                    tableColumn.get(columnCode).setColumnMulti(item.getColumnMulti());
                    tableColumn.get(columnCode).setColumnRequired(item.getColumnRequired());
                    tableColumn.get(columnCode).setColumnDate(item.getColumnDate());
                    tableColumn.get(columnCode).setColumnWidth(item.getColumnWidth());
                    if (StringUtils.isNotBlank(item.getColumnOrder())) {
                        tableColumn.get(columnCode).setColumnOrder(item.getColumnOrder());
                    }
                    if (STR_1.equals(item.getColumnDict())) {
                        tableColumn.get(columnCode).setColumnType(KEY_COLUMN_TYPE_DATE);
                    }
                    tableColumn.get(columnCode).setColumnPrecision(item.getColumnPrecision());
                }
            }
        }

        generateCodeDto.setColumnMap(order(tableColumn));

        String[] menuCode = generateCodeDto.getMenuCode().split(SYMBOL_POINT_SLASH);
        String[] menuName = generateCodeDto.getMenuName().split(SYMBOL_POINT_SLASH);
        for (int i = 0; i < menuCode.length; i++) {
            String[] item = new String[2];
            item[0] = menuCode[i];
            item[1] = menuName[i];
            generateCodeDto.getMenuList().add(item);
        }

        generateCodeDto.setFunctionCode(menuCode[2]);
        generateCodeDto.setFunctionName(menuName[2]);
    }

    private static Map<String, ColumnInfoDto> getColumn(GenerateCodeDto generateCodeDto, String table,
                                                              boolean primaryKey) throws Exception {
        Map<String, ColumnInfoDto> columnMap = new LinkedHashMap<>(16);
        int tableStart = table.indexOf(SYMBOL_BRACKETS_LEFT);
        int tableEnd = table.lastIndexOf(SYMBOL_BRACKETS_RIGHT);
        String[] columnList = table.substring(tableStart + 1, tableEnd).split("not null,");
        if (columnList == null || table.length() == 0) {
            throw new Exception("未获取到数据表字段信息");
        }
        int orderNo = 0;
        for (int i = 0; i < columnList.length; i++) {
            String item = columnList[i].trim();
            if (i == columnList.length - 1 && item.contains("primary key")) {
                if (primaryKey) {
                    int keyStart = item.indexOf(SYMBOL_BRACKETS_LEFT);
                    int keyEnd = item.lastIndexOf(SYMBOL_BRACKETS_RIGHT);
                    String key = item.substring(keyStart + 1, keyEnd);
                    generateCodeDto.setPrimaryKey(key.replaceAll(SYMBOL_S_SLASH, SYMBOL_EMPTY));
                    String[] keyArr = key.replaceAll(SYMBOL_S_SLASH, SYMBOL_EMPTY).split(SYMBOL_COMMA);
                    if (keyArr != null) {
                        for (String itemKey : keyArr) {
                            generateCodeDto.getPrimaryKeyMap().put(itemKey, itemKey);
                        }
                    }
                }
                continue;
            }

            String[] itemInfo = item.split(SYMBOL_S_SLASH);
            String columnUnderline = itemInfo[0];
            String columnType = itemInfo[1];
            if (StringUtils.isEmpty(columnUnderline) && StringUtils.isEmpty(columnType)) {
                continue;
            }
            String precision = SYMBOL_EMPTY;
            if (columnType.indexOf(SYMBOL_COMMA) != -1) {
                precision = columnType.substring(columnType.indexOf(SYMBOL_COMMA) + 1, columnType.indexOf(SYMBOL_BRACKETS_RIGHT));
            }
            String column = CommonUtils.lineToHump(columnUnderline);
            if (KEY_TA_CODE.equals(column)) {
                continue;
            }
            ColumnInfoDto columnInfo = new ColumnInfoDto();
            columnInfo.setColumn(column);
            columnInfo.setColumnUnderline(columnUnderline);
            String columnTypeLast = columnType.split("\\(")[0].toLowerCase();
            if (KEY_COLUMN_TYPE_INTEGER.equals(columnTypeLast) && column.endsWith("Date")) {
                columnTypeLast = KEY_COLUMN_TYPE_DATE;
                columnInfo.setColumnDate(STR_1);
            }
            orderNo += 10;
            int columnWidth = 150;
            if (KEY_PRD_CODE.equals(column)) {
                columnWidth = 200;
            }
            columnInfo.setColumnWidth(String.valueOf(columnWidth));
            columnInfo.setColumnType(columnTypeLast);
            columnInfo.setColumnPrecision(precision);
            columnInfo.setColumnName(SYMBOL_EMPTY);
            columnInfo.setColumnDict(SYMBOL_EMPTY);
            columnInfo.setColumnOrder(String.valueOf(orderNo));
            columnMap.put(column, columnInfo);
        }

        ColumnInfoDto transCode = new ColumnInfoDto();
        transCode.setColumn(KEY_TRANS_CODE_AND_SUB_TRANS_CODE_HUMP);
        transCode.setColumnUnderline(KEY_TRANS_CODE_AND_SUB_TRANS_CODE);
        transCode.setColumnType(KEY_COLUMN_TYPE_VARCHAR2);
        transCode.setColumnPrecision(SYMBOL_EMPTY);
        transCode.setColumnOrder(String.valueOf(orderNo + 10));
        transCode.setColumnWidth(String.valueOf(150));
        columnMap.put(KEY_TRANS_CODE_AND_SUB_TRANS_CODE_HUMP, transCode);

        return columnMap;
    }

    private static LinkedHashMap<String, ColumnInfoDto> order(Map<String, ColumnInfoDto> request) {
        LinkedHashMap<String, ColumnInfoDto> result = new LinkedHashMap<>(request.size());
        List<ColumnInfoDto> list = new ArrayList<>(request.values());
        Collections.sort(list);
        for (ColumnInfoDto item : list) {
            result.put(item.getColumn(), item);
        }
        return result;
    }
}
