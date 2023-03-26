package com.hoomoomoo.im.service;

import com.hoomoomoo.im.dto.ColumnInfoDto;
import com.hoomoomoo.im.dto.GenerateCodeDto;
import com.hoomoomoo.im.utils.CommonUtils;
import org.apache.commons.collections.CollectionUtils;
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
        if (StringUtils.isEmpty(table)) {
            throw new Exception(String.format(MSG_SET, "正式表结构 (oracle)"));
        }
        int tableStart = table.indexOf(SYMBOL_BRACKETS_LEFT);
        if (tableStart == -1) {
            throw new Exception(String.format(MSG_SET, "正式表结构 (oracle)"));
        }
        String[] tableName = table.substring(0, tableStart).split(SYMBOL_S_SLASH);
        generateCodeDto.setTableName(tableName[tableName.length - 1]);

        Map<String, ColumnInfoDto> tableColumn = getColumn(generateCodeDto, table, true);
        generateCodeDto.setColumnMap(tableColumn);

        String asyTable = generateCodeDto.getAsyTable();
        if (StringUtils.isNotEmpty(asyTable)) {
            Map<String, ColumnInfoDto> asyTableColumn = getColumn(generateCodeDto, asyTable, false);
            generateCodeDto.setAsyColumnMap(asyTableColumn);

            Iterator<String> iterator = asyTableColumn.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (key.startsWith(KEY_ORI)) {
                    String sourceKey = CommonUtils.initialLower(key.substring(3));
                    if (tableColumn.get(sourceKey) != null) {
                        generateCodeDto.getAsyKeyMap().put(key, sourceKey);
                    } else if (key.equals(KEY_ORI_BEG_DATE)) {
                        generateCodeDto.getAsyKeyMap().put(key, KEY_BEGIN_DATE);
                    }
                }
            }
        }

        List<ColumnInfoDto> column = generateCodeDto.getColumn();
        if (CollectionUtils.isNotEmpty(column)) {
            for (ColumnInfoDto item : column) {
                String columnCode = item.getColumnCode();
                ColumnInfoDto tableColumnConfig = tableColumn.get(columnCode);
                if (tableColumnConfig != null) {
                    String columnName = item.getColumnName();
                    tableColumn.get(columnCode).setColumnName(columnName);
                    tableColumn.get(columnCode).setColumnDict(item.getColumnDict());
                    tableColumn.get(columnCode).setColumnMulti(item.getColumnMulti());
                    tableColumn.get(columnCode).setColumnRequired(item.getColumnRequired());
                    tableColumn.get(columnCode).setColumnDate(item.getColumnDate());
                    tableColumn.get(columnCode).setColumnWidth(item.getColumnWidth());
                    tableColumn.get(columnCode).setColumnQuery(item.getColumnQuery());
                    tableColumn.get(columnCode).setColumnUpdate(item.getColumnUpdate());
                    tableColumn.get(columnCode).setColumnQueryOrder(item.getColumnQueryOrder());
                    tableColumn.get(columnCode).setColumnQueryOrderType(item.getColumnQueryOrderType());
                    tableColumn.get(columnCode).setColumnBatchUpdate(item.getColumnBatchUpdate());
                    tableColumn.get(columnCode).setColumnDefault(item.getColumnDefault());
                    tableColumn.get(columnCode).setColumnQueryStat(item.getColumnQueryStat());
                    tableColumn.get(columnCode).setColumnMultiSingle(item.getColumnMultiSingle());
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

        generateCodeDto.setColumnMap(orderColumn(tableColumn));
        generateCodeDto.setColumnQueryOrder(orderColumnQuery(tableColumn));

        String menuCode1 = generateCodeDto.getMenuCode1();
        String menuCode2 = generateCodeDto.getMenuCode2();
        String menuCode3 = generateCodeDto.getMenuCode3();
        String menuName1 = generateCodeDto.getMenuName1();
        String menuName2 = generateCodeDto.getMenuName2();
        String menuName3 = generateCodeDto.getMenuName3();
        String[] item1 = new String[2];
        item1[0] = menuCode1;
        item1[1] = menuName1;
        generateCodeDto.getMenuList().add(item1);
        String[] item2 = new String[2];
        item2[0] = menuCode2;
        item2[1] = menuName2;
        generateCodeDto.getMenuList().add(item2);
        String[] item3 = new String[2];
        item3[0] = menuCode3;
        item3[1] = menuName3;
        generateCodeDto.getMenuList().add(item3);
        generateCodeDto.setFunctionCode(menuCode3);
        generateCodeDto.setFunctionName(menuName3);
    }

    private static Map<String, ColumnInfoDto> getColumn(GenerateCodeDto generateCodeDto, String table,
                                                              boolean primaryKey) throws Exception {
        Map<String, ColumnInfoDto> columnMap = new LinkedHashMap<>(16);
        int tableStart = table.indexOf(SYMBOL_BRACKETS_LEFT);
        int tableEnd = table.lastIndexOf(SYMBOL_BRACKETS_RIGHT);
        String[] columnList = table.substring(tableStart + 1, tableEnd).split(KEY_NOT_NULL);
        if (columnList == null || table.length() == 0) {
            throw new Exception("未获取到数据表字段信息");
        }
        Map<String, String> fieldTranslateMap = generateCodeDto.getFieldTranslateMap();
        int orderNo = 0;
        for (int i = 0; i < columnList.length; i++) {
            String item = columnList[i].trim();
            if (i == columnList.length - 1 && item.contains(KEY_PRIMARY_KEY)) {
                if (primaryKey) {
                    int keyStart = item.indexOf(SYMBOL_BRACKETS_LEFT);
                    int keyEnd = item.lastIndexOf(SYMBOL_BRACKETS_RIGHT);
                    String key = item.substring(keyStart + 1, keyEnd);
                    generateCodeDto.setPrimaryKey(key.replaceAll(SYMBOL_S_SLASH, SYMBOL_EMPTY));
                    String[] keyArr = key.replaceAll(SYMBOL_S_SLASH, SYMBOL_EMPTY).split(SYMBOL_COMMA);
                    if (keyArr != null) {
                        for (String itemKey : keyArr) {
                            itemKey = itemKey.toLowerCase();
                            if (GenerateCommon.skipColumn(columnMap.get(CommonUtils.lineToHump(itemKey)), false)) {
                                continue;
                            }
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
            String column = CommonUtils.lineToHump(columnUnderline.toLowerCase());
            if (KEY_TA_CODE.equals(column)) {
                continue;
            }
            ColumnInfoDto columnInfo = new ColumnInfoDto();
            columnInfo.setColumnCode(column);
            columnInfo.setColumnUnderline(columnUnderline);
            String columnTypeLast = columnType.split("\\(")[0].toLowerCase();
            if (KEY_COLUMN_TYPE_INTEGER.equals(columnTypeLast) && column.endsWith(KEY_DATE)) {
                columnTypeLast = KEY_COLUMN_TYPE_DATE;
                columnInfo.setColumnDate(STR_1);
            }
            orderNo += 10;
            int columnWidth = 150;
            if (column.contains(KEY_PRD_CODE)) {
                columnWidth = 200;
            }
            columnInfo.setColumnWidth(String.valueOf(columnWidth));
            columnInfo.setColumnType(columnTypeLast);
            columnInfo.setColumnPrecision(precision);
            if (fieldTranslateMap.containsKey(columnUnderline)) {
                columnInfo.setColumnName(fieldTranslateMap.get(columnUnderline));
            } else {
                columnInfo.setColumnName(SYMBOL_EMPTY);
            }
            columnInfo.setColumnDict(SYMBOL_EMPTY);
            columnInfo.setColumnOrder(String.valueOf(orderNo));
            columnMap.put(column, columnInfo);
        }

        ColumnInfoDto transCode = new ColumnInfoDto();
        transCode.setColumnCode(KEY_TRANS_CODE_AND_SUB_TRANS_CODE);
        transCode.setColumnUnderline(KEY_TRANS_CODE_AND_SUB_TRANS_CODE_UNDERLINE);
        transCode.setColumnType(KEY_COLUMN_TYPE_VARCHAR2);
        transCode.setColumnPrecision(SYMBOL_EMPTY);
        transCode.setColumnOrder(String.valueOf(orderNo + 10));
        transCode.setColumnWidth(String.valueOf(150));
        columnMap.put(KEY_TRANS_CODE_AND_SUB_TRANS_CODE, transCode);

        return columnMap;
    }

    private static LinkedHashMap<String, ColumnInfoDto> orderColumn(Map<String, ColumnInfoDto> request) {
        LinkedHashMap<String, ColumnInfoDto> result = new LinkedHashMap<>(request.size());
        List<ColumnInfoDto> list = new ArrayList<>(request.values());
        Collections.sort(list);
        for (ColumnInfoDto item : list) {
            result.put(item.getColumnCode(), item);
        }
        return result;
    }

    private static String orderColumnQuery(Map<String, ColumnInfoDto> request) {
        StringBuilder content = new StringBuilder();
        List<ColumnInfoDto> list = new ArrayList<>(request.values());
        Map<String, String> column = new TreeMap<>((o1, o2) -> Integer.valueOf(o1).compareTo(Integer.valueOf(o2)));
        for (ColumnInfoDto item : list) {
            String columnUnderline = item.getColumnUnderline();
            String columnQueryOrder = item.getColumnQueryOrder();
            if (StringUtils.isNotBlank(columnQueryOrder)) {
                String order = columnUnderline;
                if (STR_1.equals(item.getColumnQueryOrderType())) {
                    order += SYMBOL_SPACE + KEY_ORDER_TYPE_DESC;
                }
                column.put(columnQueryOrder, order);
            }
        }
        List<String> columnOrder = new ArrayList<>(column.values());
        for (String item : columnOrder) {
            content.append(item).append(SYMBOL_COMMA).append(SYMBOL_SPACE);
        }
        if (StringUtils.isEmpty(content)) {
            return content.toString();
        }
        return SYMBOL_QUOTES + content.substring(0, content.lastIndexOf(SYMBOL_COMMA)) + SYMBOL_QUOTES;
    }
}
