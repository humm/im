package com.hoomoomoo.im.dto;

import lombok.Data;

import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.STR_BLANK;

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
    private String vuePath;
    private String routePath;
    //private String menuCode;
    private String menuCode1;
    private String menuCode2;
    private String menuCode3;
    //private String menuName;
    private String menuName1;
    private String menuName2;
    private String menuName3;
    private String dtoCode;
    private String author;
    private String table;
    private String asyTable;
    private Map<String, ColumnInfoDto> columnMap = new LinkedHashMap<>(16);
    private Map<String, ColumnInfoDto> asyColumnMap = new LinkedHashMap<>(16);
    private LinkedHashMap<String, String> primaryKeyMap = new LinkedHashMap<>(16);
    private LinkedHashMap<String, String> asyKeyMap = new LinkedHashMap<>(16);
    private List<String[]> menuList = new ArrayList<>();
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
    private List<ColumnInfoDto> column;
    private String pageType;
    private String functionCode;
    private String functionName;
    private String menuOrder;
    private String columnQueryOrder;
    private String menuType;
    private Map<String, String> fieldTranslateMap = new LinkedHashMap<>(16);

    public GenerateCodeDto() {
        this.dtoPackageName = STR_BLANK;
        this.dtoNameDto = STR_BLANK;
        this.interfacePackageName = STR_BLANK;
        this.interfaceName = STR_BLANK;
        this.servicePackageName = STR_BLANK;
        this.serviceName = STR_BLANK;
        this.auditServicePackageName = STR_BLANK;
        this.auditServiceName = STR_BLANK;
        this.exportPackageName = STR_BLANK;
        this.exportName = STR_BLANK;
        this.importPackageName = STR_BLANK;
        this.importName = STR_BLANK;
        this.tableName = STR_BLANK;
        this.primaryKey = STR_BLANK;
        this.functionCode = STR_BLANK;
        this.functionName = STR_BLANK;
        this.menuOrder = STR_BLANK;
        this.menuType = STR_BLANK;
    }

}
