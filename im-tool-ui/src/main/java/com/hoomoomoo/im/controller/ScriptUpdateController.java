package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;

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

    private double progress = 0;

    @FXML
    void executeSubmit(ActionEvent event) {
        setProgress(0);
        updateProgress();
        generateScript();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
                    e.printStackTrace();
                }
            }
        }).start();
    }

    synchronized private void setProgress(double value) {
        try {
            progress = value;
            Platform.runLater(() -> {
                schedule.setProgress(progress);
            });
            schedule.requestFocus();
        } catch (Exception e) {
            LoggerUtils.info(e.toString());
        }
    }

    public void generateScript() {
        new Thread(() -> {
            try {
                submit.setDisable(true);
                Date date = new Date();
                OutputUtils.clearLog(target);
                String sourceScript = source.getText();
                List<String> logList = new ArrayList<>(16);
                if (StringUtils.isNotBlank(sourceScript)) {
                    List<String> deleteSqlList = new ArrayList<>(16);
                    String[] items = sourceScript.split(STR_SYMBOL_SEMICOLON);
                    String[] itemsAfter = sourceScript.replace(STR_SYMBOL_NEXT_LINE, STR_EMPTY).split(STR_SYMBOL_SEMICOLON);
                    AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                    for (String item : itemsAfter) {
                        // 获取sql字段和值
                        String[] sql = item.split(STR_NAME_VALUES);
                        if (sql.length != 2) {
                            sql = item.split(STR_NAME_VALUES.toUpperCase());
                            if (sql.length != 2) {
                                throw new Exception("sql语句未包含或者包含多个" + STR_NAME_VALUES + STR_SYMBOL_NEXT_LINE + item);
                            }
                        }
                        Map<String, String> sqlInfo = new LinkedHashMap<>(16);
                        String[] columns = null;
                        String[] values = null;
                        for (int i = 0; i < sql.length; i++) {
                            String sqlItem = sql[i];
                            int indexStart = sqlItem.indexOf(STR_SYMBOL_BRACKETS_LEFT) + 1;
                            int indexEnd = sqlItem.lastIndexOf(STR_SYMBOL_BRACKETS_RIGHT);
                            String subSql = sqlItem.substring(indexStart, indexEnd);
                            if (i == 0) {
                                columns = subSql.split(STR_SYMBOL_COMMA);
                            } else {
                                values = subSql.split(STR_SYMBOL_COMMA);
                            }
                        }
                        if (columns != null && values != null) {
                            for (int i = 0; i < columns.length; i++) {
                                sqlInfo.put(columns[i].toLowerCase().trim(), values[i].trim());
                            }
                        }
                        // 生成delete语句
                        List<LinkedHashMap<String, List<String>>> tableConfig = appConfigDto.getScriptUpdateTable();
                        outer:
                        for (LinkedHashMap<String, List<String>> table : tableConfig) {
                            Iterator<String> iterator = table.keySet().iterator();
                            while (iterator.hasNext()) {
                                String tableName = iterator.next();
                                if (item.contains(tableName)) {
                                    StringBuilder deleteSql = new StringBuilder();
                                    deleteSql.append("delete from " + tableName + " where");
                                    List<String> cloumn = table.get(tableName);
                                    for (String cloumnItem : cloumn) {
                                        getConnect(deleteSql);
                                        if ("null".equals(sqlInfo.get(cloumnItem.toLowerCase()))) {
                                            if (!appConfigDto.getScriptUpdateSkip()) {
                                                deleteSql.append(cloumnItem.toLowerCase() + " is null");
                                            } else {
                                                deleteSql.append("1=1");
                                            }
                                        } else {
                                            deleteSql.append(cloumnItem.toLowerCase() + "=" + sqlInfo.get(cloumnItem.toLowerCase()));
                                        }
                                    }
                                    deleteSql.append(STR_SYMBOL_SEMICOLON);
                                    deleteSqlList.add(deleteSql.toString());
                                    break outer;
                                }
                            }
                        }
                    }
                    if (CollectionUtils.isNotEmpty(deleteSqlList)) {
                        // 组装sql语句
                        for (int i = 0; i < items.length; i++) {
                            String sql = items[i].trim();
                            if (sql.equals(STR_EMPTY)) {
                                continue;
                            }
                            if (sql.toLowerCase().startsWith("insert")) {
                                OutputUtils.info(target, deleteSqlList.get(i) + STR_SYMBOL_NEXT_LINE);
                                logList.add(deleteSqlList.get(i));
                            }
                            OutputUtils.info(target, sql + STR_SYMBOL_SEMICOLON + STR_SYMBOL_NEXT_LINE);
                            logList.add(sql.replace(STR_SYMBOL_NEXT_LINE, STR_SPACE) + STR_SYMBOL_SEMICOLON);
                        }
                    } else {
                        OutputUtils.clearLog(target);
                        OutputUtils.info(target, "未匹配到升级脚本生成规则");
                    }
                    // 写日志文件
                    LoggerUtils.writeScriptUpdateInfo(date, logList);
                }
            } catch (Exception e) {
                e.printStackTrace();
                OutputUtils.clearLog(target);
                OutputUtils.info(target, e.toString());
            } finally {
                submit.setDisable(false);
            }
            setProgress(1);
        }).start();
    }

    private static StringBuilder getConnect(StringBuilder sql) {
        if (sql.toString().endsWith("where")) {
            return sql.append(" ");
        } else {
            return sql.append(" and ");
        }
    }
}
