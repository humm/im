package com.hoomoomoo.im.controller;

import com.alibaba.fastjson.JSONObject;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.GenerateCodeDto;
import com.hoomoomoo.im.dto.GenerateCodeRecord;
import com.hoomoomoo.im.service.*;
import com.hoomoomoo.im.utils.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.GENERATE_CODE;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/10/15
 */
public class GenerateCodeController extends BaseController implements Initializable {

    @FXML
    private AnchorPane generateCode;

    /*@FXML
    private TextField javaPath;

    @FXML
    private TextField vuePath;

    @FXML
    private TextField sqlPath;

    @FXML
    private TextField routePath;*/

    @FXML
    private TextField menuOrder;

    @FXML
    private RadioButton set;

    @FXML
    private RadioButton query;

    @FXML
    private RadioButton queryConfig;

    @FXML
    private RadioButton dbPub;

    @FXML
    private RadioButton dbTrans;

    @FXML
    private RadioButton dbQuery;

    @FXML
    private RadioButton dbOrder;

    @FXML
    private RadioButton fund;

    @FXML
    private RadioButton account;

    @FXML
    private TextField author;

    @FXML
    private TextField dtoCode;

    /*@FXML
    private TextField menuCode;*/

    @FXML
    private TextField menuCode1;

    @FXML
    private TextField menuCode2;

    @FXML
    private TextField menuCode3;

   /* @FXML
    private TextField menuName;*/

    @FXML
    private TextField menuName1;

    @FXML
    private TextField menuName2;

    @FXML
    private TextField menuName3;

    @FXML
    private Button table;

    @FXML
    private Button asyTable;

    @FXML
    private Button columnInfo;

    @FXML
    private TableView log;

    @FXML
    private Button execute;

    @FXML
    private ComboBox record;

    private Map<String, GenerateCodeRecord> configRecord = new LinkedHashMap<>(16);

    @FXML
    void selectSet(ActionEvent event) {
        OutputUtils.selected(set, true);
        OutputUtils.selected(query, false);
        OutputUtils.selected(queryConfig, false);
    }

    @FXML
    void selectQuery(ActionEvent event) {
        OutputUtils.selected(set, false);
        OutputUtils.selected(query, true);
        OutputUtils.selected(queryConfig, false);
    }

    @FXML
    void selectFund(ActionEvent event) {
        OutputUtils.selected(fund, true);
        OutputUtils.selected(account, false);
    }

    @FXML
    void selectAccount(ActionEvent event) {
        OutputUtils.selected(account, true);
        OutputUtils.selected(fund, false);
    }

    @FXML
    void selectQueryConfig(ActionEvent event) {
        OutputUtils.selected(set, false);
        OutputUtils.selected(query, false);
        OutputUtils.selected(queryConfig, true);
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

    @FXML
    void selectRecord(ActionEvent event) {
        String item =  (String)record.getSelectionModel().getSelectedItem();
        try {
            AppConfigDto appConfigSelected;
            GenerateCodeDto generateCodeDto;
            if (StringUtils.isBlank(item)) {
                appConfigSelected = ConfigCache.getConfigCache().getAppConfigDto();
                generateCodeDto = new GenerateCodeDto();
                generateCodeDto.setAuthor(appConfigSelected.getGenerateCodeAuthor());
            } else {
                appConfigSelected = configRecord.get(item).getAppConfigDto();
                generateCodeDto = appConfigSelected.getGenerateCodeDto();
            }

            AppConfigDto appConfigDtoCache = ConfigCache.getConfigCache().getAppConfigDto();
            appConfigDtoCache.setGenerateCodeDto(generateCodeDto);

            generateCodeDto.setJavaPath(appConfigDtoCache.getGenerateCodeJavaPath());
            generateCodeDto.setSqlPath(appConfigDtoCache.getGenerateCodeSqlPath());
            generateCodeDto.setVuePath(appConfigDtoCache.getGenerateCodeVuePath());
            generateCodeDto.setRoutePath(appConfigDtoCache.getGenerateCodeRoutePath());
            generateCodeDto.setFieldTranslateMap(appConfigDtoCache.getFieldTranslateMap());

            OutputUtils.info(author, generateCodeDto.getAuthor());
            OutputUtils.info(menuCode1, generateCodeDto.getMenuCode1());
            OutputUtils.info(menuCode2, generateCodeDto.getMenuCode2());
            OutputUtils.info(menuCode3, generateCodeDto.getMenuCode3());
            OutputUtils.info(menuName1, generateCodeDto.getMenuName1());
            OutputUtils.info(menuName2, generateCodeDto.getMenuName2());
            OutputUtils.info(menuName3, generateCodeDto.getMenuName3());
            OutputUtils.info(dtoCode, generateCodeDto.getDtoCode());
            OutputUtils.info(menuOrder, generateCodeDto.getMenuOrder());
            appConfigSelected.setGenerateCodeMenuType(generateCodeDto.getMenuType());
            appConfigSelected.setGenerateCodePageType(generateCodeDto.getPageType());
            appConfigSelected.setGenerateCodeDbType(generateCodeDto.getDbType());

            initComponent(appConfigSelected);
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        }
    }

    void initGenerateCodeDto() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        GenerateCodeDto generateCodeDto = appConfigDto.getGenerateCodeDto();
        generateCodeDto.getColumnMap().clear();
        generateCodeDto.getAsyColumnMap().clear();
        generateCodeDto.getPrimaryKeyMap().clear();
        generateCodeDto.getMenuList().clear();

        generateCodeDto.setJavaPath(appConfigDto.getGenerateCodeJavaPath());
        generateCodeDto.setSqlPath(appConfigDto.getGenerateCodeSqlPath());
        generateCodeDto.setVuePath(appConfigDto.getGenerateCodeVuePath());
        generateCodeDto.setRoutePath(appConfigDto.getGenerateCodeRoutePath());

        generateCodeDto.setAuthor(author.getText());
        generateCodeDto.setDtoCode(dtoCode.getText());
        generateCodeDto.setMenuCode1(menuCode1.getText());
        generateCodeDto.setMenuCode2(menuCode2.getText());
        generateCodeDto.setMenuCode3(menuCode3.getText());
        generateCodeDto.setMenuName1(menuName1.getText());
        generateCodeDto.setMenuName2(menuName2.getText());
        generateCodeDto.setMenuName3(menuName3.getText());
        generateCodeDto.setMenuOrder(menuOrder.getText());
        generateCodeDto.setFieldTranslateMap(appConfigDto.getFieldTranslateMap());
        if (set.isSelected()) {
            generateCodeDto.setPageType(String.valueOf(set.getUserData()));
        } else if (query.isSelected()){
            generateCodeDto.setPageType(String.valueOf(query.getUserData()));
        } else {
            generateCodeDto.setPageType(String.valueOf(queryConfig.getUserData()));
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

        if (fund.isSelected()) {
            generateCodeDto.setMenuType(String.valueOf(fund.getUserData()));
        } else if (account.isSelected()) {
            generateCodeDto.setMenuType(String.valueOf(account.getUserData()));
        }

    }

    @FXML
    void execute(ActionEvent event) {
        try {
            OutputUtils.clearLog(log);
            LoggerUtils.info(String.format(BaseConst.MSG_USE, GENERATE_CODE.getName()));
            initGenerateCodeDto();
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (!TaCommonUtil.checkConfigGenerateCode(log, appConfigDto.getGenerateCodeDto())) {
                return;
            }
            setProgress(0);
            generateCode(appConfigDto.getGenerateCodeDto());
            updateProgress();
            initHistoryRecord();
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        }
    }

    @FXML
    void columnConfig(ActionEvent event) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            Stage stage = appConfigDto.getColumnStage();
            if (stage == null) {
                initGenerateCodeDto();
                InitTable.init(appConfigDto.getGenerateCodeDto());
                Parent root = new FXMLLoader().load(new FileInputStream(FileUtils.getFilePath(PATH_COLUMN_SET_FXML)));
                Scene scene = new Scene(root);
                scene.getStylesheets().add(FileUtils.getFileUrl(PATH_STARTER_CSS).toExternalForm());
                stage = new Stage();
                stage.getIcons().add(new Image(PATH_ICON));
                stage.setScene(scene);
                stage.setTitle(CONFIG_COLUMN);
                stage.setResizable(false);
                stage.show();
                appConfigDto.setColumnStage(stage);
                stage.setOnCloseRequest(columnEvent -> {
                    appConfigDto.getColumnStage().close();
                    appConfigDto.setColumnStage(null);
                });
            } else {
                stage.toFront();
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        }
    }

    @FXML
    void tableConfig(ActionEvent event) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            appConfigDto.setPageType(STR_1);
            Stage stage = appConfigDto.getTableStage();
            if (stage == null) {
                Parent root = new FXMLLoader().load(new FileInputStream(FileUtils.getFilePath(PATH_BLANK_SET_FXML)));
                Scene scene = new Scene(root);
                scene.getStylesheets().add(FileUtils.getFileUrl(PATH_STARTER_CSS).toExternalForm());
                stage = new Stage();
                stage.getIcons().add(new Image(PATH_ICON));
                stage.setScene(scene);
                stage.setTitle(CONFIG_TABLE);
                stage.setResizable(false);
                stage.show();
                appConfigDto.setTableStage(stage);
                stage.setOnCloseRequest(columnEvent -> {
                    appConfigDto.getTableStage().close();
                    appConfigDto.setTableStage(null);
                });
            } else {
                stage.toFront();
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        }
    }

    @FXML
    void asyTableConfig(ActionEvent event) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            appConfigDto.setPageType(STR_2);
            Stage stage = appConfigDto.getAsyTableStage();
            if (stage == null) {
                Parent root = new FXMLLoader().load(new FileInputStream(FileUtils.getFilePath(PATH_BLANK_SET_FXML)));
                Scene scene = new Scene(root);
                scene.getStylesheets().add(FileUtils.getFileUrl(PATH_STARTER_CSS).toExternalForm());
                stage = new Stage();
                stage.getIcons().add(new Image(PATH_ICON));
                stage.setScene(scene);
                stage.setTitle(CONFIG_TABLE);
                stage.setResizable(false);
                stage.show();
                appConfigDto.setAsyTableStage(stage);
                stage.setOnCloseRequest(columnEvent -> {
                    appConfigDto.getAsyTableStage().close();
                    appConfigDto.setAsyTableStage(null);
                });
            } else {
                stage.toFront();
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        }
    }

    private void generateCode(GenerateCodeDto generateCodeDto) throws Exception {

        new Thread(() -> {
            try {
                execute.setDisable(true);
                List<String> fileLog = new ArrayList<>();
                Date currentDate = new Date();

                InitTable.init(generateCodeDto);
                OutputUtils.info(log, "表结构解析成功");

                GenerateAuditService.getPackageName(generateCodeDto);
                GenerateExcelConfig.getPackageName(generateCodeDto);

                String dtoFile = GenerateDto.init(generateCodeDto);
                if (StringUtils.isNotEmpty(dtoFile)) {
                    fileLog.add(dtoFile);
                    OutputUtils.info(log, String.format(BaseConst.MSG_FILE_GENERATE, "dto"));
                }

                String interfaceFile = GenerateInterface.init(generateCodeDto);
                if (StringUtils.isNotEmpty(interfaceFile)) {
                    fileLog.add(interfaceFile);
                    OutputUtils.info(log, String.format(BaseConst.MSG_FILE_GENERATE, "interface"));
                }

                String serviceFile = GenerateService.init(generateCodeDto);
                if (StringUtils.isNotEmpty(serviceFile)) {
                    fileLog.add(serviceFile);
                    OutputUtils.info(log, String.format(BaseConst.MSG_FILE_GENERATE, "service"));
                }

                String auditServiceFile = GenerateAuditService.init(generateCodeDto);
                if (StringUtils.isNotEmpty(auditServiceFile)) {
                    fileLog.add(auditServiceFile);
                    OutputUtils.info(log, String.format(BaseConst.MSG_FILE_GENERATE, "auditService"));
                }

                String controllerFile = GenerateController.init(generateCodeDto);
                if (StringUtils.isNotEmpty(controllerFile)) {
                    fileLog.add(controllerFile);
                    OutputUtils.info(log, String.format(BaseConst.MSG_FILE_GENERATE, "controller"));
                }

                String importFile = GenerateExcelConfig.init(generateCodeDto);
                if (StringUtils.isNotEmpty(importFile)) {
                    fileLog.add(importFile);
                    OutputUtils.info(log, String.format(BaseConst.MSG_FILE_GENERATE, "excelConfig"));
                }

                String exportFile = GenerateExportConfig.init(generateCodeDto);
                if (StringUtils.isNotEmpty(exportFile)) {
                    fileLog.add(exportFile);
                    OutputUtils.info(log, String.format(BaseConst.MSG_FILE_GENERATE, "exportConfig"));
                }

                String sqlFile = GenerateSql.init(generateCodeDto);
                if (StringUtils.isNotEmpty(sqlFile)) {
                    String[] sql = sqlFile.split(BaseConst.SYMBOL_COMMA);
                    for (String item : sql) {
                        fileLog.add(item);
                        String fileName = item.substring(item.lastIndexOf("/") + 1);
                        OutputUtils.info(log, String.format(BaseConst.MSG_FILE_GENERATE, fileName));
                    }
                }

                String routeFile = GenerateRoute.init(generateCodeDto);
                if (StringUtils.isNotEmpty(routeFile)) {
                    fileLog.add(routeFile);
                    OutputUtils.info(log, String.format(BaseConst.MSG_FILE_GENERATE, "route"));
                }

                String vueFile = GenerateVue.init(generateCodeDto);
                if (StringUtils.isNotEmpty(vueFile)) {
                    fileLog.add(vueFile);
                    OutputUtils.info(log, String.format(BaseConst.MSG_FILE_GENERATE, "vue"));
                }

                LoggerUtils.writeGenerateCodeInfo(currentDate, fileLog);
                String record = generateCodeDto.getFunctionName() + SYMBOL_SPACE + SYMBOL_HYPHEN + SYMBOL_SPACE + CommonUtils.getCurrentDateTime2();
                record += CONFIG_PREFIX + JSONObject.toJSONString(ConfigCache.getConfigCache().getAppConfigDto()) + SYMBOL_NEXT_LINE;
                FileUtils.writeFile(FileUtils.getFilePath(String.format(PATH_RECORD_LOG, GENERATE_CODE.getLogFolder())), record, true);
                setProgress(1);
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(log, e.getMessage());
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
            /*if (StringUtils.isNotBlank(appConfigDto.getGenerateCodeJavaPath())) {
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
            }*/
            if (StringUtils.isNotBlank(appConfigDto.getGenerateCodeAuthor())) {
                OutputUtils.info(author, appConfigDto.getGenerateCodeAuthor());
            }
            if (StringUtils.isNotBlank(appConfigDto.getGenerateCodeMenuCode())) {
                String[] menuCode = appConfigDto.getGenerateCodeMenuCode().split(SYMBOL_POINT_SLASH);
                if (menuCode.length == 3) {
                    OutputUtils.info(menuCode1, menuCode[0]);
                    OutputUtils.info(menuCode2, menuCode[1]);
                    OutputUtils.info(menuCode3, menuCode[2]);
                }
            }
            if (StringUtils.isNotBlank(appConfigDto.getGenerateCodeMenuName())) {
                String[] menuName = appConfigDto.getGenerateCodeMenuName().split(SYMBOL_POINT_SLASH);
                if (menuName.length == 3) {
                    OutputUtils.info(menuName1, menuName[0]);
                    OutputUtils.info(menuName2, menuName[1]);
                    OutputUtils.info(menuName3, menuName[2]);
                }
            }

            initComponent(appConfigDto);

            GenerateCodeDto generateCodeDto = new GenerateCodeDto();
            appConfigDto.setGenerateCodeDto(generateCodeDto);
            generateCodeDto.setJavaPath(appConfigDto.getGenerateCodeJavaPath());
            generateCodeDto.setVuePath(appConfigDto.getGenerateCodeVuePath());
            generateCodeDto.setSqlPath(appConfigDto.getGenerateCodeSqlPath());
            generateCodeDto.setRoutePath(appConfigDto.getGenerateCodeRoutePath());
            generateCodeDto.setPageType(appConfigDto.getGenerateCodePageType());
            generateCodeDto.setAuthor(appConfigDto.getGenerateCodeAuthor());
            generateCodeDto.setDbType(appConfigDto.getGenerateCodeDbType());
            generateCodeDto.setMenuType(appConfigDto.getGenerateCodeMenuType());
            generateCodeDto.setFieldTranslateMap(appConfigDto.getFieldTranslateMap());

            initHistoryRecord();

            if (appConfigDto.getGenerateCodeDefaultLast() && record.getItems().size() > 1) {
                record.getSelectionModel().select(1);
                selectRecord(null);
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        }
    }

    private void initComponent(AppConfigDto appConfigDto) {
        String pageType = appConfigDto.getGenerateCodePageType();
        if (BaseConst.PAGE_TYPE_SET.equals(pageType)) {
            selectSet(null);
        } else if (BaseConst.PAGE_TYPE_QUERY.equals(pageType)) {
            selectQuery(null);
        } else if (BaseConst.PAGE_TYPE_QUERY_CONFIG.equals(pageType)) {
            selectQueryConfig(null);
        }
        String dbType = appConfigDto.getGenerateCodeDbType();
        if (BaseConst.DB_TYPE_PUB.equals(dbType)) {
            selectDbPub(null);
        } else if (BaseConst.DB_TYPE_TRANS.equals(dbType)) {
            selectDbTrans(null);
        } else if (BaseConst.DB_TYPE_TRANS_QUERY.equals(dbType)) {
            selectDbQuery(null);
        } else if (BaseConst.DB_TYPE_TRANS_ORDER.equals(dbType)) {
            selectDbOrder(null);
        }
        String menuType = appConfigDto.getGenerateCodeMenuType();
        if (MENU_TYPE_FUND.equals(menuType)) {
            selectFund(null);
        } else if (MENU_TYPE_ACCOUNT.equals(menuType)) {
            selectAccount(null);
        }

    }

    private void initHistoryRecord() {
        record.getItems().add(SYMBOL_EMPTY);
        List<String> recordList = null;
        try {
            recordList = FileUtils.readNormalFile(FileUtils.getFilePath(String.format(PATH_RECORD_LOG, GENERATE_CODE.getLogFolder())), false);
        } catch (FileNotFoundException e) {
            LoggerUtils.info(FileUtils.getFilePath(GENERATE_CODE.getLogFolder()) + FILE_TYPE_RECORD + " 不存在");
        } catch (IOException e) {
            LoggerUtils.info(e);
        }

        if (CollectionUtils.isNotEmpty(recordList)) {
            for (int i=recordList.size() -1; i>=0; i--) {
                String item = recordList.get(i);
                String[] element = item.split(CONFIG_PREFIX);
                if (element.length != 2) {
                    continue;
                }
                if (configRecord.containsKey(element[0])) {
                    continue;
                }
                GenerateCodeRecord generateCodeRecord = new GenerateCodeRecord();
                generateCodeRecord.setMenuName(element[0]);
                try {
                    generateCodeRecord.setAppConfigDto(JSONObject.parseObject(element[1], AppConfigDto.class));
                } catch (Exception e) {
                    LoggerUtils.info(String.format("历史数据转换错误，不兼容【%s】", element[0]));
                    continue;
                }
                configRecord.put(element[0], generateCodeRecord);

                record.getItems().add(element[0]);
            }
        }
    }
}
