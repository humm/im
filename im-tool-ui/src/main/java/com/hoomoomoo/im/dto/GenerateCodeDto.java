package com.hoomoomoo.im.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.hoomoomoo.im.consts.BaseConst.SYMBOL_EMPTY;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.dto
 * @date 2021/10/15
 */
@Data
public class GenerateCodeDto {

    private String javaPath;
    private String sqlPath;
    private String packageCode;
    private String classCode;
    private String dtoName;
    private String author;
    private String describe;
    private String table;
    private String asyTable;
    private Map<String, Map<String, String>> columnMap;
    private Map<String, Map<String, String>> asyColumnMap;
    private Map<String, String> dictMap;
    private Map<String, String> primaryKeyMap;
    private String dtoPackageName;
    private String dtoNameDto;
    private String interfacePackageName;
    private String interfaceName;
    private String servicePackageName;
    private String serviceName;
    private String auditServicePackageName;
    private String auditServiceName;
    private String exportPackageName;
    private String exportName;
    private String importPackageName;
    private String importName;
    private String controllerPackageName;
    private String controllerName;
    private String tableName;
    private String primaryKey;
    private String dbType;
    private String dictColumn;
    private String pageType;
    private String secondMenuName;

    public GenerateCodeDto() {
        this.javaPath = SYMBOL_EMPTY;
        this.sqlPath = SYMBOL_EMPTY;
        this.packageCode = SYMBOL_EMPTY;
        this.classCode = SYMBOL_EMPTY;
        this.dtoName = SYMBOL_EMPTY;
        this.author = SYMBOL_EMPTY;
        this.describe = SYMBOL_EMPTY;
        this.table = SYMBOL_EMPTY;
        this.asyTable = SYMBOL_EMPTY;
        this.columnMap = new LinkedHashMap<>(16);
        this.asyColumnMap = new LinkedHashMap<>(16);
        this.dictMap = new HashMap<>(16);
        this.primaryKeyMap = new HashMap<>(16);
        this.dtoPackageName = SYMBOL_EMPTY;
        this.dtoNameDto = SYMBOL_EMPTY;
        this.interfacePackageName = SYMBOL_EMPTY;
        this.interfaceName = SYMBOL_EMPTY;
        this.servicePackageName = SYMBOL_EMPTY;
        this.serviceName = SYMBOL_EMPTY;
        this.auditServicePackageName = SYMBOL_EMPTY;
        this.auditServiceName = SYMBOL_EMPTY;
        this.exportPackageName = SYMBOL_EMPTY;
        this.exportName = SYMBOL_EMPTY;
        this.importPackageName = SYMBOL_EMPTY;
        this.importName = SYMBOL_EMPTY;
        this.tableName = SYMBOL_EMPTY;
        this.primaryKey = SYMBOL_EMPTY;
        this.dbType = SYMBOL_EMPTY;
        this.dictColumn = SYMBOL_EMPTY;
        this.pageType = SYMBOL_EMPTY;
        this.secondMenuName = SYMBOL_EMPTY;
    }

}
