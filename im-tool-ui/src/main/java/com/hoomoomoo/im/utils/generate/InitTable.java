package com.hoomoomoo.im.utils.generate;

import com.hoomoomoo.im.dto.GenerateCodeDto;
import com.hoomoomoo.im.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.utils.generate
 * @date 2021/10/15
 */
public class InitTable {

    public static void init(GenerateCodeDto generateCodeDto) throws Exception {
        String table = generateCodeDto.getTable();
        int tableStart = table.indexOf(SYMBOL_BRACKETS_LEFT);
        String[] tableName = table.substring(0, tableStart).split(SYMBOL_S_SLASH);
        generateCodeDto.setTableName(tableName[tableName.length - 1]);

        Map<String, Map<String, String>> tableColumn = getColumn(generateCodeDto, table, true);
        generateCodeDto.setColumnMap(tableColumn);

        String asyTable = generateCodeDto.getAsyTable();
        Map<String, Map<String, String>> asyTableColumn = getColumn(generateCodeDto, asyTable, false);
        generateCodeDto.setAsyColumnMap(asyTableColumn);

        Map dictMap = new HashMap(16);
        String dictColumn = generateCodeDto.getDictColumn();
        if (StringUtils.isNotEmpty(dictColumn)) {
            String[] dictConfig = dictColumn.split(SYMBOL_SEMICOLON);
            if (dictConfig != null) {
                for (String item : dictConfig) {
                    String[] dictItem = item.split(SYMBOL_COLON);
                    if (dictItem != null && dictItem.length == 2) {
                        dictMap.put(dictItem[0], dictItem[1]);
                    }
                }
            }
        }
        generateCodeDto.setDictMap(dictMap);
    }

    private static Map<String, Map<String, String>> getColumn(GenerateCodeDto generateCodeDto, String table,
                                                              boolean primaryKey) throws Exception {
        Map<String, Map<String, String>> columnMap = new LinkedHashMap<>(16);
        int tableStart = table.indexOf(SYMBOL_BRACKETS_LEFT);
        int tableEnd = table.lastIndexOf(SYMBOL_BRACKETS_RIGHT);
        String[] columnList = table.substring(tableStart + 1, tableEnd).split("not null,");
        if (columnList == null || table.length() == 0) {
            throw new Exception("未获取到数据表字段信息");
        }

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
            Map columnInfo = new LinkedHashMap(16);
            columnInfo.put(KEY_COLUMN, column);
            columnInfo.put(KEY_COLUMN_UNDERLINE, columnUnderline);
            columnInfo.put(KEY_COLUMN_TYPE, columnType.split("\\(")[0].toLowerCase());
            columnInfo.put(KEY_PRECISION, precision);
            columnMap.put(column, columnInfo);
        }

        Map transCode = new LinkedHashMap(16);
        transCode.put(KEY_COLUMN, KEY_TRANS_CODE_AND_SUB_TRANS_CODE_HUMP);
        transCode.put(KEY_COLUMN_UNDERLINE, KEY_TRANS_CODE_AND_SUB_TRANS_CODE);
        transCode.put(KEY_COLUMN_TYPE, KEY_VARCHAR2);
        transCode.put(KEY_PRECISION, SYMBOL_EMPTY);
        columnMap.put(KEY_TRANS_CODE_AND_SUB_TRANS_CODE_HUMP, transCode);

        return columnMap;
    }

}
