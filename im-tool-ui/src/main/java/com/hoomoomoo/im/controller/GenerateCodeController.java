package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.dto.GenerateCodeDto;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.generate.*;
import javafx.fxml.Initializable;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.BaseConst.SYMBOL_COMMA;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/10/15
 */
public class GenerateCodeController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private static final String javaPath = "E:\\workspace\\ta6\\lcpt-server\\ta-web\\ta-web-manager-fund\\ta-web-manager-fund-core\\src\\main\\java";
    private static final String sqlPath = "E:\\workspace\\ta6\\lcpt-server\\ta-web\\ta-web-manager-fund\\ta-web-manager-fund-core\\src\\main\\java";
    private static final String packageCode = "fundsysinfo.funddiscount";
    private static final String secondMenuName = "优惠信息设置";
    private static final String classCode = "FundPropFavourDemoSet";
    private static final String dtoName = "FundPropFavourDemo";
    private static final String author = "humm23693";
    private static final String describe = "模板代码";
    private static final String dbType = "pub";
    private static final String dictColumn = "busin_code:F_C20110;amt_way:F_C20111;";
    private static final String pageType = "1";
    private static final String table = "create table tbfundprdpropfavour(\n" +
            "    ta_code                   VARCHAR2(18)         default ' ' not null,\n" +
            "    invest_direction          VARCHAR2(1)          default ' ' not null,\n" +
            "    busin_code                VARCHAR2(6)          default ' ' not null,\n" +
            "    id_type                   VARCHAR2(18)         default ' ' not null,\n" +
            "    prd_code                VARCHAR2(6)          default ' ' not null,\n" +
            "    branch_no                VARCHAR2(6)          default ' ' not null,\n" +
            "    seller_code               VARCHAR2(9)          default ' ' not null,\n" +
            "    amt_way                   VARCHAR2(2)          default ' ' not null,\n" +
            "    trans_way                 VARCHAR2(1)          default ' ' not null,\n" +
            "    ori_source_flag           VARCHAR2(1)          default ' ' not null,\n" +
            "    source_flag               VARCHAR2(1)          default ' ' not null,\n" +
            "    targ_invest_direction     VARCHAR2(1)          default ' ' not null,\n" +
            "    min_amt                   NUMBER(18,2)         default 0.0 not null,\n" +
            "    max_amt                   NUMBER(18,2)         default 0.0 not null,\n" +
            "    min_hold                  NUMBER(18,3)         default 0.0 not null,\n" +
            "    max_hold                  NUMBER(18,3)         default 0.0 not null,\n" +
            "    begin_date                INTEGER              default 0 not null,\n" +
            "    end_date                  INTEGER              default 0 not null,\n" +
            "    fare_type                 VARCHAR2(1)          default ' ' not null,\n" +
            "    favor_ratio               NUMBER(18,4)         default 0.0 not null,\n" +
            "    favor_type                VARCHAR2(1)          default ' ' not null,\n" +
            "    op_times                  INTEGER              default 0 not null,\n" +
            "    constraint pk_fundprdpropfavour primary key (invest_direction, busin_code, seller_code, amt_way, trans_way, ori_source_flag, source_flag, targ_invest_direction, begin_date, min_amt, min_hold, fare_type, favor_type)\n" +
            ")  ;";
    private static final String asyTable = "create table tbfundprdpropfavourasy(\n" +
            "    ta_code                   VARCHAR2(18)         default ' ' not null,\n" +
            "    invest_direction          VARCHAR2(1)          default ' ' not null,\n" +
            "    busin_code                VARCHAR2(6)          default ' ' not null,\n" +
            "    id_type                   VARCHAR2(18)         default ' ' not null,\n" +
            "    prd_code                VARCHAR2(6)          default ' ' not null,\n" +
            "    branch_no                VARCHAR2(6)          default ' ' not null,\n" +
            "    seller_code               VARCHAR2(9)          default ' ' not null,\n" +
            "    amt_way                   VARCHAR2(2)          default ' ' not null,\n" +
            "    trans_way                 VARCHAR2(1)          default ' ' not null,\n" +
            "    ori_source_flag           VARCHAR2(1)          default ' ' not null,\n" +
            "    source_flag               VARCHAR2(1)          default ' ' not null,\n" +
            "    targ_invest_direction     VARCHAR2(1)          default ' ' not null,\n" +
            "    min_amt                   NUMBER(18,2)         default 0.0 not null,\n" +
            "    max_amt                   NUMBER(18,2)         default 0.0 not null,\n" +
            "    min_hold                  NUMBER(18,3)         default 0.0 not null,\n" +
            "    max_hold                  NUMBER(18,3)         default 0.0 not null,\n" +
            "    begin_date                INTEGER              default 0 not null,\n" +
            "    end_date                  INTEGER              default 0 not null,\n" +
            "    fare_type                 VARCHAR2(1)          default ' ' not null,\n" +
            "    favor_ratio               NUMBER(18,4)         default 0.0 not null,\n" +
            "    favor_type                VARCHAR2(1)          default ' ' not null,\n" +
            "    op_times                  INTEGER              default 0 not null,\n" +
            "    entry_serial_no           VARCHAR2(32)         default ' ' not null,\n" +
            "    entry_order_no            INTEGER              default 0 not null,\n" +
            "    oper_no                   VARCHAR2(32)         default ' ' not null,\n" +
            "    serial_status             VARCHAR2(1)          default ' ' not null,\n" +
            "    op_dir                    VARCHAR2(1)          default ' ' not null,\n" +
            "    constraint pk_tbfundprdpropfavourasy primary key (entry_serial_no, entry_order_no)\n" +
            ")  ;";

    public static void main(String[] args) {
        GenerateCodeDto generateCodeDto = new GenerateCodeDto();
        generateCodeDto.setJavaPath(javaPath);
        generateCodeDto.setSqlPath(sqlPath);
        generateCodeDto.setPackageCode(packageCode);
        generateCodeDto.setClassCode(classCode);
        generateCodeDto.setDtoName(dtoName);
        generateCodeDto.setAuthor(author);
        generateCodeDto.setDescribe(describe);
        generateCodeDto.setTable(table);
        generateCodeDto.setAsyTable(asyTable);
        generateCodeDto.setDbType(dbType);
        generateCodeDto.setDictColumn(dictColumn);
        generateCodeDto.setPageType(pageType);
        generateCodeDto.setSecondMenuName(secondMenuName);
        // 控制判断 默认值处理
        try {
            List<String> fileLog = new ArrayList<>();

            GenerateAuditService.getPackageName(generateCodeDto);
            GenerateImport.getPackageName(generateCodeDto);

            InitTable.init(generateCodeDto);

            String dtoFile = GenerateDao.init(generateCodeDto);
            if (StringUtils.isNotEmpty(dtoFile)) {
                fileLog.add(dtoFile);
            }

            String interfaceFile = GenerateInterface.init(generateCodeDto);
            if (StringUtils.isNotEmpty(interfaceFile)) {
                fileLog.add(interfaceFile);
            }

            String serviceFile = GenerateService.init(generateCodeDto);
            if (StringUtils.isNotEmpty(serviceFile)) {
                fileLog.add(serviceFile);
            }

            String auditServiceFile = GenerateAuditService.init(generateCodeDto);
            if (StringUtils.isNotEmpty(auditServiceFile)) {
                fileLog.add(auditServiceFile);
            }

            String controllerFile = GenerateController.init(generateCodeDto);
            if (StringUtils.isNotEmpty(controllerFile)) {
                fileLog.add(controllerFile);
            }

            String importFile = GenerateImport.init(generateCodeDto);
            if (StringUtils.isNotEmpty(importFile)) {
                fileLog.add(importFile);
            }

            String exportFile = GenerateExport.init(generateCodeDto);
            if (StringUtils.isNotEmpty(exportFile)) {
                fileLog.add(exportFile);
            }

            String sqlFile = GenerateSql.init(generateCodeDto);
            if (StringUtils.isNotEmpty(sqlFile)) {
                String[] sql = sqlFile.split(SYMBOL_COMMA);
                for (String item : sql) {
                    fileLog.add(item);
                }
            }
            LoggerUtils.writeGenerateCodeInfo(new Date(), fileLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
