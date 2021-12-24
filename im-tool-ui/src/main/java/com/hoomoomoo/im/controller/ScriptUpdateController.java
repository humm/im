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
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.*;
import java.util.List;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.FunctionConfig.SCRIPT_UPDATE;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/05/14
 */
public class ScriptUpdateController extends BaseController implements Initializable {

    @FXML
    private TextArea source;

    @FXML
    private Button submit;

    @FXML
    private Button copy;

    @FXML
    private TextArea target;

    @FXML
    private RadioButton rewrite;

    @FXML
    private RadioButton append;

    @FXML
    private TextField param;

    @FXML
    void executeCopy(ActionEvent event) {
        schedule.requestFocus();
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(target.getText()), null);
    }

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
            if (!appConfigDto.getScriptUpdateGenerateFile()) {
                rewrite.setDisable(true);
                append.setDisable(true);
                return;
            }
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

    public void generateScript() {
        new Thread(() -> {
            try {
                submit.setDisable(true);
                Date date = new Date();
                OutputUtils.clearLog(target);
                String sourceScript = source.getText();
                if (StringUtils.isNotBlank(sourceScript)) {
                    Map<String, String> deleteSqlMap = new LinkedHashMap<>(16);
                    String[] source = sourceScript.split(SYMBOL_NEXT_LINE);
                    StringBuilder itemsTemp = new StringBuilder();
                    for (int k=0; k<source.length; k++) {
                        String item = source[k];
                        if (StringUtils.isEmpty(item) || item.startsWith(ANNOTATION_TYPE_NORMAL) || item.toLowerCase().startsWith(KEY_DELETE)) {
                            continue;
                        }
                        itemsTemp.append(item.trim() + SYMBOL_NEXT_LINE);
                    }
                    int indexLast = itemsTemp.toString().lastIndexOf(SYMBOL_NEXT_LINE);
                    List<String> items = Arrays.asList(itemsTemp.toString().substring(0, indexLast).split(SYMBOL_SEMICOLON));
                    AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                    for (int j = 0; j < items.size(); j++) {
                        String item = items.get(j).replace(SYMBOL_NEXT_LINE, SYMBOL_EMPTY).trim();

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
                                    String sqlKey = items.get(j).trim();
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
                        for (int i = 0; i < items.size(); i++) {
                            String sqlKey = items.get(i);
                            String sql = items.get(i).replaceAll(" values", "values").replaceAll(" insert", "insert").trim();
                            if (sql.equals(SYMBOL_EMPTY)) {
                                continue;
                            }
                            if (sql.toLowerCase().startsWith(KEY_DELETE)) {
                                continue;
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
