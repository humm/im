package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.GenerateCodeDto;
import com.hoomoomoo.im.dto.LogDto;
import com.hoomoomoo.im.service.*;
import com.hoomoomoo.im.utils.*;
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
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
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
    private Button columnInfo;

    @FXML
    private TableView log;

    @FXML
    private Button execute;

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
        generateCodeDto.setTable(table.getText().trim());
        generateCodeDto.setAsyTable(asyTable.getText().trim());
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
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        }
    }

    @FXML
    void columnConfig(ActionEvent event) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            Stage columnStage = appConfigDto.getColumnStage();
            if (columnStage == null) {
                initGenerateCodeDto();
                InitTable.init(appConfigDto.getGenerateCodeDto());
                Parent root = new FXMLLoader().load(new FileInputStream(FileUtils.getFilePath(PATH_COLUMN_SET_FXML)));
                Scene scene = new Scene(root);
                scene.getStylesheets().add(FileUtils.getFileUrl(PATH_STARTER_CSS).toExternalForm());
                columnStage = new Stage();
                columnStage.getIcons().add(new Image(SET_ICON));
                columnStage.setScene(scene);
                columnStage.setTitle("配置字段信息");
                columnStage.setResizable(false);
                columnStage.show();
                appConfigDto.setColumnStage(columnStage);
                columnStage.setOnCloseRequest(columnEvent -> {
                    appConfigDto.getColumnStage().close();
                    appConfigDto.setColumnStage(null);
                });
            } else {
                columnStage.toFront();
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
            String pageType = appConfigDto.getGenerateCodePageType();
            if (BaseConst.PAGE_TYPE_SET.equals(pageType)) {
                selectSet(null);
            } else if (BaseConst.PAGE_TYPE_QUERY.equals(pageType)) {
                selectQuery(null);
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
            GenerateCodeDto generateCodeDto = new GenerateCodeDto();
            appConfigDto.setGenerateCodeDto(generateCodeDto);
            generateCodeDto.setJavaPath(appConfigDto.getGenerateCodeJavaPath());
            generateCodeDto.setVuePath(appConfigDto.getGenerateCodeVuePath());
            generateCodeDto.setSqlPath(appConfigDto.getGenerateCodeSqlPath());
            generateCodeDto.setRoutePath(appConfigDto.getGenerateCodeRoutePath());
            generateCodeDto.setPageType(appConfigDto.getGenerateCodePageType());
            generateCodeDto.setAuthor(appConfigDto.getGenerateCodeAuthor());
            generateCodeDto.setDbType(appConfigDto.getGenerateCodeDbType());
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        }
    }
}
