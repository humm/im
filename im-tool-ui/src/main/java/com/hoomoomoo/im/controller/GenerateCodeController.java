package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.GenerateCodeDto;
import com.hoomoomoo.im.service.*;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.FunctionConfig.GENERATE_CODE;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/10/15
 */
public class GenerateCodeController extends BaseController implements Initializable {

    @FXML
    private AnchorPane generateCode;

    @FXML
    private TextField javaPath;

    @FXML
    private TextField vuePath;

    @FXML
    private TextField sqlPath;

    @FXML
    private TextField routePath;

    @FXML
    private RadioButton set;

    @FXML
    private RadioButton query;

    @FXML
    private RadioButton dbPub;

    @FXML
    private RadioButton dbTrans;

    @FXML
    private RadioButton dbQuery;

    @FXML
    private RadioButton dbOrder;

    @FXML
    private TextField author;

    @FXML
    private TextField dtoCode;

    @FXML
    private TextField menuCode;

    @FXML
    private TextField menuName;

    @FXML
    private TextArea table;

    @FXML
    private TextArea asyTable;

    @FXML
    private TextArea column;

    @FXML
    private TableView<?> log;

    @FXML
    private Button execute;

    private GenerateCodeDto generateCodeDto = new GenerateCodeDto();

    @FXML
    void selectSet(ActionEvent event) {
        OutputUtils.selected(set, true);
        OutputUtils.selected(query, false);
    }

    @FXML
    void selectQuery(ActionEvent event) {
        OutputUtils.selected(set, false);
        OutputUtils.selected(query, true);
    }

    @FXML
    void selectDbPub(ActionEvent event) {
        OutputUtils.selected(dbPub, true);
        OutputUtils.selected(dbTrans, false);
        OutputUtils.selected(dbQuery, false);
        OutputUtils.selected(dbOrder, false);
    }

    @FXML
    void selectDbTrans(ActionEvent event) {
        OutputUtils.selected(dbPub, false);
        OutputUtils.selected(dbTrans, true);
        OutputUtils.selected(dbQuery, false);
        OutputUtils.selected(dbOrder, false);
    }

    @FXML
    void selectDbQuery(ActionEvent event) {
        OutputUtils.selected(dbPub, false);
        OutputUtils.selected(dbTrans, false);
        OutputUtils.selected(dbQuery, true);
        OutputUtils.selected(dbOrder, false);
    }

    @FXML
    void selectDbOrder(ActionEvent event) {
        OutputUtils.selected(dbPub, false);
        OutputUtils.selected(dbTrans, false);
        OutputUtils.selected(dbQuery, false);
        OutputUtils.selected(dbOrder, true);
    }

    void initGenerateCodeDto() {
        generateCodeDto.getColumnMap().clear();
        generateCodeDto.getAsyColumnMap().clear();
        generateCodeDto.getPrimaryKeyMap().clear();
        generateCodeDto.getMenuList().clear();
        generateCodeDto.setJavaPath(javaPath.getText());
        generateCodeDto.setVuePath(vuePath.getText());
        generateCodeDto.setSqlPath(sqlPath.getText());
        generateCodeDto.setRoutePath(routePath.getText());
        generateCodeDto.setAuthor(author.getText());
        generateCodeDto.setDtoCode(dtoCode.getText());
        generateCodeDto.setMenuCode(menuCode.getText());
        generateCodeDto.setMenuName(menuName.getText());
        generateCodeDto.setTable(table.getText().trim());
        generateCodeDto.setAsyTable(asyTable.getText().trim());
        generateCodeDto.setColumn(column.getText().trim());
        if (set.isSelected()) {
            generateCodeDto.setPageType(String.valueOf(set.getUserData()));
        } else {
            generateCodeDto.setPageType(String.valueOf(query.getUserData()));
        }

        if (dbPub.isSelected()) {
            generateCodeDto.setDbType(String.valueOf(dbPub.getUserData()));
        } else if (dbTrans.isSelected()) {
            generateCodeDto.setDbType(String.valueOf(dbTrans.getUserData()));
        } else if (dbQuery.isSelected()) {
            generateCodeDto.setDbType(String.valueOf(dbQuery.getUserData()));
        } else {
            generateCodeDto.setDbType(String.valueOf(dbOrder.getUserData()));
        }

        generateCodeDto.setDtoCode("fundPropFavourSetDemo");
        generateCodeDto.setMenuCode("fundsysinfo.fundDiscountInfo.fundPropFavourSetDemo");
        generateCodeDto.setMenuName("信息维护.优惠信息.赎回转换优惠设置Demo");
        generateCodeDto.setTable("create table tbfundprdpropfavour(\n" +
                "\tta_code                   VARCHAR2(18)         default ' ' not null,\n" +
                "\tinvest_direction          VARCHAR2(1)          default ' ' not null,\n" +
                "\tbusin_code                VARCHAR2(6)          default ' ' not null,\n" +
                "\tid_type                   VARCHAR2(18)         default ' ' not null,\n" +
                "\tprd_code                VARCHAR2(6)          default ' ' not null,\n" +
                "\tbranch_no                VARCHAR2(6)          default ' ' not null,\n" +
                "\tseller_code               VARCHAR2(9)          default ' ' not null,\n" +
                "\tamt_way                   VARCHAR2(2)          default ' ' not null,\n" +
                "\ttrans_way                 VARCHAR2(1)          default ' ' not null,\n" +
                "\tori_source_flag           VARCHAR2(1)          default ' ' not null,\n" +
                "\tsource_flag               VARCHAR2(1)          default ' ' not null,\n" +
                "\ttarg_invest_direction     VARCHAR2(1)          default ' ' not null,\n" +
                "\tmin_amt                   NUMBER(18,2)         default 0.0 not null,\n" +
                "\tmax_amt                   NUMBER(18,2)         default 0.0 not null,\n" +
                "\tmin_hold                  NUMBER(18,3)         default 0.0 not null,\n" +
                "\tmax_hold                  NUMBER(18,3)         default 0.0 not null,\n" +
                "\tbegin_date                INTEGER              default 0 not null,\n" +
                "\tend_date                  INTEGER              default 0 not null,\n" +
                "\tfare_type                 VARCHAR2(1)          default ' ' not null,\n" +
                "\tfavor_ratio               NUMBER(18,4)         default 0.0 not null,\n" +
                "\tfavor_type                VARCHAR2(1)          default ' ' not null,\n" +
                "\top_times                  INTEGER              default 0 not null,\n" +
                "\tconstraint pk_fundprdpropfavour primary key (invest_direction, busin_code, seller_code, amt_way, trans_way, ori_source_flag, source_flag, targ_invest_direction, begin_date, min_amt, min_hold, fare_type, favor_type)\n" +
                ") ;");
        generateCodeDto.setAsyTable("create table tbfundprdpropfavourasy(\n" +
                "\tta_code                   VARCHAR2(18)         default ' ' not null,\n" +
                "\tinvest_direction          VARCHAR2(1)          default ' ' not null,\n" +
                "\tbusin_code                VARCHAR2(6)          default ' ' not null,\n" +
                "\tid_type                   VARCHAR2(18)         default ' ' not null,\n" +
                "\tprd_code                VARCHAR2(6)          default ' ' not null,\n" +
                "\tbranch_no                VARCHAR2(6)          default ' ' not null,\n" +
                "\tseller_code               VARCHAR2(9)          default ' ' not null,\n" +
                "\tamt_way                   VARCHAR2(2)          default ' ' not null,\n" +
                "\ttrans_way                 VARCHAR2(1)          default ' ' not null,\n" +
                "\tori_source_flag           VARCHAR2(1)          default ' ' not null,\n" +
                "\tsource_flag               VARCHAR2(1)          default ' ' not null,\n" +
                "\ttarg_invest_direction     VARCHAR2(1)          default ' ' not null,\n" +
                "\tmin_amt                   NUMBER(18,2)         default 0.0 not null,\n" +
                "\tmax_amt                   NUMBER(18,2)         default 0.0 not null,\n" +
                "\tmin_hold                  NUMBER(18,3)         default 0.0 not null,\n" +
                "\tmax_hold                  NUMBER(18,3)         default 0.0 not null,\n" +
                "\tbegin_date                INTEGER              default 0 not null,\n" +
                "\tend_date                  INTEGER              default 0 not null,\n" +
                "\tfare_type                 VARCHAR2(1)          default ' ' not null,\n" +
                "\tfavor_ratio               NUMBER(18,4)         default 0.0 not null,\n" +
                "\tfavor_type                VARCHAR2(1)          default ' ' not null,\n" +
                "\top_times                  INTEGER              default 0 not null,\n" +
                "\tentry_serial_no           VARCHAR2(32)         default ' ' not null,\n" +
                "\tentry_order_no            INTEGER              default 0 not null,\n" +
                "\toper_no                   VARCHAR2(32)         default ' ' not null,\n" +
                "\tserial_status             VARCHAR2(1)          default ' ' not null,\n" +
                "\top_dir                    VARCHAR2(1)          default ' ' not null,\n" +
                "\tconstraint pk_tbfundprdpropfavourasy primary key (entry_serial_no, entry_order_no)\n" +
                ");");
        generateCodeDto.setColumn("{\n" +
                "\tinvest_direction:{\n" +
                "\t\tname:'投资方向',\n" +
                "\t\tdict:'F_C20010',\n" +
                "\t\tmulti:'1'\n" +
                "\t},\n" +
                "\tbusin_code:{\n" +
                "\t\tname:'业务类型',\n" +
                "\t\tdict:'F_C30001'\n" +
                "\t},\n" +
                "\tid_type:{\n" +
                "\t\tname:'证件号码',\n" +
                "\t\tdict:'',\n" +
                "\t\tmulti:''\n" +
                "\t},\n" +
                "\tprd_code:{\n" +
                "\t\tname:'基金代码',\n" +
                "\t\tdict:'',\n" +
                "\t\tmulti:''\n" +
                "\t},\n" +
                "\tbranch_no:{\n" +
                "\t\tname:'网点代码',\n" +
                "\t\tdict:'',\n" +
                "\t\tmulti:''\n" +
                "\t},\n" +
                "\tseller_code:{\n" +
                "\t\tname:'销售商代码',\n" +
                "\t\tdict:'',\n" +
                "\t\tmulti:''\n" +
                "\t},\n" +
                "\tamt_way:{\n" +
                "\t\tname:'资金方式',\n" +
                "\t\tdict:'F_C20057',\n" +
                "\t\trequired:'1',\n" +
                "\t\tmulti:''\n" +
                "\t},\n" +
                "\ttrans_way:{\n" +
                "\t\tname:'交易方式',\n" +
                "\t\tdict:'F_C20043',\n" +
                "\t\tmulti:''\n" +
                "\t},\n" +
                "\tori_source_flag:{\n" +
                "\t\tname:'份额原始来源',\n" +
                "\t\tdict:'F_C20004',\n" +
                "\t\tmulti:'1'\n" +
                "\t},\n" +
                "\tsource_flag:{\n" +
                "\t\tname:'份额来源',\n" +
                "\t\tdict:'F_C20004',\n" +
                "\t\tmulti:'1'\n" +
                "\t},\n" +
                "\ttarg_invest_direction:{\n" +
                "\t\tname:'对方投资方向',\n" +
                "\t\tdict:'F_C20010',\n" +
                "\t\tmulti:''\n" +
                "\t},\n" +
                "\tmin_amt:{\n" +
                "\t\tname:'金额最小',\n" +
                "\t\tdict:'',\n" +
                "\t\tmulti:''\n" +
                "\t},\n" +
                "\tmax_amt:{\n" +
                "\t\tname:'金额最大',\n" +
                "\t\tdict:'',\n" +
                "\t\tmulti:''\n" +
                "\t},\n" +
                "\tmin_hold:{\n" +
                "\t\tname:'存续天数最小',\n" +
                "\t\tdict:'',\n" +
                "\t\tmulti:''\n" +
                "\t},\n" +
                "\tmax_hold:{\n" +
                "\t\tname:'存续天数最大',\n" +
                "\t\tdict:'',\n" +
                "\t\tmulti:''\n" +
                "\t},\n" +
                "\tbegin_date:{\n" +
                "\t\tname:'优惠开始日期',\n" +
                "\t\tdict:'',\n" +
                "\t\tmulti:'',\n" +
                "\t\tdate:'1'\n" +
                "\t},\n" +
                "\tend_date:{\n" +
                "\t\tname:'优惠截止日期',\n" +
                "\t\tdict:'',\n" +
                "\t\tmulti:'',\n" +
                "\t\tdate:'1'\n" +
                "\t},\n" +
                "\tfare_type:{\n" +
                "\t\tname:'费用类型',\n" +
                "\t\tdict:'F_C20015',\n" +
                "\t\tmulti:'1'\n" +
                "\t},\n" +
                "\tfavor_ratio:{\n" +
                "\t\tname:'优惠费率',\n" +
                "\t\tdict:'',\n" +
                "\t\tmulti:''\n" +
                "\t},\n" +
                "\tfavor_type:{\n" +
                "\t\tname:'优惠类型',\n" +
                "\t\tdict:'F_C20170',\n" +
                "\t},\n" +
                "\top_times:{\n" +
                "\t\tname:'1年最多优惠次数',\n" +
                "\t\tdict:'',\n" +
                "\t\tmulti:''\n" +
                "\t}\n" +
                "}");
    }

    @FXML
    void execute(ActionEvent event) {
        try {
            OutputUtils.clearLog(log);
            LoggerUtils.info(String.format(MSG_USE, GENERATE_CODE.getName()));
            initGenerateCodeDto();
            if (!CommonUtils.checkConfigGenerateCode(log, generateCodeDto)) {
                return;
            }
            setProgress(0);
            generateCode(generateCodeDto);
            updateProgress();
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.toString());
        }
    }

    private void generateCode(GenerateCodeDto generateCodeDto) throws Exception {

        new Thread(() -> {
            try {
                execute.setDisable(true);
                List<String> fileLog = new ArrayList<>();

                InitTable.init(generateCodeDto);
                OutputUtils.info(log, "表结构解析成功");

                GenerateAuditService.getPackageName(generateCodeDto);
                GenerateExcelConfig.getPackageName(generateCodeDto);

                String dtoFile = GenerateDto.init(generateCodeDto);
                if (StringUtils.isNotEmpty(dtoFile)) {
                    fileLog.add(dtoFile);
                    OutputUtils.info(log, String.format(MSG_FILE_GENERATE, "dto"));
                }

                String interfaceFile = GenerateInterface.init(generateCodeDto);
                if (StringUtils.isNotEmpty(interfaceFile)) {
                    fileLog.add(interfaceFile);
                    OutputUtils.info(log, String.format(MSG_FILE_GENERATE, "interface"));
                }

                String serviceFile = GenerateService.init(generateCodeDto);
                if (StringUtils.isNotEmpty(serviceFile)) {
                    fileLog.add(serviceFile);
                    OutputUtils.info(log, String.format(MSG_FILE_GENERATE, "service"));
                }

                String auditServiceFile = GenerateAuditService.init(generateCodeDto);
                if (StringUtils.isNotEmpty(auditServiceFile)) {
                    fileLog.add(auditServiceFile);
                    OutputUtils.info(log, String.format(MSG_FILE_GENERATE, "auditService"));
                }

                String controllerFile = GenerateController.init(generateCodeDto);
                if (StringUtils.isNotEmpty(controllerFile)) {
                    fileLog.add(controllerFile);
                    OutputUtils.info(log, String.format(MSG_FILE_GENERATE, "controller"));
                }

                String importFile = GenerateExcelConfig.init(generateCodeDto);
                if (StringUtils.isNotEmpty(importFile)) {
                    fileLog.add(importFile);
                    OutputUtils.info(log, String.format(MSG_FILE_GENERATE, "excelConfig"));
                }

                String exportFile = GenerateExportConfig.init(generateCodeDto);
                if (StringUtils.isNotEmpty(exportFile)) {
                    fileLog.add(exportFile);
                    OutputUtils.info(log, String.format(MSG_FILE_GENERATE, "exportConfig"));
                }

                String sqlFile = GenerateSql.init(generateCodeDto);
                if (StringUtils.isNotEmpty(sqlFile)) {
                    String[] sql = sqlFile.split(SYMBOL_COMMA);
                    for (String item : sql) {
                        fileLog.add(item);
                        String fileName = item.substring(item.lastIndexOf("/") + 1);
                        OutputUtils.info(log, String.format(MSG_FILE_GENERATE, fileName));
                    }
                }

                String routeFile = GenerateRoute.init(generateCodeDto);
                if (StringUtils.isNotEmpty(routeFile)) {
                    fileLog.add(routeFile);
                    OutputUtils.info(log, String.format(MSG_FILE_GENERATE, "route"));
                }

                String vueFile = GenerateVue.init(generateCodeDto);
                if (StringUtils.isNotEmpty(vueFile)) {
                    fileLog.add(vueFile);
                    OutputUtils.info(log, String.format(MSG_FILE_GENERATE, "vue"));
                }

                LoggerUtils.writeGenerateCodeInfo(new Date(), fileLog);
                setProgress(1);
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(log, e.toString());
            } finally {
                execute.setDisable(false);
            }
        }).start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AppConfigDto appConfigDto = null;
        try {
            appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (StringUtils.isNotBlank(appConfigDto.getGenerateCodeJavaPath())) {
                OutputUtils.info(javaPath, appConfigDto.getGenerateCodeJavaPath());
            }
            if (StringUtils.isNotBlank(appConfigDto.getGenerateCodeSqlPath())) {
                OutputUtils.info(sqlPath, appConfigDto.getGenerateCodeSqlPath());
            }
            if (StringUtils.isNotBlank(appConfigDto.getGenerateCodeVuePath())) {
                OutputUtils.info(vuePath, appConfigDto.getGenerateCodeVuePath());
            }
            if (StringUtils.isNotBlank(appConfigDto.getGenerateCodeRoutePath())) {
                OutputUtils.info(routePath, appConfigDto.getGenerateCodeRoutePath());
            }
            if (StringUtils.isNotBlank(appConfigDto.getGenerateCodeAuthor())) {
                OutputUtils.info(author, appConfigDto.getGenerateCodeAuthor());
            }
            String pageType = appConfigDto.getGenerateCodePageType();
            if (PAGE_TYPE_SET.equals(pageType)) {
                selectSet(null);
            } else if (PAGE_TYPE_QUERY.equals(pageType)) {
                selectQuery(null);
            }
            String dbType = appConfigDto.getGenerateCodeDbType();
            if (DB_TYPE_PUB.equals(dbType)) {
                selectDbPub(null);
            } else if (DB_TYPE_TRANS.equals(dbType)) {
                selectDbTrans(null);
            } else if (DB_TYPE_TRANS_QUERY.equals(dbType)) {
                selectDbQuery(null);
            } else if (DB_TYPE_TRANS_ORDER.equals(dbType)) {
                selectDbOrder(null);
            }
            generateCodeDto.setJavaPath(appConfigDto.getGenerateCodeJavaPath());
            generateCodeDto.setVuePath(appConfigDto.getGenerateCodeVuePath());
            generateCodeDto.setSqlPath(appConfigDto.getGenerateCodeSqlPath());
            generateCodeDto.setRoutePath(appConfigDto.getGenerateCodeRoutePath());
            generateCodeDto.setPageType(appConfigDto.getGenerateCodePageType());
            generateCodeDto.setAuthor(appConfigDto.getGenerateCodeAuthor());
            generateCodeDto.setDbType(appConfigDto.getGenerateCodeDbType());
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.toString());
        }
    }
}
