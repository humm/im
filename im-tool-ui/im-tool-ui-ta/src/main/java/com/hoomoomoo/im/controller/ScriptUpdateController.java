package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.*;
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
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.SCRIPT_UPDATE;

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
    private RadioButton onlyDelete;

    @FXML
    private RadioButton all;

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
            LoggerUtils.info(String.format(BaseConst.MSG_USE, SCRIPT_UPDATE.getName()));
            if (!TaCommonUtil.checkConfig(target, SCRIPT_UPDATE.getCode())) {
                return;
            }
            if (rewrite.isSelected() == false && append.isSelected() == false) {
                selectRewrite(null);
            }
            if (onlyDelete.isSelected() == false && all.isSelected() == false) {
                selectAll(null);
            }
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            appConfigDto.setScriptUpdateGenerateMode(rewrite.isSelected() ? STR_2 : STR_1);
            appConfigDto.setScriptUpdateGenerateType(all.isSelected() ? STR_2 : STR_1);
            setProgress(0);
            updateProgress();
            generateScript(appConfigDto);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            String mode = appConfigDto.getScriptUpdateGenerateMode();
            String type = appConfigDto.getScriptUpdateGenerateType();
            if (!appConfigDto.getScriptUpdateGenerateFile()) {
                rewrite.setDisable(true);
                append.setDisable(true);
            }
            if (StringUtils.isBlank(mode)) {
                selectRewrite(null);
            } else if (STR_1.equals(mode)) {
                selectAppend(null);
            } else if (STR_2.equals(mode)) {
                selectRewrite(null);
            }
            if (StringUtils.isBlank(type)) {
                selectAll(null);
            } else if (STR_1.equals(type)) {
                selectOnlyDelete(null);
            } else if (STR_2.equals(type)) {
                selectAll(null);
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

    @FXML
    void selectOnlyDelete(ActionEvent event) {
        OutputUtils.selected(onlyDelete, true);
        OutputUtils.selected(all, false);
    }

    @FXML
    void selectAll(ActionEvent event) {
        OutputUtils.selected(onlyDelete, false);
        OutputUtils.selected(all, true);
    }

    public void generateScript(AppConfigDto appConfigDto) {
        new Thread(() -> {
            try {
                submit.setDisable(true);
                OutputUtils.clearLog(target);
                String sourceScript = source.getText();
                generatesql(appConfigDto, sourceScript);
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.clearLog(target);
                OutputUtils.info(target, e.getMessage());
            } finally {
                submit.setDisable(false);
                setProgress(1);
            }
        }).start();
    }

    public List<String> generatesql (AppConfigDto appConfigDto, String sourceScript) throws Exception {
        List<String> result = new ArrayList<>();
        Date date = new Date();
        if (StringUtils.isNotBlank(sourceScript)) {
            Map<String, String> deleteSqlMap = new LinkedHashMap<>(16);
            String[] source = sourceScript.split(SYMBOL_NEXT_LINE);
            StringBuilder itemsTemp = new StringBuilder();
            for (int k=0; k<source.length; k++) {
                String item = source[k];
                if (StringUtils.isEmpty(item)  || item.toLowerCase().startsWith(BaseConst.KEY_DELETE)) {
                    continue;
                }
                if (item.startsWith(BaseConst.ANNOTATION_TYPE_NORMAL)) {
                    boolean isContinue = true;
                    String scriptUpdateIgnoreSkip = appConfigDto.getScriptUpdateIgnoreSkip();
                    if (StringUtils.isNotBlank(scriptUpdateIgnoreSkip)) {
                        String[] items = scriptUpdateIgnoreSkip.split(BaseConst.SYMBOL_COMMA);
                        inner: for (String skip : items) {
                            if (item.toLowerCase().indexOf(skip.toLowerCase()) != -1) {
                                item = item.replaceAll(BaseConst.ANNOTATION_TYPE_NORMAL, BaseConst.SYMBOL_EMPTY);
                                isContinue = false;
                                break inner;
                            }
                        }
                    }
                    if (isContinue) {
                        continue;
                    }
                }
                if (item.trim().toLowerCase().startsWith("insert") || item.toLowerCase().trim().startsWith("values") || item.trim().toLowerCase().startsWith(BaseConst.ANNOTATION_TYPE_NORMAL)) {
                    item = item.trim();
                }
                itemsTemp.append(item + SYMBOL_NEXT_LINE);
            }
            int indexLast = itemsTemp.toString().lastIndexOf(SYMBOL_NEXT_LINE);
            if (indexLast == -1) {
                setProgress(1);
                return result;
            }
            List<String> items = Arrays.asList(itemsTemp.substring(0, indexLast).split(BaseConst.SYMBOL_SEMICOLON));
            boolean existRule;
            for (int j = 0; j < items.size(); j++) {
                existRule = false;
                String item = items.get(j).replace(SYMBOL_NEXT_LINE, BaseConst.SYMBOL_EMPTY).trim();
                // 获取sql字段和值
                String[] sql = item.split(BaseConst.KEY_VALUES);
                if (sql.length != 2) {
                    sql = item.split(BaseConst.KEY_VALUES.toUpperCase());
                    if (sql.length != 2) {
                        throw new Exception("sql语句未包含或者包含多个" + BaseConst.KEY_VALUES + SYMBOL_NEXT_LINE + item);
                    }
                }
                Map<String, String> sqlInfo = new LinkedHashMap<>(16);
                String[] columns = null;
                String[] values = null;
                for (int i = 0; i < sql.length; i++) {
                    String sqlItem = sql[i];
                    int indexStart = sqlItem.indexOf(BaseConst.SYMBOL_BRACKETS_LEFT) + 1;
                    int indexEnd = sqlItem.lastIndexOf(BaseConst.SYMBOL_BRACKETS_RIGHT);
                    String subSql = sqlItem.substring(indexStart, indexEnd);
                    if (i == 0) {
                        columns = subSql.split(BaseConst.SYMBOL_COMMA);
                    } else {
                        values = subSql.split(BaseConst.SYMBOL_COMMA);
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
                        if (tableNameConfig.contains(tableName + BaseConst.SYMBOL_POINT)) {
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
                            deleteSql.append(BaseConst.SYMBOL_SEMICOLON);
                            String sqlKey = items.get(j).trim();
                            if (StringUtils.isEmpty(deleteSqlMap.get(sqlKey))) {
                                deleteSqlMap.put(sqlKey, deleteSql.toString());
                            } else {
                                deleteSqlMap.put(sqlKey, deleteSqlMap.get(sqlKey) + SYMBOL_NEXT_LINE + deleteSql);
                            }
                            existRule = true;
                            break;
                        }
                    }
                }
                if (!existRule) {
                    if (target != null) {
                        OutputUtils.info(target, "【" + tableName + "】未配置生成规则");
                    }
                    throw new Exception("【" + tableName + "】未配置生成规则");
                }
            }
            List<String> logList = new ArrayList<>(16);
            List<String> scriptList = new ArrayList<>(16);
            if (!MapUtils.isEmpty(deleteSqlMap)) {
                String paramControl = SYMBOL_EMPTY;
                if (param != null) {
                    paramControl = param.getText().trim();
                }
                String paramSql = "\n from (select count(1) param_exists from tbparam where param_id = '" + paramControl + "') a where param_exists = 1";
                if (paramControl.startsWith("F_C")) {
                    String[] param = paramControl.split(SYMBOL_COMMA);
                    if (param.length == 2) {
                        paramSql = "\n from (select count(1) param_exists from tbdict where hs_key = '" + param[0] + "' and val = '" + param[1] + "') a where param_exists = 1";
                    } else {
                        paramSql = "\n from (select count(1) param_exists from tbdict where hs_key = '" + paramControl + "') a where param_exists = 1";
                    }
                }
                // 组装sql语句
                for (int i = 0; i < items.size(); i++) {
                    String sqlKey = items.get(i).trim();
                    String sql = items.get(i).replaceAll(" values", "values").replaceAll(" insert", "insert").trim();
                    if (sql.equals(BaseConst.SYMBOL_EMPTY)) {
                        continue;
                    }
                    if (sql.toLowerCase().startsWith(BaseConst.KEY_DELETE)) {
                        continue;
                    }
                    // 参数处理
                    if (StringUtils.isNotEmpty(paramControl)) {
                        sql = sql.replace("values(", " select ").replace("values (", " select ").replace("VALUES(", " select ").replace("VALUES (", " select ");
                        int index = sql.lastIndexOf(BaseConst.SYMBOL_BRACKETS_RIGHT);
                        sql = sql.substring(0, index) + paramSql;
                    }
                    String updateSql = generateUpdate(sqlKey);
                    if (sql.toLowerCase().startsWith("insert")) {
                        if (target != null) {
                            OutputUtils.info(target, deleteSqlMap.get(sqlKey) + SYMBOL_NEXT_LINE);
                        } else {
                            result.add(deleteSqlMap.get(sqlKey) + SYMBOL_NEXT_LINE);
                        }
                        logList.add(deleteSqlMap.get(sqlKey));
                        scriptList.add(deleteSqlMap.get(sqlKey));
                    }
                    if (STR_2.equals(appConfigDto.getScriptUpdateGenerateType())) {
                        if (target != null) {
                            OutputUtils.info(target, sql + BaseConst.SYMBOL_SEMICOLON + SYMBOL_NEXT_LINE);
                        } else {
                            result.add(sql + BaseConst.SYMBOL_SEMICOLON + SYMBOL_NEXT_LINE);
                        }
                        logList.add(sql.replace(SYMBOL_NEXT_LINE, BaseConst.SYMBOL_SPACE) + BaseConst.SYMBOL_SEMICOLON);
                        scriptList.add(sql + BaseConst.SYMBOL_SEMICOLON + SYMBOL_NEXT_LINE);
                        if (StringUtils.isNotEmpty(updateSql)) {
                            if (target != null) {
                                OutputUtils.info(target, updateSql + SYMBOL_NEXT_LINE);
                            } else {
                                result.add(updateSql + SYMBOL_NEXT_LINE);
                            }
                            logList.add(updateSql);
                            scriptList.add(updateSql);
                        }
                    }
                }
                if (STR_1.equals(appConfigDto.getScriptUpdateGenerateType())) {
                    logList.add(SYMBOL_EMPTY);
                    scriptList.add(SYMBOL_EMPTY);
                }
            } else {
                if (target != null) {
                    OutputUtils.clearLog(target);
                    OutputUtils.info(target, "未配置生成规则");
                }
            }
            // 写日志文件
            LoggerUtils.writeScriptUpdateInfo(date, logList);
            if (appConfigDto.getScriptUpdateGenerateFile()) {
                String path = new URL("file:" + appConfigDto.getScriptUpdateGeneratePath() + "/script.sql").getFile();
                FileUtils.writeFile(path, scriptList, STR_1.equals(appConfigDto.getScriptUpdateGenerateMode()));
            }
        }
        return result;
    }

    private static String generateUpdate(String item) throws Exception{
        StringBuilder updateSql = new StringBuilder();
        if (item.toLowerCase().contains("tbmenucondition")) {
            updateSql.append("update tbmenuconditionuser set ");
            item = item.replaceAll("\\s+", SYMBOL_EMPTY);
            String[] sql = item.split(BaseConst.KEY_VALUES);
            if (sql.length != 2) {
                sql = item.split(BaseConst.KEY_VALUES.toUpperCase());
                if (sql.length != 2) {
                    throw new Exception("sql语句未包含或者包含多个" + BaseConst.KEY_VALUES + SYMBOL_NEXT_LINE + item);
                }
            }
            String[] column = sql[0].substring(sql[0].indexOf("(") + 1, sql[0].indexOf(")")).split(",");
            String[] value = sql[1].substring(sql[1].indexOf("(") + 1, sql[1].indexOf(")")).split("',");
            for (int i=0; i<column.length; i++) {
                if (i == 0 || i == 2 || i == 3) {
                    continue;
                }
                if (i != 1) {
                    updateSql.append(", ");
                }
                updateSql.append(column[i] + " = " + (value[i].length() == 1 ? value[i] + SYMBOL_SPACE : value[i]));
                if (i != column.length - 1) {
                    updateSql.append("'");
                }
            }
            updateSql.append(" where " + column[0] + " = " + value[0] + "'");
            updateSql.append(" and " + column[2] + " = " + value[2] + "'");
            updateSql.append(" and " + column[3] + " = " + value[3] + "'");
            updateSql.append(";");
        }
        return updateSql.toString();
    }

    private static String getTableName(String sql) {
        int tableNameStartIndex = sql.toLowerCase().indexOf(BaseConst.KEY_INSERT_INTO);
        int tableNameStartEnd = sql.toLowerCase().indexOf(BaseConst.SYMBOL_BRACKETS_LEFT);
        return sql.substring(tableNameStartIndex + 4, tableNameStartEnd).toLowerCase().trim();
    }

    private static StringBuilder getConnect(StringBuilder sql) {
        if (sql.toString().endsWith("where")) {
            return sql.append(" ");
        } else {
            return sql.append(" and ");
        }
    }

    public static void main(String[] args) throws Exception{
        String item = "insert into tbmenucondition (menu_code,component_kind,condition_kind,element_code,element_name,data_dict,check_format,default_value,required_flag,readonly_flag,visable,data_width,order_no,exp_flag,sort_no)\n" +
                "values('fundRequestConfirmQuery', 'A', '0', 'dataSource', '数据源', '{\"dict\":\"F_C30022\"," +
                "\"clearable\":\"0\"}', ' ', '0', '0', '0', '1', '0', '0', '1', '1' );\n";
        if (item.toLowerCase().contains("tbmenucondition")) {
            item = item.replaceAll("\\s+", "");
            String[] sql = item.split(BaseConst.KEY_VALUES);
            if (sql.length != 2) {
                sql = item.split(BaseConst.KEY_VALUES.toUpperCase());
                if (sql.length != 2) {
                    throw new Exception("sql语句未包含或者包含多个" + BaseConst.KEY_VALUES + SYMBOL_NEXT_LINE + item);
                }
            }
            String[] column = sql[0].substring(sql[0].indexOf("(") + 1, sql[0].indexOf(")")).split(",");
            String[] value = sql[1].substring(sql[1].indexOf("(") + 1, sql[1].indexOf(")")).split("',");
            StringBuilder updateSql = new StringBuilder("update tbmenuconditionuser set ");
            for (int i=0; i<column.length; i++) {
                if (i == 0 || i == 2 || i == 3) {
                    continue;
                }
                if (i != 1) {
                    updateSql.append(", ");
                }
                updateSql.append(column[i] + " = " + (StringUtils.isEmpty(value[i]) ? SYMBOL_SPACE : value[i]));
                if (i != column.length - 1) {
                    updateSql.append("'");
                }
            }
            updateSql.append(" where " + column[0] + " = " + value[0] + "'");
            updateSql.append(" and " + column[2] + " = " + value[2] + "'");
            updateSql.append(" and " + column[3] + " = " + value[3] + "'");
            updateSql.append(";");
        }
    }
}
