package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.DATABASE_SCRIPT;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2022-09-25
 */
public class DatabaseScriptController extends BaseController implements Initializable {

    @FXML
    private AnchorPane databaseScript;

    @FXML
    private TextField databasePath;

    @FXML
    private Button databaseExecute;

    @FXML
    private Button databaseExecuteDetail;

    @FXML
    private TextArea log;

    @FXML
    private Label fileNum;

    @FXML
    private Label sqlNum;

    @FXML
    private Label sqlFailNum;

    private String logFilePath;


    @FXML
    void selectDatabasePath(ActionEvent event) {
        DirectoryChooser fileChooser = new DirectoryChooser ();
        File file = fileChooser.showDialog(databaseScript.getScene().getWindow());
        if (file != null) {
            OutputUtils.clearLog(databasePath);
            OutputUtils.info(databasePath, file.getAbsolutePath());
        }
    }

    @FXML
    void databaseExecuteDetail(ActionEvent event) throws IOException {
        Runtime.getRuntime().exec("explorer /e,/select," + new File(logFilePath).getAbsolutePath());
    }

    @FXML
    void databaseExecute(ActionEvent event) {
        try {
            OutputUtils.clearLog(log);
            LoggerUtils.info(String.format(BaseConst.MSG_USE, DATABASE_SCRIPT.getName()));
            if (!TaCommonUtil.checkConfig(log, DATABASE_SCRIPT.getCode())) {
                return;
            }
            setProgress(0);
            if (StringUtils.isBlank(databasePath.getText())) {
                OutputUtils.info(log, "请选择数据库脚本位置");
                return;
            }
            updateProgress();
            executeUpdateScript();
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        }
    }

    private void executeUpdateScript() {
        new Thread(() -> {
            try {
                int executeSqlNum = 0;
                int executeFailSqlNum = 0;
                int filelNum = 0;
                List<String> failSql = new ArrayList<>();
                databaseExecute.setDisable(true);
                databaseExecuteDetail.setDisable(true);
                File fileDir = new File(databasePath.getText());
                if (fileDir.isDirectory()) {
                    List<File> fileList = getFileList(fileDir, new ArrayList<>(16));
                    if (CollectionUtils.isNotEmpty(fileList)) {
                        List<String> executeSql = new ArrayList<>(16);
                        List<String> content;
                        boolean nextFlag = false;
                        for (File item : fileList) {
                            nextFlag = true;
                            String encode = FileUtils.getFileEncode(item.getAbsolutePath());
                            OutputUtils.info(fileNum, String.valueOf(++filelNum));
                            OutputUtils.info(log, String.format("执行[ %s ]开始\n", item));
                            executeSql.clear();
                            content = FileUtils.readNormalFile(item.getAbsolutePath(), false);
                            boolean procedure = false;
                            boolean MultSql = false;
                            if (CollectionUtils.isNotEmpty(content)) {
                                StringBuilder sqlPart = new StringBuilder();
                                for (String sql : content) {
                                    String checkSql = sql.toLowerCase().trim();
                                    if (StringUtils.isBlank(checkSql)) {
                                        continue;
                                    }
                                    if (!procedure && !MultSql && sql.startsWith("declare")) {
                                        procedure = true;
                                        sqlPart.append(sql).append(SYMBOL_NEXT_LINE);
                                    } else if (!procedure && !MultSql && !sql.endsWith(SYMBOL_SEMICOLON)) {
                                        MultSql = true;
                                        sqlPart.append(sql).append(SYMBOL_NEXT_LINE);
                                    } else if (procedure && checkSql.equals("/")) {
                                        procedure = false;
                                        String singleSql = sqlPart.toString().trim();
                                        executeSql.add(singleSql);
                                        sqlPart.setLength(0);
                                    }  else if (MultSql && checkSql.endsWith(SYMBOL_SEMICOLON)) {
                                        MultSql = false;
                                        String singleSql = sqlPart.append(sql).append(SYMBOL_NEXT_LINE).toString().trim();
                                        executeSql.add(singleSql.substring(0, singleSql.length() - 1));
                                        sqlPart.setLength(0);
                                    } else if (procedure || MultSql) {
                                        sqlPart.append(sql).append(SYMBOL_NEXT_LINE);
                                    } else if (sql.endsWith(SYMBOL_SEMICOLON)) {
                                        procedure = false;
                                        executeSql.add(sql.substring(0, sql.length() - 1));
                                    }
                                }
                            }
                            if (CollectionUtils.isNotEmpty(executeSql)) {
                                for (String sql : executeSql) {
                                    try {
                                        OutputUtils.info(sqlNum, String.valueOf(++executeSqlNum));
                                        DatabaseUtils.executeSql(sql, encode);
                                    } catch (Exception e) {
                                        if (nextFlag) {
                                            failSql.add(item.getAbsolutePath());
                                            nextFlag = false;
                                        }
                                        String errorMsg = e.getMessage().replaceAll("[\\t\\r\\n]", SYMBOL_EMPTY);
                                        String errorSql = sql + SYMBOL_NEXT_LINE;
                                        LoggerUtils.info(e);
                                        OutputUtils.info(log, errorMsg + SYMBOL_NEXT_LINE);
                                        OutputUtils.info(log, errorSql + SYMBOL_NEXT_LINE);
                                        failSql.add(errorMsg);
                                        failSql.add(errorSql);
                                        OutputUtils.info(sqlFailNum, String.valueOf(++executeFailSqlNum));
                                    }
                                }
                            }
                            OutputUtils.info(log, String.format("执行[ %s ]结束\n", item));
                        }
                    } else {
                        OutputUtils.info(log, "选文择件夹目录不存在sql文件");
                    }
                    LoggerUtils.writeDatabaScriptLogInfo(DATABASE_SCRIPT.getCode(), failSql, logFilePath);
                } else {
                    OutputUtils.info(log, "请选择文件夹目录");
                }
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(log, e.getMessage());
            } finally {
                setProgress(1);
                try {
                    DatabaseUtils.closeConnection();
                } catch (SQLException e) {
                    LoggerUtils.info(e);
                    OutputUtils.info(log, e.getMessage());
                }
                databaseExecute.setDisable(false);
                databaseExecuteDetail.setDisable(false);
            }
        }).start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            OutputUtils.clearLog(databasePath);
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (StringUtils.isNotBlank(appConfigDto.getDatabaseScriptLocation())) {
                OutputUtils.info(databasePath, appConfigDto.getDatabaseScriptLocation());
            }
            logFilePath = FileUtils.getFilePath(String.format(PATH_LOG, DATABASE_SCRIPT.getLogFolder(), "error" + FILE_TYPE_LOG));
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        }
    }

    public List<File> getFileList(File file, List<File> pathList) {
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            if (fileList != null) {
                for (File item : fileList) {
                    getFileList(item, pathList);
                }
            }
        } else {
            String fileName = file.getName().toLowerCase();
            if (fileName.endsWith(FILE_TYPE_SQL)) {
                // 只支持oracle版本 oralce表结构修改脚本
                if (fileName.contains(".ddl.") && fileName.contains(".oracle.")) {
                    pathList.add(file);
                }
                // 普通升级脚本
                if (!fileName.contains(".ddl.")) {
                    pathList.add(file);
                }
            }
        }
        return pathList;
    }
}
