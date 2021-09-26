package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.FunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.FunctionConfig.SCRIPT_UPDATE;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/05/14
 */
public class ScriptUpdateController implements Initializable {

    @FXML
    private TextArea source;

    @FXML
    private Button submit;

    @FXML
    private ProgressIndicator schedule;

    @FXML
    private TextArea target;

    @FXML
    private RadioButton rewrite;

    @FXML
    private RadioButton append;

    @FXML
    private Label scheduleText;

    @FXML
    private TextField param;

    private double progress = 0;

    @FXML
    void executeSubmit(ActionEvent event) {
        try {
            LoggerUtils.info(String.format(MSG_USE, SCRIPT_UPDATE.getName()));
            if (!CommonUtils.checkConfig(target, FunctionConfig.SCRIPT_UPDATE.getCode())) {
                return;
            }
            boolean selectRewrite = rewrite.isSelected();
            boolean selectAppend = append.isSelected();
            if (selectRewrite == false && selectAppend == false) {
                OutputUtils.selected(rewrite, true);
                OutputUtils.selected(append, false);
            }
            boolean mode = rewrite.isSelected();
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            appConfigDto.setScriptGenerateMode(mode ? STR_1 : STR_2);
            setProgress(0);
            updateProgress();
            generateScript();
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            String mode = appConfigDto.getScriptGenerateMode();
            if (StringUtils.isBlank(mode)) {
                OutputUtils.selected(rewrite, false);
                OutputUtils.selected(append, false);
                return;
            }
            if (STR_1.equals(mode)) {
                OutputUtils.selected(rewrite, true);
                OutputUtils.selected(append, false);
                return;
            }
            if (STR_2.equals(mode)) {
                OutputUtils.selected(rewrite, false);
                OutputUtils.selected(append, true);
                return;
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    @FXML
    void selectAppend(ActionEvent event) {
        OutputUtils.selected(append, true);
        OutputUtils.selected(rewrite, false);
    }

    @FXML
    void selectRewrite(ActionEvent event) {
        OutputUtils.selected(rewrite, true);
        OutputUtils.selected(append, false);
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

    public void generateScript() {
        new Thread(() -> {
            try {
                submit.setDisable(true);
                Date date = new Date();
                OutputUtils.clearLog(target);
                String sourceScript = source.getText();
                if (StringUtils.isNotBlank(sourceScript)) {
                    Map<String, String> deleteSqlMap = new LinkedHashMap<>(16);
                    String[] items = sourceScript.split(SYMBOL_SEMICOLON);
                    String[] itemsAfter = sourceScript.replace(SYMBOL_NEXT_LINE, SYMBOL_EMPTY).split(SYMBOL_SEMICOLON);
                    AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                    for (int j = 0; j < itemsAfter.length; j++) {
                        String item = itemsAfter[j].trim();
                        if (StringUtils.isEmpty(item)) {
                            continue;
                        }
                        if (item.toLowerCase().startsWith(KEY_DELETE)) {
                            continue;
                        }
                        // 注释处理
                        if (item.startsWith(ANNOTATION_TYPE_NORMAL)) {
                            if (appConfigDto.getScriptUpdateAnnotationSkip()) {
                                item = item.replace(ANNOTATION_TYPE_NORMAL, SYMBOL_EMPTY).trim();
                            } else {
                                continue;
                            }
                        }
                        // 获取sql字段和值
                        String[] sql = item.split(KEY_VALUES);
                        if (sql.length != 2) {
                            sql = item.split(KEY_VALUES.toUpperCase());
                            if (sql.length != 2) {
                                throw new Exception("sql语句未包含或者包含多个" + KEY_VALUES + SYMBOL_NEXT_LINE + item);
                            }
                        }
                        Map<String, String> sqlInfo = new LinkedHashMap<>(16);
                        String[] columns = null;
                        String[] values = null;
                        for (int i = 0; i < sql.length; i++) {
                            String sqlItem = sql[i];
                            int indexStart = sqlItem.indexOf(SYMBOL_BRACKETS_LEFT) + 1;
                            int indexEnd = sqlItem.lastIndexOf(SYMBOL_BRACKETS_RIGHT);
                            String subSql = sqlItem.substring(indexStart, indexEnd);
                            if (i == 0) {
                                columns = subSql.split(SYMBOL_COMMA);
                            } else {
                                values = subSql.split(SYMBOL_COMMA);
                            }
                        }
                        if (columns != null && values != null) {
                            for (int i = 0; i < columns.length; i++) {
                                sqlInfo.put(columns[i].toLowerCase().trim(), values[i].trim());
                            }
                        }
                        // 获取表名称
                        String tableName = getTableName(item);
                        // 生成delete语句
                        List<LinkedHashMap<String, List<String>>> tableConfig = appConfigDto.getScriptUpdateTable();
                        outer:
                        for (LinkedHashMap<String, List<String>> table : tableConfig) {
                            Iterator<String> iterator = table.keySet().iterator();
                            while (iterator.hasNext()) {
                                String tableNameConfig = iterator.next();
                                if (tableNameConfig.contains(tableName + SYMBOL_POINT)) {
                                    StringBuilder deleteSql = new StringBuilder();
                                    deleteSql.append("delete from " + tableName + " where");
                                    List<String> cloumn = table.get(tableNameConfig);
                                    for (String cloumnItem : cloumn) {
                                        getConnect(deleteSql);
                                        if ("null".equals(sqlInfo.get(cloumnItem.toLowerCase()))) {
                                            if (!appConfigDto.getScriptUpdateSkip()) {
                                                deleteSql.append(cloumnItem.toLowerCase() + " is null");
                                            } else {
                                                deleteSql.append("1=1");
                                            }
                                        } else {
                                            deleteSql.append(cloumnItem.toLowerCase() + " = " + sqlInfo.get(cloumnItem.toLowerCase()));
                                        }
                                    }
                                    deleteSql.append(SYMBOL_SEMICOLON);
                                    String sqlKey = items[j].trim();
                                    if (StringUtils.isEmpty(deleteSqlMap.get(sqlKey))) {
                                        deleteSqlMap.put(sqlKey, deleteSql.toString());
                                    } else {
                                        deleteSqlMap.put(sqlKey, deleteSqlMap.get(sqlKey) + SYMBOL_NEXT_LINE + deleteSql);
                                    }
                                }
                            }
                        }
                    }
                    List<String> logList = new ArrayList<>(16);
                    List<String> scriptList = new ArrayList<>(16);
                    if (!MapUtils.isEmpty(deleteSqlMap)) {
                        String paramControl = param.getText().trim();
                        String paramSql = "\n from (select count(1) param_exists from tbparam where param_id = '" + paramControl + "') a where param_exists = 1";
                        // 组装sql语句
                        for (int i = 0; i < items.length; i++) {
                            String sqlKey = items[i].trim();
                            String sql = items[i].replaceAll(" values", "values").trim();
                            if (sql.equals(SYMBOL_EMPTY)) {
                                continue;
                            }
                            if (sql.toLowerCase().startsWith(KEY_DELETE)) {
                                continue;
                            }
                            // 注释处理
                            if (sql.startsWith(ANNOTATION_TYPE_NORMAL)) {
                                if (appConfigDto.getScriptUpdateAnnotationSkip()) {
                                    sql = sql.replace(ANNOTATION_TYPE_NORMAL, SYMBOL_EMPTY);
                                }
                            }
                            // 参数处理
                            if (StringUtils.isNotEmpty(paramControl)) {
                                sql = sql.replace("values(", " select ").replace("values (", " select ");
                                int index = sql.lastIndexOf(SYMBOL_BRACKETS_RIGHT);
                                sql = sql.substring(0, index) + paramSql;
                            }
                            if (sql.toLowerCase().startsWith("insert")) {
                                OutputUtils.info(target, deleteSqlMap.get(sqlKey) + SYMBOL_NEXT_LINE);
                                logList.add(deleteSqlMap.get(sqlKey));
                                scriptList.add(deleteSqlMap.get(sqlKey));
                            }
                            OutputUtils.info(target, sql + SYMBOL_SEMICOLON + SYMBOL_NEXT_LINE);
                            logList.add(sql.replace(SYMBOL_NEXT_LINE, SYMBOL_SPACE) + SYMBOL_SEMICOLON);
                            scriptList.add(sql + SYMBOL_SEMICOLON + SYMBOL_NEXT_LINE);
                        }
                    } else {
                        OutputUtils.clearLog(target);
                        OutputUtils.info(target, "未匹配到升级脚本生成规则");
                    }
                    // 写日志文件
                    LoggerUtils.writeScriptUpdateInfo(date, logList);
                    if (appConfigDto.getScriptUpdateGenerateFile()) {
                        String path = new URL("file:" + appConfigDto.getScriptUpdateGeneratePath() + "/script.sql").getFile();
                        FileUtils.writeFile(path, scriptList, STR_2.equals(appConfigDto.getScriptGenerateMode()));
                    }
                }
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.clearLog(target);
                OutputUtils.info(target, e.toString());
            } finally {
                submit.setDisable(false);
            }
            setProgress(1);
        }).start();
    }

    private static String getTableName(String sql) {
        int tableNameStartIndex = sql.toLowerCase().indexOf(KEY_INSERT_INTO);
        int tableNameStartEnd = sql.toLowerCase().indexOf(SYMBOL_BRACKETS_LEFT);
        return sql.substring(tableNameStartIndex + 4, tableNameStartEnd).toLowerCase().trim();
    }

    private static StringBuilder getConnect(StringBuilder sql) {
        if (sql.toString().endsWith("where")) {
            return sql.append(" ");
        } else {
            return sql.append(" and ");
        }
    }
}
