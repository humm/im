package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.GenerateCodeDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import com.hoomoomoo.im.utils.generate.*;
import javafx.application.Platform;
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
public class GenerateCodeController implements Initializable {

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
    private ProgressIndicator schedule;

    @FXML
    private Label scheduleText;

    @FXML
    private Button execute;

    private double progress = 0;

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
                GenerateImport.getPackageName(generateCodeDto);

                String dtoFile = GenerateDto.init(generateCodeDto);
                if (StringUtils.isNotEmpty(dtoFile)) {
                    fileLog.add(dtoFile);
                    OutputUtils.info(log, "dto文件生成成功");
                }

                String interfaceFile = GenerateInterface.init(generateCodeDto);
                if (StringUtils.isNotEmpty(interfaceFile)) {
                    fileLog.add(interfaceFile);
                    OutputUtils.info(log, "interface文件生成成功");
                }

                String serviceFile = GenerateService.init(generateCodeDto);
                if (StringUtils.isNotEmpty(serviceFile)) {
                    fileLog.add(serviceFile);
                    OutputUtils.info(log, "service文件生成成功");
                }

                String auditServiceFile = GenerateAuditService.init(generateCodeDto);
                if (StringUtils.isNotEmpty(auditServiceFile)) {
                    fileLog.add(auditServiceFile);
                    OutputUtils.info(log, "auditService文件生成成功");
                }

                String controllerFile = GenerateController.init(generateCodeDto);
                if (StringUtils.isNotEmpty(controllerFile)) {
                    fileLog.add(controllerFile);
                    OutputUtils.info(log, "controller文件生成成功");
                }

                String importFile = GenerateImport.init(generateCodeDto);
                if (StringUtils.isNotEmpty(importFile)) {
                    fileLog.add(importFile);
                    OutputUtils.info(log, "import文件生成成功");
                }

                String exportFile = GenerateExport.init(generateCodeDto);
                if (StringUtils.isNotEmpty(exportFile)) {
                    fileLog.add(exportFile);
                    OutputUtils.info(log, "export文件生成成功");
                }

                String sqlFile = GenerateSql.init(generateCodeDto);
                if (StringUtils.isNotEmpty(sqlFile)) {
                    String[] sql = sqlFile.split(SYMBOL_COMMA);
                    for (String item : sql) {
                        fileLog.add(item);
                    }
                    OutputUtils.info(log, "sql文件生成成功");
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

    private void updateProgress() {
        new Thread(() -> {
            while (true) {
                if (progress >= 0.95) {
                    break;
                }
                if (progress <= 0.6) {
                    setProgress(progress + 0.05);
                } else if (progress < 0.9) {
                    setProgress(progress + 0.01);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LoggerUtils.info(e);
                }
            }
        }).start();
    }

    synchronized private void setProgress(double value) {
        try {
            progress = value;
            Platform.runLater(() -> {
                schedule.setProgress(progress);
                scheduleText.setText(String.valueOf(value * 100).split(SYMBOL_POINT_SLASH)[0] + SYMBOL_PERCENT);
                schedule.requestFocus();
            });
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
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
