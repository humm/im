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

    @FXML
    private TextField javaPath;

    @FXML
    private TextField vuePath;

    @FXML
    private TextField sqlPath;

    @FXML
    private TextField routePath;

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
    private TextField author;

    @FXML
    private TextField dtoCode;

    @FXML
    private TextField menuCode;

    @FXML
    private TextField menuName;

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
            AppConfigDto appConfigDto = configRecord.get(item).getAppConfigDto();
            AppConfigDto appConfigDtoCache = ConfigCache.getConfigCache().getAppConfigDto();
            GenerateCodeDto generateCodeDto = configRecord.get(item).getAppConfigDto().getGenerateCodeDto();
            appConfigDtoCache.setGenerateCodeDto(generateCodeDto);
            OutputUtils.info(javaPath, generateCodeDto.getJavaPath());
            OutputUtils.info(sqlPath, generateCodeDto.getSqlPath());
            OutputUtils.info(vuePath, generateCodeDto.getVuePath());
            OutputUtils.info(routePath, generateCodeDto.getRoutePath());
            OutputUtils.info(author, generateCodeDto.getAuthor());
            OutputUtils.info(menuCode, generateCodeDto.getMenuCode());
            OutputUtils.info(menuName, generateCodeDto.getMenuName());
            OutputUtils.info(dtoCode, generateCodeDto.getDtoCode());
            OutputUtils.info(menuOrder, generateCodeDto.getMenuOrder());
            appConfigDto.setGenerateCodePageType(generateCodeDto.getPageType());
            appConfigDto.setGenerateCodeDbType(generateCodeDto.getDbType());
            initComponent(appConfigDto);
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
        generateCodeDto.setJavaPath(javaPath.getText());
        generateCodeDto.setVuePath(vuePath.getText());
        generateCodeDto.setSqlPath(sqlPath.getText());
        generateCodeDto.setRoutePath(routePath.getText());
        generateCodeDto.setAuthor(author.getText());
        generateCodeDto.setDtoCode(dtoCode.getText());
        generateCodeDto.setMenuCode(menuCode.getText());
        generateCodeDto.setMenuName(menuName.getText());
        generateCodeDto.setMenuOrder(menuOrder.getText());
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
            buildRecord();
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
            if (StringUtils.isNotBlank(appConfigDto.getGenerateCodeMenuCode())) {
                OutputUtils.info(menuCode, appConfigDto.getGenerateCodeMenuCode());
            }
            if (StringUtils.isNotBlank(appConfigDto.getGenerateCodeMenuName())) {
                OutputUtils.info(menuName, appConfigDto.getGenerateCodeMenuName());
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

            buildRecord();

            if (appConfigDto.getGenerateCodeDefaultLast() && record.getItems().size() != 0) {
                record.getSelectionModel().select(0);
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
    }

    private void buildRecord() {
        List<String> recordList = null;
        try {
            recordList = FileUtils.readNormalFile(FileUtils.getFilePath(String.format(PATH_RECORD_LOG, GENERATE_CODE.getLogFolder())), false);
        } catch (FileNotFoundException e) {
            LoggerUtils.info(String.format(PATH_RECORD_LOG, GENERATE_CODE.getLogFolder()) + " 不存在");
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
                generateCodeRecord.setAppConfigDto(JSONObject.parseObject(element[1], AppConfigDto.class));
                configRecord.put(element[0], generateCodeRecord);

                ObservableList recordItems = record.getItems();
                recordItems.add(element[0]);
            }
        }
    }
}
