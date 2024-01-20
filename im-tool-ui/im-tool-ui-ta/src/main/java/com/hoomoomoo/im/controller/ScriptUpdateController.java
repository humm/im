package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.MenuDto;
import com.hoomoomoo.im.extend.MenuUpdateSql;
import com.hoomoomoo.im.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private RadioButton update;

    @FXML
    private RadioButton menuYes;

    @FXML
    private RadioButton menuNo;

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
            if (!TaCommonUtils.checkConfig(target, SCRIPT_UPDATE.getCode())) {
                return;
            }
            if (!rewrite.isSelected() && !append.isSelected()) {
                selectRewrite(null);
            }
            if (!onlyDelete.isSelected() && !all.isSelected() && !update.isSelected()) {
                selectAll(null);
            }
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            if (onlyDelete.isSelected()) {
                appConfigDto.setScriptUpdateGenerateType(STR_1);
            } else if (all.isSelected()) {
                appConfigDto.setScriptUpdateGenerateType(STR_2);
            } else {
                appConfigDto.setScriptUpdateGenerateType(STR_3);
            }
            appConfigDto.setScriptUpdateGenerateMode(rewrite.isSelected() ? STR_2 : STR_1);
            appConfigDto.setScriptUpdateGenerateUed(menuYes.isSelected() ? STR_1 : STR_2);
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
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            String mode = appConfigDto.getScriptUpdateGenerateMode();
            String type = appConfigDto.getScriptUpdateGenerateType();
            String ued = appConfigDto.getScriptUpdateGenerateUed();
            if (!appConfigDto.getScriptUpdateGenerateFile()) {
                rewrite.setDisable(true);
                append.setDisable(true);
            }
            if (StringUtils.isBlank(mode) || STR_2.equals(mode)) {
                selectRewrite(null);
            } else if (STR_1.equals(mode)) {
                selectAppend(null);
            }

            if (StringUtils.isBlank(type) || STR_2.equals(type)) {
                selectAll(null);
            } else if (STR_1.equals(type)) {
                selectOnlyDelete(null);
            } else if (STR_3.equals(type)) {
                selectUpdate(null);
            }

            if (StringUtils.isBlank(ued) || STR_1.equals(ued)) {
                selectMenuYes(null);
            } else if (STR_2.equals(ued)) {
                selectMenuNo(null);
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
        OutputUtils.selected(update, false);
    }

    @FXML
    void selectAll(ActionEvent event) {
        OutputUtils.selected(all, true);
        OutputUtils.selected(onlyDelete, false);
        OutputUtils.selected(update, false);
    }

    @FXML
    void selectUpdate(ActionEvent event) {
        OutputUtils.selected(update, true);
        OutputUtils.selected(all, false);
        OutputUtils.selected(onlyDelete, false);
    }

    @FXML
    void selectMenuYes(ActionEvent event) {
        OutputUtils.selected(menuYes, true);
        OutputUtils.selected(menuNo, false);
    }

    @FXML
    void selectMenuNo(ActionEvent event) {
        OutputUtils.selected(menuYes, false);
        OutputUtils.selected(menuNo, true);
    }

    @FXML
    void updateUed(ActionEvent event) throws Exception {
        String sourceScript = source.getText();
        if (StringUtils.isBlank(sourceScript)) {
            return;
        }
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String[] source = sourceScript.split(STR_NEXT_LINE);
        List<String> content = new ArrayList<>();
        for (String item : source) {
            content.add(item);
        }
        List<String> res = MenuUpdateSql.getUpdateSql(appConfigDto, content);
        for (String item : res) {
            OutputUtils.info(target, item + STR_NEXT_LINE);
        }
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
                OutputUtils.info(target, e.toString());
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
            String[] source = sourceScript.split(STR_NEXT_LINE);
            StringBuilder itemsTemp = new StringBuilder();
            for (int k=0; k<source.length; k++) {
                String item = source[k];
                if (StringUtils.isBlank(item)  || item.toLowerCase().startsWith(BaseConst.KEY_DELETE)) {
                    continue;
                }
                if (item.startsWith(BaseConst.ANNOTATION_TYPE_NORMAL)) {
                    boolean isContinue = true;
                    String scriptUpdateIgnoreSkip = appConfigDto.getScriptUpdateIgnoreSkip();
                    if (StringUtils.isNotBlank(scriptUpdateIgnoreSkip)) {
                        String[] items = scriptUpdateIgnoreSkip.split(BaseConst.STR_COMMA);
                        inner: for (String skip : items) {
                            if (item.toLowerCase().indexOf(skip.toLowerCase()) != -1) {
                                item = item.replaceAll(BaseConst.ANNOTATION_TYPE_NORMAL, BaseConst.STR_BLANK);
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
                itemsTemp.append(item + STR_NEXT_LINE);
            }
            int indexLast = itemsTemp.toString().lastIndexOf(STR_NEXT_LINE);
            if (indexLast == -1) {
                setProgress(1);
                return result;
            }
            List<String> items = Arrays.asList(itemsTemp.substring(0, indexLast).split(BaseConst.STR_SEMICOLON));
            boolean existRule;
            for (int j = 0; j < items.size(); j++) {
                existRule = false;
                String item = items.get(j).replace(STR_NEXT_LINE, BaseConst.STR_BLANK).trim();
                // 获取sql字段和值
                String[] sql = item.split(BaseConst.KEY_VALUES);
                if (sql.length != 2) {
                    sql = item.split(BaseConst.KEY_VALUES.toUpperCase());
                    if (sql.length != 2) {
                        throw new Exception("sql语句未包含或者包含多个" + BaseConst.KEY_VALUES + STR_NEXT_LINE + item);
                    }
                }
                Map<String, String> sqlInfo = new LinkedHashMap<>(16);
                String[] columns = null;
                String[] values = null;
                for (int i = 0; i < sql.length; i++) {
                    String sqlItem = sql[i];
                    int indexStart = sqlItem.indexOf(BaseConst.STR_BRACKETS_LEFT) + 1;
                    int indexEnd = sqlItem.lastIndexOf(BaseConst.STR_BRACKETS_RIGHT);
                    String subSql = sqlItem.substring(indexStart, indexEnd);
                    if (i == 0) {
                        columns = subSql.split(BaseConst.STR_COMMA);
                    } else {
                        values = subSql.split(BaseConst.STR_COMMA);
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
                        if (tableNameConfig.contains(tableName + BaseConst.STR_POINT)) {
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
                            deleteSql.append(BaseConst.STR_SEMICOLON);
                            String sqlKey = items.get(j).trim();
                            if (StringUtils.isEmpty(deleteSqlMap.get(sqlKey))) {
                                deleteSqlMap.put(sqlKey, deleteSql.toString());
                            } else {
                                deleteSqlMap.put(sqlKey, deleteSqlMap.get(sqlKey) + STR_NEXT_LINE + deleteSql);
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
                String paramControl = STR_BLANK;
                if (param != null) {
                    paramControl = param.getText().trim();
                }
                String paramSql = "\n from (select count(1) param_exists from tbparam where param_id = '" + paramControl + "') a where param_exists = 1";
                if (paramControl.startsWith("F_C")) {
                    String[] param = paramControl.split(STR_COMMA);
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
                    if (sql.equals(BaseConst.STR_BLANK)) {
                        continue;
                    }
                    if (sql.toLowerCase().startsWith(BaseConst.KEY_DELETE)) {
                        continue;
                    }
                    // 参数处理
                    if (StringUtils.isNotEmpty(paramControl)) {
                        sql = sql.replace("values(", " select ").replace("values (", " select ").replace("VALUES(", " select ").replace("VALUES (", " select ");
                        int index = sql.lastIndexOf(BaseConst.STR_BRACKETS_RIGHT);
                        sql = sql.substring(0, index) + paramSql;
                    }
                    String updateSql = generateUpdate(sqlKey, deleteSqlMap.get(sqlKey), true);
                    if (sql.toLowerCase().startsWith("insert") && !STR_3.equals(appConfigDto.getScriptUpdateGenerateType())) {
                        if (target != null) {
                            OutputUtils.info(target, deleteSqlMap.get(sqlKey) + STR_NEXT_LINE);
                        } else {
                            result.add(deleteSqlMap.get(sqlKey) + STR_NEXT_LINE);
                        }
                        logList.add(deleteSqlMap.get(sqlKey));
                        scriptList.add(deleteSqlMap.get(sqlKey));
                    }
                    if (STR_2.equals(appConfigDto.getScriptUpdateGenerateType())) {
                        if (target != null) {
                            OutputUtils.info(target, sql + BaseConst.STR_SEMICOLON + STR_NEXT_LINE);
                        } else {
                            result.add(sql + BaseConst.STR_SEMICOLON + STR_NEXT_LINE);
                        }
                        logList.add(sql.replace(STR_NEXT_LINE, BaseConst.STR_SPACE) + BaseConst.STR_SEMICOLON);
                        scriptList.add(sql + BaseConst.STR_SEMICOLON + STR_NEXT_LINE);
                        if (StringUtils.isNotEmpty(updateSql)) {
                            if (target != null) {
                                OutputUtils.info(target, updateSql + STR_NEXT_LINE);
                            } else {
                                result.add(updateSql + STR_NEXT_LINE);
                            }
                            logList.add(updateSql);
                            scriptList.add(updateSql);
                        }
                    } else if (STR_3.equals(appConfigDto.getScriptUpdateGenerateType())) {
                        String ele = generateUpdate(sqlKey, deleteSqlMap.get(sqlKey), false);
                        if (target != null) {
                            OutputUtils.info(target, ele + STR_NEXT_LINE_2);
                        } else {
                            result.add(ele + STR_NEXT_LINE);
                        }
                        logList.add(deleteSqlMap.get(ele));
                        scriptList.add(deleteSqlMap.get(ele));
                    }
                }
                if (STR_1.equals(appConfigDto.getScriptUpdateGenerateType())) {
                    logList.add(STR_BLANK);
                    scriptList.add(STR_BLANK);
                }
            } else {
                if (target != null) {
                    OutputUtils.clearLog(target);
                    OutputUtils.info(target, "未配置生成规则");
                }
            }


            if (STR_1.equals(appConfigDto.getScriptUpdateGenerateUed())) {
                if (CollectionUtils.isNotEmpty(logList)) {
                    String[] logsStr = StringUtils.join(logList, STR_BLANK).split(STR_SEMICOLON);
                    List<String> menuSql = new ArrayList<>();
                    for (String item : logsStr) {
                        if (item.toLowerCase().contains("tsys_menu") && !item.toLowerCase().contains("tsys_menu_std")) {
                            menuSql.add(item);
                        }
                    }
                    if (CollectionUtils.isNotEmpty(menuSql)) {
                        if (target != null) {
                            OutputUtils.info(target, STR_NEXT_LINE);
                        } else {
                            result.add(STR_NEXT_LINE);
                        }
                        for (String sql : menuSql) {
                            String menuStd = changeMenuToMenuStd(sql);
                            if (target != null) {
                                OutputUtils.info(target, menuStd + BaseConst.STR_SEMICOLON + STR_NEXT_LINE);
                            } else {
                                result.add(menuStd + BaseConst.STR_SEMICOLON + STR_NEXT_LINE);
                            }
                            logList.add(menuStd);
                            scriptList.add(menuStd);
                        }
                    }
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

    private String changeMenuToMenuStd(String sql) throws Exception {
        String menuStd = STR_BLANK;
        sql = sql.replace("tsys_menu", "tsys_menu_std");
        if (sql.trim().toLowerCase().startsWith("delete")) {
            return sql;
        }
        if (!sql.trim().toLowerCase().startsWith("insert")) {
            return menuStd;
        }
        MenuDto menuDto = new MenuDto();
        int startIndex = sql.lastIndexOf(STR_BRACKETS_LEFT);
        int endIndex = sql.lastIndexOf(STR_BRACKETS_RIGHT);
        String paramControl = CommonUtils.getComponentValue(param);
        String[] elements;
        if (StringUtils.isNotBlank(paramControl)) {
            startIndex = sql.indexOf("select") + "select".length();
            endIndex = sql.indexOf("from");
        }
        if (startIndex != -1 && endIndex != -1) {
            elements = sql.substring(startIndex + 1, endIndex).split(STR_COMMA);
        } else {
            throw new Exception("菜单【" + sql + "】数据格式错误");
        }
        if (elements.length != 16) {
            throw new Exception("菜单【" + sql + "】数据格式错误");
        }
        menuDto.setMenuCode(elements[0]);
        menuDto.setKindCode(elements[1]);
        menuDto.setTransCode(elements[2]);
        menuDto.setSubTransCode(elements[3]);
        menuDto.setMenuName(elements[4]);
        menuDto.setMenuArg(elements[5]);
        menuDto.setMenuIcon(elements[6]);
        menuDto.setWindowType(elements[7]);
        menuDto.setTip(elements[8]);
        menuDto.setHotKey(elements[9]);
        menuDto.setParentCode(elements[10]);
        menuDto.setOrderNo(elements[11]);
        menuDto.setOpenFlag(elements[12]);
        menuDto.setTreeIdx(elements[13]);
        menuDto.setRemark(elements[14]);
        menuDto.setWindowModel(elements[15]);
        String menuCode = menuDto.getMenuCode().trim();
        String parentCode = menuDto.getParentCode().trim();
        String treeIdx = menuDto.getTreeIdx().trim();
        String transCode = menuDto.getTransCode().trim();
        String kindCode = menuDto.getKindCode().trim();
        if ("'fundsysinfo','fundReport'".contains(menuCode)) {
            return menuStd;
        }
        menuDto.setKindCode("'console-bizframe-vue'");
        if ("'menu'".equals(transCode)) {
            menuDto.setMenuIcon("'icon-icon_menu_fund'");
        } else {
            menuDto.setMenuIcon("' '");
            menuDto.setRemark("'console-fund-ta-vue'");
        }
        String treeIdxPre = "/frame/";
        String sourcePre = STR_SLASH + kindCode.replaceAll(STR_QUOTES_SINGLE, STR_BLANK);
        if ("'fundsysinfo'".equals(parentCode)) {
            menuDto.setParentCode(STR_QUOTES_SINGLE + MENU_CODE_PARAM + STR_QUOTES_SINGLE);
            treeIdxPre += MENU_CODE_PARAM;
        } else if ("'fundAnalysis','fundAnalysisByTrans','fundReportManage'".contains(menuCode)) {
            menuDto.setParentCode(STR_QUOTES_SINGLE + MENU_CODE_QUERY + STR_QUOTES_SINGLE);
            treeIdxPre += MENU_CODE_QUERY;
        } else if (treeIdx.contains("fundsysinfo")) {
            treeIdxPre += MENU_CODE_PARAM;
        } else if (treeIdx.contains("fundAnalysis") || treeIdx.contains("fundAnalysisByTrans") || treeIdx.contains("fundReportManage") || "'ptaPrdAccStd'".equals(parentCode)) {
            treeIdxPre += MENU_CODE_QUERY;
        } else if (treeIdx.contains("fundDailyOperations") || treeIdx.contains("fundSpecialHandling") || treeIdx.contains("fundBusinessSecondTrade") || treeIdx.contains("fundPermissionManage")) {
            treeIdxPre += MENU_CODE_BUSIN;
            if ("'menu'".equals(transCode)) {
                if ("'fundDailyOperations'".equals(parentCode)) {
                    menuDto.setParentCode(STR_QUOTES_SINGLE + MENU_CODE_BUSIN + STR_QUOTES_SINGLE);
                } else {
                    menuDto.setMenuIcon("' '");
                }
            } else {
                if (treeIdx.contains("fundPermissionManage")) {
                    menuDto.setParentCode("'fundDataPermission'");
                    treeIdx = treeIdx.replace("fundPermissionManage", "fundDataPermission");
                }
            }
        } else if ("'ptaAccountManageFundOther'".equals(parentCode)) {
            treeIdxPre += MENU_CODE_QUERY;
            menuDto.setParentCode("'ptaAccountReport'");
            treeIdx = treeIdx.replace("fundOther", "ptaAccountReport");
        }  else if ("'ptaAccountManage'".equals(parentCode)) {
            treeIdxPre += MENU_CODE_QUERY;
            menuDto.setParentCode(STR_QUOTES_SINGLE + MENU_CODE_QUERY + STR_QUOTES_SINGLE);
            treeIdx = treeIdx.replace("fundAccount", "ptaAccountManageFundAccount");
        } else if ("'ptaAccountManageFundDaily'".equals(parentCode)) {
            treeIdxPre += MENU_CODE_BUSIN;
            menuDto.setParentCode("'ptaAccountManageFundOther'");
            treeIdx = treeIdx.replace("fundDaily", "ptaAccountManageFundOther");
        } else if ("'ptaAccountManageFundAccount'".equals(parentCode)) {
            treeIdxPre += MENU_CODE_QUERY;
            treeIdx = treeIdx.replace("fundOther", "ptaAccountManageFundAccount");
        } else {
            throw new Exception("菜单【" + menuCode.replaceAll(STR_QUOTES_SINGLE, STR_BLANK) + "】未匹配到UED菜单生成规则");
        }
        if ("'fundsysinfo'".equals(parentCode) || treeIdx.contains("fundsysinfo") || "'ptaAccountManageFundOther'".equals(parentCode) ||
                "'ptaAccountManageFundDaily'".equals(parentCode) || "'fundReportManage'".equals(parentCode) || "'ptaAccountManageFundAccount'".equals(parentCode) ||
                treeIdx.contains("fundDailyOperations") || treeIdx.contains("fundDataPermission") || treeIdx.contains("fundReportManage") ||
                treeIdx.contains("ptaAccountManageFundAccount")) {
            Pattern pattern = Pattern.compile(STR_SLASH);
            Matcher matcher = pattern.matcher(treeIdx);
            int secondMenuCodeEnd = 0;
            int index = 0;
            while (matcher.find()) {
                index = index + 1;
                if (index == 3) {
                    secondMenuCodeEnd = matcher.start();
                    break;
                }
            }
            sourcePre = treeIdx.substring(0, secondMenuCodeEnd);
        }
        treeIdx = treeIdx.replace(sourcePre, treeIdxPre).replaceAll(STR_QUOTES_SINGLE, STR_BLANK);
        if (!treeIdx.endsWith(STR_SLASH)) {
            treeIdx = STR_QUOTES_SINGLE + treeIdx + STR_SLASH + STR_QUOTES_SINGLE;
        } else {
            treeIdx = STR_QUOTES_SINGLE + treeIdx + STR_QUOTES_SINGLE;
        }
        if (("'fundAnalysisByTrans'".equals(menuCode) && !treeIdx.contains("fundAnalysisByTrans")) || (menuCode.endsWith("T'") && !treeIdx.contains("fundAnalysisByTrans"))) {
            treeIdx = treeIdx.replace("fundAnalysis", "fundAnalysisByTrans");
            Pattern pattern = Pattern.compile(STR_SLASH);
            Matcher matcher = pattern.matcher(treeIdx);
            String secondMenuCode;
            int secondMenuCodeStart = 0;
            int secondMenuCodeEnd = -1;
            int index = 0;
            while (matcher.find()) {
                index = index + 1;
                if (index == 4) {
                    secondMenuCodeStart = matcher.start() + 1;
                } else if (index == 5) {
                    secondMenuCodeEnd = matcher.start();
                }
            }
            if (secondMenuCodeEnd != -1) {
                secondMenuCode = treeIdx.substring(secondMenuCodeStart, secondMenuCodeEnd);
            } else {
                secondMenuCode = treeIdx.substring(secondMenuCodeStart);
            }
            treeIdx = treeIdx.replace(secondMenuCode, secondMenuCode + "T");
        }
        menuDto.setTreeIdx(treeIdx);

        String menuIcon = menuDto.getMenuIcon();
        if ("'icon-icon_menu_fund'".equals(menuIcon) && !"'query','param','busin'".contains(parentCode)) {
            menuDto.setMenuIcon("' '");
        }
        int orderNo = Integer.valueOf(menuDto.getOrderNo().trim().replaceAll(STR_QUOTES_SINGLE, STR_BLANK));
        menuDto.setOrderNo(String.valueOf(500000 + orderNo));
        menuDto.setOpenFlag("'1'");
        menuDto.setWindowType("' '");
        String sqlHead = sql.substring(0, sql.indexOf(STR_BRACKETS_RIGHT) + 1).toLowerCase() + STR_NEXT_LINE;
        String values = "values (";
        if (StringUtils.isNotBlank(paramControl)) {
            values = " select ";
        }
        values += menuDto.getMenuCode().trim() + ", ";
        values += menuDto.getKindCode().trim() + ", ";
        values += menuDto.getTransCode().trim() + ", ";
        values += menuDto.getSubTransCode().trim() + ", ";
        values += menuDto.getMenuName().trim() + ", ";
        values += menuDto.getMenuArg().trim() + ", ";
        values += menuDto.getMenuIcon().trim() + ", ";
        values += menuDto.getWindowType().trim() + ", ";
        values += menuDto.getTip().trim() + ", ";
        values += menuDto.getHotKey().trim() + ", ";
        values += menuDto.getParentCode().trim() + ", ";
        values += menuDto.getOrderNo().trim() + ", ";
        values += menuDto.getOpenFlag().trim() + ", ";
        values += menuDto.getTreeIdx().trim() + ", ";
        values += menuDto.getRemark().trim() + ", ";
        values += menuDto.getWindowModel().trim();
        if (StringUtils.isNotBlank(paramControl)) {
            int index = sql.indexOf(" from ");
            String partSql = sql.substring(index);
            if (partSql.endsWith(STR_SEMICOLON)) {
                partSql = partSql.substring(0, partSql.length() - 1);
            }
            values += STR_NEXT_LINE + partSql;
        } else {
            values += ")";
        }
        menuStd = sqlHead + values;
        return menuStd;
    }

    private static String generateUpdate(String item, String updateKey, boolean generate) throws Exception{
        Set<String> keyColumn = new HashSet<>();
        if (StringUtils.isNotBlank(updateKey)) {
            updateKey = updateKey.split("where")[1].trim();
            String[] updateItem = updateKey.split("and");
            for (String ele : updateItem) {
                keyColumn.add(ele.split("=")[0].trim());
            }
        }
        StringBuilder updateSql = new StringBuilder();
        if (generate) {
            if (!item.toLowerCase().contains("tbmenucondition")) {
                return STR_BLANK;
            }
        }
        if (item.toLowerCase().contains("tbmenucondition")) {
            updateSql.append("update tbmenuconditionuser set ");
        } else {
            updateSql.append("update " + getTableName(item) + " set ");
        }
        updateSql.append(STR_NEXT_LINE);
        item = item.replaceAll("\\s+", STR_BLANK);
        String[] sql = item.split(BaseConst.KEY_VALUES);
        if (sql.length != 2) {
            sql = item.split(BaseConst.KEY_VALUES.toUpperCase());
            if (sql.length != 2) {
                throw new Exception("sql语句未包含或者包含多个" + BaseConst.KEY_VALUES + STR_NEXT_LINE + item);
            }
        }
        String[] column = sql[0].substring(sql[0].indexOf("(") + 1, sql[0].indexOf(")")).split(",");
        String[] value = handleValue(column.length, sql[1].substring(sql[1].indexOf("(") + 1, sql[1].indexOf(")")).split(","));
        for (int i=0; i<column.length; i++) {
            if (keyColumn.contains(column[i])) {
                continue;
            }
            updateSql.append(STR_SPACE_2);
            updateSql.append(column[i] + " = " + ("''".equals(value[i]) ? "' '": value[i]));
            if (i != column.length - 1) {
                updateSql.append(STR_COMMA).append(STR_NEXT_LINE);
            }
        }
        updateSql.append(STR_NEXT_LINE + "where " + updateKey);
        return updateSql.toString();
    }

    private static String[] handleValue(int len, String[] value) {
        String[] res = new String[len];
        int index = 0;
        String val = STR_BLANK;
        for (String item : value) {
            if (item.startsWith(STR_QUOTES_SINGLE) && item.endsWith(STR_QUOTES_SINGLE)) {
                res[index] = item;
                index++;
            } else if (!item.contains(STR_QUOTES_SINGLE)) {
                if (StringUtils.isNotBlank(val)) {
                    val += item + STR_COMMA;
                } else {
                    res[index] = item;
                    index++;
                }
            } else if (item.startsWith(STR_QUOTES_SINGLE) || item.endsWith(STR_QUOTES_SINGLE)) {
                val += item + STR_COMMA;
                if (item.endsWith(STR_QUOTES_SINGLE)) {
                    if (val.endsWith(STR_COMMA)) {
                        val = val.substring(0, val.length() - 1);
                    }
                    res[index] = val;
                    val = STR_BLANK;
                    index++;
                }
            }
        }
        return res;
    }

    private static String getTableName(String sql) {
        int tableNameStartIndex = sql.toLowerCase().indexOf(BaseConst.KEY_INSERT_INTO);
        int tableNameStartEnd = sql.toLowerCase().indexOf(BaseConst.STR_BRACKETS_LEFT);
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
                    throw new Exception("sql语句未包含或者包含多个" + BaseConst.KEY_VALUES + STR_NEXT_LINE + item);
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
                updateSql.append(column[i] + " = " + (StringUtils.isEmpty(value[i]) ? STR_SPACE : value[i]));
                if (i != column.length - 1) {
                    updateSql.append(STR_QUOTES_SINGLE);
                }
            }
            updateSql.append(" where " + column[0] + " = " + value[0] + STR_QUOTES_SINGLE);
            updateSql.append(" and " + column[2] + " = " + value[2] + STR_QUOTES_SINGLE);
            updateSql.append(" and " + column[3] + " = " + value[3] + STR_QUOTES_SINGLE);
            updateSql.append(";");
        }
    }
}
