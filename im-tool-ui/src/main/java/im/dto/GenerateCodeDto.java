package im.dto;

import lombok.Data;

import java.util.*;

import static im.consts.BaseConst.SYMBOL_EMPTY;

/**
 * @author humm23693
 * @description TODO
 * @package im.dto
 * @date 2021/10/15
 */
@Data
public class GenerateCodeDto {

    private String javaPath;
    private String sqlPath;
    private String vuePath;
    private String routePath;
    private String menuCode;
    private String menuName;
    private String dtoCode;
    private String author;
    private String table;
    private String asyTable;
    private Map<String, ColumnInfoDto> columnMap;
    private Map<String, ColumnInfoDto> asyColumnMap;
    private Map<String, String> primaryKeyMap;
    private List<String[]> menuList;
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
    private String column;
    private String pageType;
    private String functionCode;
    private String functionName;

    public GenerateCodeDto() {
        this.columnMap = new LinkedHashMap<>(16);
        this.asyColumnMap = new LinkedHashMap<>(16);
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
        this.menuList = new ArrayList<>();
        this.functionCode = SYMBOL_EMPTY;
        this.functionName = SYMBOL_EMPTY;
    }

}
