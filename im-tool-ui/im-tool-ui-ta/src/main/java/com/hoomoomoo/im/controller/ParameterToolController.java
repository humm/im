package com.hoomoomoo.im.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.consts.MenuFunctionConfig;
import com.hoomoomoo.im.dto.*;
import com.hoomoomoo.im.extend.ScriptSqlUtils;
import com.hoomoomoo.im.task.ParameterToolTask;
import com.hoomoomoo.im.task.ParameterToolTaskParam;
import com.hoomoomoo.im.utils.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.PARAMETER_TOOL;

/**
 * @author humm23693
 * @description
 * @package com.hoomoomoo.im.controller
 * @date 2021/9/16
 */
public class ParameterToolController implements Initializable {

    @FXML
    private TextArea logs;

    @FXML
    private TextField baseDictPath;

    @FXML
    private TextField paramRealtimeSetPath;

    @FXML
    private TextField tablePath;

    @FXML
    private Button executeRealtimeBtn;

    @FXML
    private Button errorTipsResult;

    @FXML
    private Button errorTipsResultByFile;

    @FXML
    private Label errorTips;

    private static String KEY_DATA = "\"data\": {";

    private static int paramRealtimeSetNum = 0;
    private static int tableColumnsNum = 0;

    private static boolean alertTips = true;

    private static Map<String, Map<String, String>> configDictValue = new LinkedHashMap<>();
    private static Map<String, String> configDictName = new LinkedHashMap<>();
    private static Set<String> beginValidDateSpecial = new HashSet<>();
    private static Map<String, Map<String, String>> tableColumnsConfig = new HashMap<>();
    private static List<List<String>> errorTableColumnInfo = new ArrayList<>();
    private static List<List<String>> errorConfigColumnInfo = new ArrayList<>();
    private static List<List<String>> errorDefaultValuesColumnInfo = new ArrayList<>();
    private static List<List<String>> errorOrderColumnInfo = new ArrayList<>();
    private static List<List<String>> errorMultipleTabInfo = new ArrayList<>();
    private static List<String> modifyInfo = new ArrayList<>();
    private static Map<String, List<String>> logTips = new LinkedHashMap<>();


    public void updateParameterDoc(TextArea logs) throws Exception {
        this.alertTips = false;
        this.logs = logs;
        this.errorTips = new Label();
        this.executeRealtimeBtn = new Button();
        this.errorTipsResult = new Button();
        this.errorTipsResultByFile = new Button();
        initialize(null, null);
        executeRealtime(null);
    }

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String msg = String.format(BaseConst.MSG_USE, PARAMETER_TOOL.getName());
        LoggerUtils.info(msg);
        LoggerUtils.writeLogInfo(PARAMETER_TOOL.getCode(), new Date(), Arrays.asList(msg));

        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        OutputUtils.info(baseDictPath, appConfigDto.getChangeToolBaseDictPath());
        OutputUtils.info(paramRealtimeSetPath, appConfigDto.getChangeToolParamRealtimeSetPath());
        OutputUtils.info(tablePath, appConfigDto.getChangeToolTablePath());
        errorTips.setVisible(false);
        // errorTipsResult.setVisible(false);
        // errorTipsResultByFile.setVisible(false);
    }

    @FXML
    void showParamRealtimeSetResult(ActionEvent event) throws IOException {
        Runtime.getRuntime().exec("explorer /e,/select," + new File(FileUtils.getFilePath(FILE_PARAM_REALTIME_SET)).getAbsolutePath());
    }

    @FXML
    void showParamRealtimeSetResultByFile(ActionEvent event) throws IOException {
        try {
            TaCommonUtils.openMultipleBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_PARAMETER_RESULT, "检查结果");
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, "请检查结果文件是否存在");
        }
    }

    @FXML
    void showSystemLog(ActionEvent event) throws IOException {
        try {
            TaCommonUtils.openMultipleBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_SYSTEM_LOG, "系统日志");
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.info(logs, "查看系统日志错误");
        }
    }

    @FXML
    void executeRealtime(ActionEvent event) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        if (CommonUtils.needUpdateVersion(appConfigDto)) {
            return;
        }
        if (alertTips) {
            OutputUtils.clearLog(logs);
        }
        try {
            String dictPath;
            if (baseDictPath != null) {
                dictPath = baseDictPath.getText();
            } else {
                dictPath = appConfigDto.getChangeToolBaseDictPath();
            }
            if (StringUtils.isBlank(dictPath)) {
                OutputUtils.info(logs, "请设置字典全量脚本文件");
                return;
            }
            if (new File(dictPath).isDirectory()) {
                OutputUtils.info(logs, "字典全量脚本文件必须为文件");
                return;
            }
            String paramPath;
            if (paramRealtimeSetPath != null) {
                paramPath = paramRealtimeSetPath.getText();
            } else {
                paramPath = appConfigDto.getChangeToolParamRealtimeSetPath();
            }
            if (StringUtils.isBlank(paramPath)) {
                OutputUtils.info(logs, "请设置开通脚本目录位置");
                return;
            }
            if (!new File(paramPath).isDirectory()) {
                OutputUtils.info(logs, "开通脚本目录必须为文件夹");
                return;
            }
            String path;
            if (tablePath != null) {
                path = tablePath.getText();
            } else {
                path = appConfigDto.getChangeToolTablePath();
            }
            if (StringUtils.isBlank(path)) {
                OutputUtils.info(logs, "请表结构目录");
                return;
            }
            if (!new File(path).isDirectory()) {
                OutputUtils.info(logs, "表结构目录必须为文件夹");
                return;
            }
            TaskUtils.execute(new ParameterToolTask(new ParameterToolTaskParam(this, STR_3, dictPath, paramPath, path)));
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.infoContainBr(logs, e.getMessage());
        }
    }

    public void executeRealtimeExe(String dictPath, String paramPath, String tablePath) {
        try {
            paramRealtimeSetNum = 0;
            tableColumnsNum = 0;
            errorTips.setVisible(false);
            errorTipsResult.setVisible(false);
            errorTipsResultByFile.setVisible(false);
            errorTableColumnInfo.clear();
            errorConfigColumnInfo.clear();
            errorDefaultValuesColumnInfo.clear();
            errorOrderColumnInfo.clear();
            errorMultipleTabInfo.clear();
            modifyInfo.clear();
            logTips.clear();
            executeRealtimeBtn.setDisable(true);
            OutputUtils.info(logs, getCommonMsg("初始化字典信息 开始"));
            initConfigInfo();
            initBaseDict(dictPath);
            initExtDict(paramPath);
            OutputUtils.info(logs, getCommonMsg("初始化字典信息 结束"));
            OutputUtils.info(logs, getCommonMsg("初始化表结构信息 开始"));
            initTableInfo(tablePath);
            OutputUtils.info(logs, getCommonMsg("初始化表结构信息 结束"));
            OutputUtils.info(logs, getCommonMsg("生成文件 开始"));
            buildFile(paramPath);
            if (paramRealtimeSetNum == 0) {
                OutputUtils.info(logs, getCommonMsg("未扫描到参数电子化配置脚本，请检查【开通脚本目录】配置是否正确"));
                errorTips.setVisible(true);
            }
            if (tableColumnsNum == 0) {
                OutputUtils.info(logs, getCommonMsg("未扫描到表结构，请检查【表结构目录】配置是否正确"));
                errorTips.setVisible(true);
            }
            OutputUtils.info(logs, getCommonMsg("生成文件 结束"));
            if (alertTips && CollectionUtils.isNotEmpty(modifyInfo)) {
                OutputUtils.infoContainBr(logs, "修改明细");
                for (String ele : modifyInfo) {
                    OutputUtils.infoContainBr(logs, ele);
                }
            }
            // 忽略提示信息
            Map<String, List<String>> desc = skipErrorTips(errorTableColumnInfo);
            desc.putAll(skipErrorTips(errorConfigColumnInfo));

            Map<String, StringBuilder> tipsByFile = new LinkedHashMap<>();
            int errorColumn = 0;
            int errorTable = 0;
            StringBuilder errorMessage = new StringBuilder();
            if (CollectionUtils.isNotEmpty(errorTableColumnInfo)) {
                String msg;
                for (List<String> ele : errorTableColumnInfo) {
                    String folder = ele.get(0);
                    if (StringUtils.isNotBlank(ele.get(3))) {
                        errorColumn++;
                        msg = String.format("%s  %s  %s  %s  %s  %s", ele.get(0), ele.get(1), ele.get(2), "未获取到表字段信息", ele.get(3), ele.get(4));
                    } else {
                        errorTable++;
                        msg = String.format("%s  %s  %s  %s  %s", ele.get(0), ele.get(1), ele.get(2), "未获取到表结构信息", ele.get(3));
                    }
                    msg += STR_NEXT_LINE;
                    errorMessage.append(msg);
                    if (tipsByFile.containsKey(folder)) {
                        tipsByFile.get(folder).append(msg);
                    } else {
                        tipsByFile.put(folder, new StringBuilder(msg));
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(errorConfigColumnInfo)) {
                for (List<String> ele : errorConfigColumnInfo) {
                    String folder = ele.get(0);
                    String msg = String.format("%s  %s  %s  %s  %s  %s", ele.get(0), ele.get(1), ele.get(2), "未配置字段信息", ele.get(3), ele.get(4)) + STR_NEXT_LINE;
                    errorMessage.append(msg);
                    if (tipsByFile.containsKey(folder)) {
                        tipsByFile.get(folder).append(msg);
                    } else {
                        tipsByFile.put(folder, new StringBuilder(msg));
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(errorDefaultValuesColumnInfo)) {
                for (List<String> ele : errorDefaultValuesColumnInfo) {
                    String folder = ele.get(0);
                    String msg = String.format("%s  %s  %s  %s  %s  %s", ele.get(0), ele.get(1), ele.get(2), "未配置字段默认值", ele.get(3), ele.get(4)) + STR_NEXT_LINE;
                    errorMessage.append(msg);
                    if (tipsByFile.containsKey(folder)) {
                        tipsByFile.get(folder).append(msg);
                    } else {
                        tipsByFile.put(folder, new StringBuilder(msg));
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(errorOrderColumnInfo)) {
                for (List<String> ele : errorOrderColumnInfo) {
                    String folder = ele.get(0);
                    String msg = String.format("%s  %s  %s  %s  %s  %s", ele.get(0), ele.get(1), ele.get(2), "未配置字段排序", ele.get(3), ele.get(4)) + STR_NEXT_LINE;
                    errorMessage.append(msg);
                    if (tipsByFile.containsKey(folder)) {
                        tipsByFile.get(folder).append(msg);
                    } else {
                        tipsByFile.put(folder, new StringBuilder(msg));
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(errorMultipleTabInfo)) {
                for (List<String> ele : errorMultipleTabInfo) {
                    String folder = ele.get(0);
                    String msg = String.format("%s  %s  %s  %s", ele.get(0), ele.get(1), ele.get(2), "存在多tab未指定tab名称") + STR_NEXT_LINE;
                    errorMessage.append(msg);
                    if (tipsByFile.containsKey(folder)) {
                        tipsByFile.get(folder).append(msg);
                    } else {
                        tipsByFile.put(folder, new StringBuilder(msg));
                    }
                }
            }

            Set<String> currentUpdate = getCurrentUpdateTab();

            Iterator<String> iterator = tipsByFile.keySet().iterator();
            while (iterator.hasNext()) {
                String ele = iterator.next();
                if (!currentUpdate.contains(ele)) {
                    iterator.remove();
                }
            }

            FileUtils.deleteFile(new File(FileUtils.getFilePath(FILE_PARAM_REALTIME_SET_FOLDER)));

            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            if (StringUtils.isNotBlank(errorMessage)) {
                StringBuilder summary = new StringBuilder();
                summary.append("未获取到表字段信息: " + errorColumn + STR_SPACE_2);
                summary.append("未获取到表结构信息: " + errorTable + STR_SPACE_2);
                summary.append("未配置字段信息: " + errorConfigColumnInfo.size() + STR_SPACE_2);
                summary.append("未配置字段默认值: " + errorDefaultValuesColumnInfo.size() + STR_SPACE_2);
                summary.append("未配置字段排序: " + errorOrderColumnInfo.size() + STR_SPACE_2);

                if (alertTips) {
                    OutputUtils.infoContainBr(logs, "异常明细信息");
                    OutputUtils.infoContainBr(logs, errorMessage.toString());
                }

                if (CollectionUtils.isNotEmpty(currentUpdate)) {
                    if (MapUtils.isNotEmpty(tipsByFile)) {
                        for (Map.Entry<String, StringBuilder> entry : tipsByFile.entrySet()) {
                            FileUtils.writeFile(FileUtils.getFilePath(FILE_PARAM_REALTIME_SET_FOLDER + entry.getKey() + FILE_TYPE_SQL), Arrays.asList(entry.getValue().toString()));
                            currentUpdate.remove(entry.getKey());
                        }
                    }

                    List<String> notFixed = new ArrayList<>(currentUpdate);
                    Collections.sort(notFixed, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });

                    List<String> needFixed = new ArrayList<>(tipsByFile.keySet());
                    Collections.sort(needFixed, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });

                    if (MapUtils.isNotEmpty(logTips)) {
                        for (Map.Entry<String, List<String>> entry : logTips.entrySet()) {
                            List<String> ele = entry.getValue();
                            String msg = String.format("%s  %s  %s  %s  %s", ele.get(0), ele.get(1), ele.get(2), ele.get(3), ele.get(4));
                            summary.append(STR_NEXT_LINE + msg);
                        }
                        summary.append(STR_NEXT_LINE);
                    }
                    summary.append(STR_NEXT_LINE + "本次修改【无差异】页面(" + notFixed.size() + "): " + notFixed.stream().collect(Collectors.joining(STR_SPACE_2)));
                    summary.append(STR_NEXT_LINE + "本次修改【有差异】页面(" + needFixed.size() + "): " + needFixed.stream().collect(Collectors.joining(STR_SPACE_2)));
                    summary.append(STR_NEXT_LINE);
                }

                FileUtils.writeFile(FileUtils.getFilePath(FILE_PARAM_REALTIME_SET), Arrays.asList(summary + STR_NEXT_LINE_2 + errorMessage));
                OutputUtils.info(logs, summary.toString());

                errorTips.setVisible(true);
                errorTipsResult.setVisible(true);
                errorTipsResultByFile.setVisible(true);
                if (alertTips) {
                    Platform.runLater(() -> {
                        CommonUtils.showTipsByError(summary.toString(), 90 * 1000);
                    });
                } else {
                    appConfigDto.getRepairErrorInfo().add(NAME_PARAMETER_DOC);
                }
            } else {
                FileUtils.writeFile(FileUtils.getFilePath(FILE_PARAM_REALTIME_SET), Arrays.asList("完美..."));
                appConfigDto.getRepairErrorInfo().add(NAME_PARAMETER_DOC_SUCCESS);
            }
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.infoContainBr(logs, e.getMessage());
        } finally {
            executeRealtimeBtn.setDisable(false);
        }
    }

    private Set<String> getCurrentUpdateTab() {
        Set<String> currentUpdate = new LinkedHashSet<>();
        currentUpdate.add("03_fundProfitSchemaSet");
        currentUpdate.add("04_fundProfitProjSet");
        currentUpdate.add("05_fundMergerControlSet");
        currentUpdate.add("06_fundFareBelongInfoSet");
        currentUpdate.add("07_fundStatusModify");
        currentUpdate.add("08_fundFactCollectInfoSet");
        currentUpdate.add("09_fundTrusteeLiquiDay");
        currentUpdate.add("10_fundAgencyLiquiDay");
        currentUpdate.add("11_fundFundStatusSchema");
        currentUpdate.add("12_fundProductInfoSet");
        currentUpdate.add("13_fundCurrencySchemeSet");
        currentUpdate.add("14_fundSaleQualifySet");
        currentUpdate.add("15_fundFeeRateInfo");
        currentUpdate.add("35_fundChangeLiquiDay");
        currentUpdate.add("36_fundChangeLimitInfoSet");
        currentUpdate.add("37_fundNetRedeemSchema");
        currentUpdate.add("41_fundTailRatioBalance");
        currentUpdate.add("43_fundTrationControl");
        currentUpdate.add("25_fundProfitZoneSet");
        currentUpdate.add("89_fundManualProfit");
        currentUpdate.add("30_fundSpecifyRedeemSet");
        currentUpdate.add("39_fundUpgradeInfoSet");
        currentUpdate.add("40_fundConctrlSchemaSet");
        currentUpdate.add("42_fundCustTailRatioBalance");
        currentUpdate.add("31_fundDiscountLimitInfoSet");
        currentUpdate.add("18_fundAgencyInfoBase");
        currentUpdate.add("20_fundFundOpendayListSet");
        currentUpdate.add("46_fundIdTypeBizLimitSet");
        currentUpdate.add("88_fundProfitBaseZoneSet");
        currentUpdate.add("90_fundNewStiBusLmt");
        currentUpdate.add("79_fundInvestorRoleLimitSet");
        currentUpdate.add("80_fundInvestorRoleSet");
        return currentUpdate;
    }

    private String getCommonMsg(String message) {
        if (alertTips) {
            return message + STR_NEXT_LINE;
        }
        return ScriptCheckController.getCommonMsg(NAME_PARAMETER_DOC, message);
    }

    private boolean skipColumns(String tableCode, String fieldCode) {
        // 中信孤版个性化字段
        if (StringUtils.equals("tbfundfarebelong_date", tableCode) && StringUtils.equals("effective_flag", fieldCode)) {
            return true;
        }
        return false;
    }

    private Map<String, List<String>> skipErrorTips(List<List<String>> error) throws IOException {
        Map<String, List<String>> desc = new HashMap<>();
        Iterator<List<String>> iterator = error.listIterator();
        Map<String, List<String>> skipExcludeConfig = new HashMap<>();
        Map<String, List<String>> skipIncludeConfig = new HashMap<>();
        String excludeDev = FileUtils.getFilePath(PATH_PARAM_REALTIME_SET_SKIP_EXCLUDE_DEV);
        String includeDev = FileUtils.getFilePath(PATH_PARAM_REALTIME_SET_SKIP_INCLUDE_DEV);
        if (new File(excludeDev).exists()) {
            skipExcludeConfig.putAll(JSON.parseObject(FileUtils.readNormalFileToString(excludeDev), Map.class));
        }
        if (new File(includeDev).exists()) {
            skipIncludeConfig.putAll(JSON.parseObject(FileUtils.readNormalFileToString(includeDev), Map.class));
        }
        skipExcludeConfig.putAll(JSON.parseObject(FileUtils.readNormalFileToString(FileUtils.getFilePath(PATH_PARAM_REALTIME_SET_SKIP_EXCLUDE)), Map.class));
        skipIncludeConfig.putAll(JSON.parseObject(FileUtils.readNormalFileToString(FileUtils.getFilePath(PATH_PARAM_REALTIME_SET_SKIP_INCLUDE)), Map.class));
        while (iterator.hasNext()) {
            List<String> errorInfo = iterator.next();
            if (errorInfo.size() != 5) {
                continue;
            }
            String fileFolderName = errorInfo.get(0);
            String fileName = errorInfo.get(1);
            String column = errorInfo.get(4);
            // 剔除字段
            if (skipExcludeConfig.containsKey(fileName) || skipExcludeConfig.containsKey(KEY_GLOBAL)) {
                List<String> columns = new ArrayList<>();
                List<String> tableColumns = skipExcludeConfig.get(fileName);
                if (CollectionUtils.isNotEmpty(tableColumns)) {
                    columns.addAll(tableColumns);
                }
                columns.addAll(skipExcludeConfig.get(KEY_GLOBAL));
                if (columns.contains(column)) {
                    iterator.remove();
                    continue;
                }
            }
            // 包含字段
            if (skipIncludeConfig.containsKey(fileName)) {
                List<String> columns = skipIncludeConfig.get(fileName);
                if (CollectionUtils.isNotEmpty(columns) && !columns.contains(column)) {
                    if (!logTips.containsKey(fileFolderName)) {
                        logTips.put(fileFolderName, Arrays.asList(errorInfo.get(0), errorInfo.get(1), errorInfo.get(2), errorInfo.get(3), "配置了自定义字段显示,请确认已涵盖页面所使用字段"));
                    }
                    iterator.remove();
                    continue;
                }
            }
        }
        return desc;
    }

    private void initTableInfo(String tablePath) throws IOException {
        File file = new File(tablePath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File item : files) {
                initTableInfo(item.getAbsolutePath());
            }
        } else {
            if (tablePath.contains("oracle.table") && !tablePath.contains("temp_pubfund")) {
                tableColumnsNum++;
                getTableColumns(tablePath);
            }
        }
    }

    private void getTableColumns(String tablePath) throws IOException {
        if (alertTips) {
            OutputUtils.infoContainBr(logs, "扫描文件 " + tablePath);
        }
        String tableContent = FileUtils.readNormalFileToString(tablePath);
        if (StringUtils.isBlank(tableContent)) {
            return;
        }
        String[] table = tableContent.split("create table");
        for (int i = 1; i < table.length; i++) {
            String item = changeToLower(CommonUtils.formatStrToSingleSpace(table[i]));
            item = item.split(KEY_CONSTRAINT)[0];
            String tableName = changeToLower(item.split("\\(")[0]);
            String[] tableColumns = item.split("not null,");
            for (String column : tableColumns) {
                if (StringUtils.isBlank(column)) {
                    continue;
                }
                String[] eleConfig = column.split(STR_S_SLASH);
                String fieldCode = STR_BLANK;
                String fieldType = STR_BLANK;
                for (int j = 0; j < eleConfig.length; j++) {
                    String ele = changeToLower(eleConfig[j]);
                    if (StringUtils.isBlank(ele)) {
                        continue;
                    }
                    if (ele.contains("(")) {
                        String[] fieldInfo = (ele.split("\\("));
                        if (fieldInfo.length == 0) {
                            continue;
                        }
                        if (fieldInfo.length == 1) {
                            fieldCode = eleConfig[j + 1];
                            fieldType = eleConfig[j + 2];
                        } else {
                            fieldCode = ele.split("\\(")[1];
                            fieldType = eleConfig[j + 1];
                        }
                        break;
                    } else if (StringUtils.equals(tableName, ele)) {
                        continue;
                    } else {
                        fieldCode = ele;
                        fieldType = eleConfig[j + 1];
                        break;
                    }
                }
                if (tableColumnsConfig.containsKey(tableName)) {
                    tableColumnsConfig.get(tableName).put(fieldCode, fieldType);
                } else {
                    Map<String, String> field = new HashMap<>(2);
                    field.put(fieldCode, fieldType);
                    tableColumnsConfig.put(tableName, field);
                }
            }
        }
    }

    private String changeToLower(String str) {
        return StringUtils.isBlank(str) ? STR_BLANK : str.toLowerCase().trim();
    }

    private void initConfigInfo() {
        beginValidDateSpecial.add("fundFeeRateInfo");
        beginValidDateSpecial.add("fundProfitProjSet");
        beginValidDateSpecial.add("fundProfitZoneSet");
    }

    private void initExtDict(String path) throws IOException {
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File item : files) {
                initExtDict(item.getAbsolutePath());
            }
        } else {
            initBaseDict(path);
        }
    }

    private void initBaseDict(String path) throws IOException {
        if (!path.endsWith(FILE_TYPE_SQL)) {
            return;
        }
        if (alertTips) {
            OutputUtils.infoContainBr(logs, "扫描文件 " + path);
        }
        String dict = FileUtils.readNormalFileToString(path);
        dict = CommonUtils.formatStrToSingleSpace(dict);
        if (StringUtils.isNotBlank(dict)) {
            String[] element = dict.split(STR_SEMICOLON);
            for (String ele : element) {
                String eleLower = ele.toLowerCase();
                if (eleLower.contains(KEY_INSERT_INTO) && eleLower.contains(KEY_VALUES)) {
                    String eleAfter = ScriptSqlUtils.handleSqlForValues(ele);
                    if (eleAfter != null) {
                        String[] dictInfo = eleAfter.split("',");
                        if (dictInfo.length < 4) {
                            // LoggerUtils.error("数据字典格式化获取数据错误: " + eleAfter);
                            continue;
                        }
                        String dictCode = ScriptSqlUtils.getSqlFieldValue(dictInfo[0].replace(STR_BRACKETS_LEFT, STR_BLANK));
                        String dictName = ScriptSqlUtils.getSqlFieldValue(dictInfo[1]);
                        String dictKey = ScriptSqlUtils.getSqlFieldValue(dictInfo[2]);
                        String dictPrompt = ScriptSqlUtils.getSqlFieldValue(dictInfo[3]);
                        if (StringUtils.isBlank(dictCode)) {
                            continue;
                        }
                        if (configDictValue.containsKey(dictCode)) {
                            Map<String, String> dictMap = configDictValue.get(dictCode);
                            if (!dictMap.containsKey(dictKey)) {
                                dictMap.put(dictKey, dictPrompt);
                            }
                        } else {
                            Map<String, String> dictMap = new LinkedHashMap<>();
                            dictMap.put(dictKey, dictPrompt);
                            configDictValue.put(dictCode, dictMap);
                            configDictName.put(dictCode, dictName);
                        }
                    }
                }
            }
        }
    }

    private ParamRealtimeDto initTabComponent(String filePath) throws IOException {
        File file = new File(filePath);
        String fileName = file.getName();
        String fileFolder = new File(file.getParent()).getName();
        String content = FileUtils.readNormalFileToStringSkipAnnotation(filePath);
        content = CommonUtils.formatStrToSingleSpace(content);
        ParamRealtimeDto paramRealtimeDto = new ParamRealtimeDto();
        List<ParamRealtimeApiTabDto> paramRealtimeApiTabList = new ArrayList<>();
        List<ParamRealtimeApiComponentDto> paramRealtimeApiComponentDtoList = new ArrayList<>();
        paramRealtimeDto.setParamRealtimeApiTabList(paramRealtimeApiTabList);
        paramRealtimeDto.setParamRealtimeApiComponentDtoList(paramRealtimeApiComponentDtoList);
        if (StringUtils.isNotBlank(content)) {
            String[] element = content.split("\\);");
            for (String ele : element) {
                String eleLower = ele.toLowerCase();
                if (eleLower.contains(KEY_INSERT_INTO) && eleLower.contains(KEY_VALUES)) {
                    String eleAfter = ScriptSqlUtils.handleSqlForValues(ele);
                    if (eleAfter != null) {
                        String[] sqlInfo = eleAfter.split("',");
                        if (eleLower.contains(KEY_TB_FUND_API_TAB)) {
                            if (sqlInfo.length < 6) {
                                LoggerUtils.error("格式化获取数据错误: " + ele);
                                continue;
                            }
                            ParamRealtimeApiTabDto paramRealtimeApiTab = new ParamRealtimeApiTabDto();
                            paramRealtimeApiTabList.add(paramRealtimeApiTab);
                            paramRealtimeApiTab.setMenuCode(ScriptSqlUtils.getSqlFieldValue(sqlInfo[0].replace(STR_BRACKETS_LEFT, STR_BLANK)));
                            paramRealtimeApiTab.setTabCode(ScriptSqlUtils.getSqlFieldValue(sqlInfo[1]));
                            paramRealtimeApiTab.setTabName(ScriptSqlUtils.getSqlFieldValue(sqlInfo[2]));
                            paramRealtimeApiTab.setServiceCode(ScriptSqlUtils.getSqlFieldValue(sqlInfo[3]));
                            paramRealtimeApiTab.setOnSubmit(ScriptSqlUtils.getSqlFieldValue(sqlInfo[4]));
                            paramRealtimeApiTab.setCheckName(ScriptSqlUtils.getSqlFieldValue(sqlInfo[5].replace(STR_BRACKETS_RIGHT, STR_BLANK)));
                            if (sqlInfo.length == 9) {
                                paramRealtimeApiTab.setFieldName(ScriptSqlUtils.getSqlFieldValue(sqlInfo[6]));
                                paramRealtimeApiTab.setDstScope(ScriptSqlUtils.getSqlFieldValue(sqlInfo[7]));
                                paramRealtimeApiTab.setTableCode(ScriptSqlUtils.getSqlFieldValue(sqlInfo[8].replace(STR_BRACKETS_RIGHT, STR_BLANK)));
                            }
                        } else if (eleLower.contains(KEY_TB_FUND_API_COMPONENT)) {
                            if (sqlInfo.length < 6) {
                                LoggerUtils.error("格式化获取数据错误: " + ele);
                                continue;
                            }
                            String tabCode = ScriptSqlUtils.getSqlFieldValue(sqlInfo[0].replace(STR_BRACKETS_LEFT, STR_BLANK));
                            String fieldCode = ScriptSqlUtils.getSqlFieldValue(sqlInfo[1]);
                            String fieldName = ScriptSqlUtils.getSqlFieldValue(sqlInfo[2]);
                            if (sqlInfo.length < 8) {
                                errorDefaultValuesColumnInfo.add(Arrays.asList(fileFolder, fileName, tabCode, fieldName, fieldCode));
                            }
                            ParamRealtimeApiComponentDto paramRealtimeApiComponentDto = new ParamRealtimeApiComponentDto();
                            paramRealtimeApiComponentDtoList.add(paramRealtimeApiComponentDto);
                            paramRealtimeApiComponentDto.setTabCode(tabCode);
                            paramRealtimeApiComponentDto.setFieldCode(fieldCode);
                            paramRealtimeApiComponentDto.setFieldName(fieldName);
                            paramRealtimeApiComponentDto.setTransType(ScriptSqlUtils.getSqlFieldValue(sqlInfo[3]));
                            paramRealtimeApiComponentDto.setDictKey(ScriptSqlUtils.getSqlFieldValue(sqlInfo[4]));
                            paramRealtimeApiComponentDto.setCheckRules(ScriptSqlUtils.getSqlFieldValue(sqlInfo[5].replace(STR_BRACKETS_RIGHT, STR_BLANK)));
                            if (sqlInfo.length >= 7) {
                                String orderField = ScriptSqlUtils.getSqlFieldValue(sqlInfo[6]);
                                paramRealtimeApiComponentDto.setOrderField(orderField);
                                if (StringUtils.isBlank(orderField)) {
                                    errorOrderColumnInfo.add(Arrays.asList(fileFolder, fileName, tabCode, fieldName, fieldCode));
                                }
                            } else {
                                errorOrderColumnInfo.add(Arrays.asList(fileFolder, fileName, tabCode, fieldName, fieldCode));
                            }
                            if (sqlInfo.length >= 8) {
                                paramRealtimeApiComponentDto.setDefaultValue(ScriptSqlUtils.getSqlFieldValue(sqlInfo[7].replace(STR_BRACKETS_RIGHT, STR_BLANK)));
                            }
                        }
                    }
                }
            }
        }
        return paramRealtimeDto;
    }

    private void buildFile(String path) throws Exception {
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File item : files) {
                String absolutePath = item.getAbsolutePath();
                if (absolutePath.contains("paramRealtimeSet")) {
                    buildFile(absolutePath);
                }
            }
        } else {
            paramRealtimeSetNum++;
            if (!path.endsWith(".sql")) {
                return;
            }
            if (alertTips) {
                OutputUtils.infoContainBr(logs, "扫描文件 " + path);
            }
            buildExcel(path);
        }
    }

    private void buildExcel(String filePath) throws Exception {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        ParamRealtimeDto paramRealtimeDto = initTabComponent(filePath);
        if (CollectionUtils.isEmpty(paramRealtimeDto.getParamRealtimeApiTabList())) {
            return;
        }
        buildInterfaceDesc(workbook);
        buildRequestDesc(workbook, paramRealtimeDto, new File(filePath));
        buildComponentDesc(workbook, paramRealtimeDto, new File(filePath));
        buildDictDesc(workbook, paramRealtimeDto);
        buildFile(workbook, paramRealtimeDto, filePath);
    }

    private void buildFile(SXSSFWorkbook workbook, ParamRealtimeDto paramRealtimeDto, String filePath) throws IOException {
        String excelFileBakPath = filePath.replace(".sql", KEY_BACKUP + ".xlsx");
        String jsonFilePath = filePath.replace(".sql", "_normal.json");
        String jsonSceneFilePath = filePath.replace(".sql", "_scene.json");
        List<String> requestContent = paramRealtimeDto.getRequestContent();
        FileUtils.writeFile(jsonFilePath, requestContent.stream().collect(Collectors.joining(STR_NEXT_LINE)), ENCODING_UTF8);
        FileUtils.writeFile(jsonSceneFilePath, buildSceneContent(paramRealtimeDto).stream().collect(Collectors.joining(STR_NEXT_LINE)), ENCODING_UTF8);
        FileOutputStream fileOutputStream = new FileOutputStream(excelFileBakPath);
        workbook.write(fileOutputStream);
        workbook.dispose();
        String excelFilePath = excelFileBakPath.replace(KEY_BACKUP, STR_BLANK);
        if (ExcelComparatorUtils.compareExcel(excelFilePath, excelFileBakPath)) {
            FileUtils.deleteFile(excelFileBakPath);
        } else {
            List<String> diffList = ExcelComparatorUtils.getDiffList();
            modifyInfo.add(STR_SPACE_2 + filePath);
            for (String diff : diffList) {
                modifyInfo.add(STR_SPACE_4 + diff);
            }
            FileUtils.deleteFile(excelFilePath);
            Files.move(Paths.get(excelFileBakPath), Paths.get(excelFilePath), StandardCopyOption.ATOMIC_MOVE);
        }
    }

    private List<String> buildSceneContent(ParamRealtimeDto paramRealtimeDto) {
        List<String> requestPartData = paramRealtimeDto.getRequestPartData();
        List<String> sceneContent = new ArrayList<>();
        sceneContent.add(requestPartData.get(0));
        int dataIndex = 0;
        for (int i = 0; i < requestPartData.size(); i++) {
            if (requestPartData.get(i).contains(KEY_DATA)) {
                dataIndex = i;
                break;
            }
        }
        sceneContent.add("    \"systemCode\": \"xxxxxxxxxx\",");
        sceneContent.add("    \"mac\": \"xxxxxxxxxx\",");
        sceneContent.add("    \"sign\": \"xxxxxxxxxx\",");
        sceneContent.add("    \"username\": \"admin\",");
        sceneContent.add("    \"user\": \"productcenter\",");
        sceneContent.add("    \"operatorId\": \"admin\",");
        sceneContent.add("    \"checkerId\": \"system\",");
        sceneContent.add("    \"importDate\": \"20251010\",");
        sceneContent.add("    \"finishDate\": \"20251010\",");
        sceneContent.add("    \"sceneCode\": \"xxx\",");
        sceneContent.add("    \"projectCode\": \"xxx\",");
        sceneContent.add("    \"projectName\": \"xxx\",");
        sceneContent.add("    \"memo\": \"xxx\",");
        sceneContent.addAll(requestPartData.subList(dataIndex, requestPartData.size()));

        List<String> head = new ArrayList<>();
        head.add("{");
        head.add("    \"请求地址\": \"http://127.0.0.1:8181/API/paramSceneConfig/paramSceneConfigData\",");
        head.add("    \"参考报文说明\": \"场景化使用\"");
        if (StringUtils.isNotBlank(paramRealtimeDto.getBeginValidDate())) {
            addBeginValidDate(head, paramRealtimeDto.getBeginValidDate());
        }
        head.add("}");
        sceneContent.addAll(0, head);
        return sceneContent;
    }

    private void buildComponentDesc(SXSSFWorkbook workbook, ParamRealtimeDto paramRealtimeDto, File file) throws Exception {
        String fileName = file.getName();
        String fileFolder = new File(file.getParent()).getName();
        List<ParamRealtimeApiComponentDto> paramRealtimeApiComponentDtoList = paramRealtimeDto.getParamRealtimeApiComponentDtoList();
        List<ParamRealtimeApiTabDto> paramRealtimeApiTabDtoList = paramRealtimeDto.getParamRealtimeApiTabList();
        for (ParamRealtimeApiTabDto paramRealtimeApiTabDto : paramRealtimeApiTabDtoList) {
            String sheetName = "字段说明";
            if (paramRealtimeApiTabDtoList.size() != 1) {
                sheetName = paramRealtimeApiTabDto.getTabName();
            }
            String tabCode = paramRealtimeApiTabDto.getTabCode();
            SXSSFSheet componentDesc = workbook.createSheet(sheetName);
            CellStyle titleCellStyle = ExcelCommonUtils.getTitleCellStyle(workbook);

            SXSSFRow rowTitle = componentDesc.createRow(0);
            buildRowCell(rowTitle, titleCellStyle, 0, "字段代码");
            buildRowCell(rowTitle, titleCellStyle, 1, "字段描述");
            buildRowCell(rowTitle, titleCellStyle, 2, "字段类型");
            buildRowCell(rowTitle, titleCellStyle, 3, "字段最大长度");
            buildRowCell(rowTitle, titleCellStyle, 4, "数据字典");
            buildRowCell(rowTitle, titleCellStyle, 5, "默认值");
            buildRowCell(rowTitle, titleCellStyle, 6, "必填");
            buildRowCell(rowTitle, titleCellStyle, 7, "校验规则");

            componentDesc.setColumnWidth(0, 35 * 256);
            componentDesc.setColumnWidth(1, 35 * 256);
            componentDesc.setColumnWidth(2, 15 * 256);
            componentDesc.setColumnWidth(3, 15 * 256);
            componentDesc.setColumnWidth(4, 15 * 256);
            componentDesc.setColumnWidth(5, 20 * 256);
            componentDesc.setColumnWidth(6, 10 * 256);
            componentDesc.setColumnWidth(7, 200 * 256);

            CellStyle centerCellStyle = ExcelCommonUtils.getCenterCellStyle(workbook);
            CellStyle redCenterCellStyle = ExcelCommonUtils.getRedCenterCellStyle(workbook);
            CellStyle wrapTextCellStyle = ExcelCommonUtils.getWrapTextCellStyle(workbook);
            String tableCode = changeToLower(paramRealtimeApiTabDto.getTableCode());
            Map<String, String> tableInfo = tableColumnsConfig.get(tableCode);
            if (StringUtils.equals(tableCode, "tbfundproduct_date")) {
                if (tableColumnsConfig.containsKey("tbfundproduct_ext")) {
                    tableInfo.putAll(tableColumnsConfig.get("tbfundproduct_ext"));
                }
            }
            if (MapUtils.isNotEmpty(tableInfo)) {
                List<String> fieldList = paramRealtimeApiComponentDtoList.stream().map(ParamRealtimeApiComponentDto::getFieldCode).collect(Collectors.toList());
                for (String key : tableInfo.keySet()) {
                    if (!fieldList.contains(key)) {
                        errorConfigColumnInfo.add(Arrays.asList(fileFolder, fileName, paramRealtimeApiTabDto.getTabName(), tableCode, key));
                    }
                }
            }
            int rowIndex = 0;
            boolean errorTableColumnInfoExists;
            for (ParamRealtimeApiComponentDto item : paramRealtimeApiComponentDtoList) {
                errorTableColumnInfoExists = false;
                String fieldCode = changeToLower(item.getFieldCode());
                if (skipColumns(tableCode, fieldCode)) {
                    continue;
                }
                boolean needAddBeginValidDate = StringUtils.isNotBlank(paramRealtimeApiTabDto.getFieldName()) && !beginValidDateSpecial.contains(paramRealtimeApiTabDto.getMenuCode());
                if (StringUtils.equals(paramRealtimeApiTabDto.getFieldName(), fieldCode) && needAddBeginValidDate) {
                    continue;
                }
                if (!StringUtils.equals(tabCode, item.getTabCode())) {
                    continue;
                }
                SXSSFRow row = componentDesc.createRow(++rowIndex);
                buildRowCell(row, null, 0, fieldCode);
                buildRowCell(row, null, 1, item.getFieldName());
                if (MapUtils.isNotEmpty(tableInfo)) {
                    if (tableInfo.containsKey(fieldCode)) {
                        String column = tableInfo.get(fieldCode);
                        String fieldType = STR_BLANK;
                        String fieldMaxLength = STR_BLANK;
                        if (column.startsWith(COLUMN_TYPE_VARCHAR2) || column.startsWith(COLUMN_TYPE_CLOB)) {
                            fieldType = COLUMN_TYPE_C;
                            fieldMaxLength = column.split("\\(")[1].replace(STR_BRACKETS_RIGHT, STR_BLANK);
                        } else if (column.startsWith(COLUMN_TYPE_INTEGER)) {
                            fieldType = COLUMN_TYPE_I;
                        } else if (column.startsWith(COLUMN_TYPE_NUMBER)) {
                            fieldType = COLUMN_TYPE_N;
                            fieldMaxLength = column.split("\\(")[1].replace(STR_BRACKETS_RIGHT, STR_BLANK);
                        }
                        buildRowCell(row, centerCellStyle, 2, fieldType);
                        buildRowCell(row, null, 3, fieldMaxLength);
                    } else {
                        for (List<String> ele : errorTableColumnInfo) {
                            if (StringUtils.equals(ele.get(1), fileName) && StringUtils.equals(ele.get(1), paramRealtimeApiTabDto.getTabName())
                                    && StringUtils.equals(ele.get(2), tableCode) && StringUtils.equals(ele.get(3), fieldCode)) {
                                errorTableColumnInfoExists = true;
                                break;
                            }
                        }
                        if (!errorTableColumnInfoExists) {
                            errorTableColumnInfo.add(Arrays.asList(fileFolder, fileName, paramRealtimeApiTabDto.getTabName(), tableCode, fieldCode));
                        }
                    }
                } else {
                    if (StringUtils.isNotBlank(tabCode)) {
                        for (List<String> ele : errorTableColumnInfo) {
                            if (StringUtils.equals(ele.get(1), fileName) && StringUtils.equals(ele.get(1), paramRealtimeApiTabDto.getTabName())
                                    && StringUtils.equals(ele.get(2), tableCode)) {
                                errorTableColumnInfoExists = true;
                                break;
                            }
                        }
                        if (!errorTableColumnInfoExists) {
                            errorTableColumnInfo.add(Arrays.asList(fileFolder, fileName, paramRealtimeApiTabDto.getTabName(), tableCode, STR_BLANK));
                        }
                    }
                }

                if (StringUtils.equals(STR_0, item.getTransType()) && StringUtils.isNotBlank(item.getDictKey())) {
                    buildRowCell(row, null, 4, item.getDictKey());
                }
                if (StringUtils.isNotBlank(item.getDefaultValue())) {
                    buildRowCell(row, null, 5, item.getDefaultValue());
                }
                String required = KEY_N;
                List<String> primaryKey = paramRealtimeDto.getPrimaryKey().get(tabCode);
                if (CollectionUtils.isNotEmpty(primaryKey) && primaryKey.contains(fieldCode)) {
                    required = KEY_Y;
                }
                String checkRules = item.getCheckRules();
                if (StringUtils.isNotBlank(checkRules.trim())) {
                    StringBuilder rule = new StringBuilder();
                    checkRules = checkRules.replaceAll("\\\\", STR_BLANK);
                    JSONArray rules;
                    try {
                        rules = JSON.parseArray(checkRules);
                    } catch (Exception e) {
                        OutputUtils.infoContainBr(logs, checkRules);
                        throw new Exception(e);
                    }
                    Iterator iterator = rules.iterator();
                    while (iterator.hasNext()) {
                        String ele = iterator.next().toString();
                        if (StringUtils.equals(required, KEY_N)) {
                            if (ele.contains("\"required\"") && ele.contains("true")) {
                                required = KEY_Y;
                            } else if (ele.contains("\"checkIsRequired\"")) {
                                required = KEY_N;
                            }
                        }
                        rule.append(ele).append(STR_NEXT_LINE);
                    }
                    checkRules = rule.toString();
                    if (StringUtils.isNotBlank(checkRules)) {
                        checkRules = checkRules.substring(0, checkRules.length() - 1);
                        buildRowCell(row, wrapTextCellStyle, 7, checkRules);
                    }
                }
                if (StringUtils.equals(required, KEY_Y)) {
                    buildRowCell(row, redCenterCellStyle, 6, required);
                } else {
                    buildRowCell(row, centerCellStyle, 6, required);
                }
                if (needAddBeginValidDate) {
                    buildBeginValidDateLine(componentDesc, rowIndex, wrapTextCellStyle, centerCellStyle, paramRealtimeApiTabDto);
                }
            }
        }
    }

    private void buildBeginValidDateLine(SXSSFSheet componentDesc, int rowIndex, CellStyle wrapTextCellStyle, CellStyle centerCellStyle, ParamRealtimeApiTabDto paramRealtimeApiTabDto) {
        SXSSFRow row = componentDesc.createRow(++rowIndex);
        buildRowCell(row, null, 0, paramRealtimeApiTabDto.getFieldName());
        buildRowCell(row, null, 1, "有效起始日期");
        buildRowCell(row, centerCellStyle, 2, COLUMN_TYPE_I);
        buildRowCell(row, null, 3, NAME_DESC_BEGIN_VALID_DATE);
        buildRowCell(row, centerCellStyle, 6, KEY_N);
        buildRowCell(row, wrapTextCellStyle, 7, "{\"required\":true,\"message\":\"有效起始日期必填\"}\n{\"validator\":\"isDate\",\"message\":\"日期格式不正确\"}");
    }

    private void buildDictDesc(SXSSFWorkbook workbook, ParamRealtimeDto paramRealtimeDto) {
        List<ParamRealtimeApiComponentDto> paramRealtimeApiComponentDtoList = paramRealtimeDto.getParamRealtimeApiComponentDtoList();
        Set<String> dict = new LinkedHashSet<>();
        for (ParamRealtimeApiComponentDto paramRealtimeApiComponentDto : paramRealtimeApiComponentDtoList) {
            if (StringUtils.equals(STR_0, paramRealtimeApiComponentDto.getTransType())) {
                dict.add(paramRealtimeApiComponentDto.getDictKey());
            }
        }
        SXSSFSheet dictDesc = workbook.createSheet("数据字典");
        CellStyle titleCellStyle = ExcelCommonUtils.getTitleCellStyle(workbook);

        SXSSFRow rowTitle = dictDesc.createRow(0);
        buildRowCell(rowTitle, titleCellStyle, 0, "字典项");
        buildRowCell(rowTitle, titleCellStyle, 1, "字典描述");
        buildRowCell(rowTitle, titleCellStyle, 2, "选项值");
        buildRowCell(rowTitle, titleCellStyle, 3, "选项描述");

        dictDesc.setColumnWidth(0, 20 * 256);
        dictDesc.setColumnWidth(1, 35 * 256);
        dictDesc.setColumnWidth(2, 20 * 256);
        dictDesc.setColumnWidth(3, 80 * 256);

        int rowIndex = 0;
        for (String item : dict) {
            if (StringUtils.isBlank(item)) {
                continue;
            }
            if (configDictValue.containsKey(item)) {
                Map<String, String> ele = configDictValue.get(item);
                for (Map.Entry<String, String> entry : ele.entrySet()) {
                    SXSSFRow row = dictDesc.createRow(++rowIndex);
                    buildRowCell(row, null, 0, item);
                    buildRowCell(row, null, 1, configDictName.get(item));
                    buildRowCell(row, null, 2, entry.getKey());
                    buildRowCell(row, null, 3, entry.getValue());
                }
            }
        }
    }

    private String getMultipleTabName(ParamRealtimeApiTabDto paramRealtimeApiTab) {
        Map<String, String> tab = new HashMap<>();
        tab.put("fundProductInfoSet", "基金信息");
        tab.put("fundAgencyInfoBase", "销售商信息");
        return tab.get(paramRealtimeApiTab.getMenuCode());
    }

    private void buildRequestDesc(SXSSFWorkbook workbook, ParamRealtimeDto paramRealtimeDto, File file) {
        List<ParamRealtimeApiTabDto> paramRealtimeApiTabList = paramRealtimeDto.getParamRealtimeApiTabList();
        List<ParamRealtimeApiComponentDto> paramRealtimeApiComponentDtoList = paramRealtimeDto.getParamRealtimeApiComponentDtoList();
        if (CollectionUtils.isNotEmpty(paramRealtimeApiTabList) && CollectionUtils.isNotEmpty(paramRealtimeApiComponentDtoList)) {
            ParamRealtimeApiTabDto paramRealtimeApiTab = paramRealtimeApiTabList.get(0);
            String sheetName = paramRealtimeApiTab.getTabName();
            boolean multipleTab = paramRealtimeApiTabList.size() > 1;
            if (multipleTab) {
                String multipleTabName = getMultipleTabName(paramRealtimeApiTab);
                if (StringUtils.isNotBlank(multipleTabName)) {
                    sheetName = multipleTabName;
                } else {
                    String fileName = file.getName();
                    String fileFolder = new File(file.getParent()).getName();
                    errorMultipleTabInfo.add(Arrays.asList(fileFolder, fileName, sheetName));
                }
            }
            SXSSFSheet requestDesc = workbook.createSheet(sheetName + "接口");
            CellStyle titleCellStyle = ExcelCommonUtils.getTitleCellStyle(workbook);
            SXSSFRow rowTitle = requestDesc.createRow(0);
            buildRowCell(rowTitle, titleCellStyle, 0, "字段");
            buildRowCell(rowTitle, titleCellStyle, 1, "描述");
            buildRowCell(rowTitle, titleCellStyle, 2, "备注");
            buildRowCell(rowTitle, titleCellStyle, 3, "必填");
            buildRowCell(rowTitle, titleCellStyle, 4, "类型");
            buildRowCell(rowTitle, titleCellStyle, 5, "长度");
            buildRowCell(rowTitle, titleCellStyle, 6, "格式");
            requestDesc.setColumnWidth(0, 20 * 256);
            requestDesc.setColumnWidth(1, 25 * 256);
            requestDesc.setColumnWidth(2, 90 * 256);

            List<ParamRealtimeRequestDescDto> paramRealtimeRequestDescList = new ArrayList<>();
            paramRealtimeRequestDescList.add(
                    new ParamRealtimeRequestDescDto("systemCode", "系统标识", "启用鉴权功能必填", KEY_N, COLUMN_TYPE_C, STR_BLANK, STR_BLANK)
            );
            paramRealtimeRequestDescList.add(
                    new ParamRealtimeRequestDescDto("mac", "mac地址", "鉴权功能使用", KEY_N, COLUMN_TYPE_C, STR_BLANK, STR_BLANK)
            );
            paramRealtimeRequestDescList.add(
                    new ParamRealtimeRequestDescDto("sign", "签名", "启用鉴权功能必填", KEY_N, COLUMN_TYPE_C, STR_BLANK, STR_BLANK)
            );
            paramRealtimeRequestDescList.add(
                    new ParamRealtimeRequestDescDto("username", "用户代码", "用户信息", KEY_Y, COLUMN_TYPE_C, STR_BLANK, STR_BLANK)
            );
            paramRealtimeRequestDescList.add(
                    new ParamRealtimeRequestDescDto("function", "接口代码", "推送接口标识", KEY_Y, COLUMN_TYPE_C, STR_BLANK, STR_BLANK)
            );
            paramRealtimeRequestDescList.add(
                    new ParamRealtimeRequestDescDto("action", "操作类型", "add:新增   edit:修改   delete:删除   sync:同步", KEY_Y, COLUMN_TYPE_C, STR_BLANK, STR_BLANK)
            );
            paramRealtimeRequestDescList.add(
                    new ParamRealtimeRequestDescDto("isOverWrite", "是否覆盖", "1:是   0:否", KEY_Y, COLUMN_TYPE_C, STR_BLANK, STR_BLANK)
            );
            paramRealtimeRequestDescList.add(
                    new ParamRealtimeRequestDescDto("lowId", "请求流水号", "删除和同步使用", KEY_N, COLUMN_TYPE_C, STR_BLANK, STR_BLANK)
            );
            paramRealtimeRequestDescList.add(
                    new ParamRealtimeRequestDescDto("data", "请求数据", "data中为参数的相关信息, 其为json格式, 主要数据由各个tab页中的数据组成", KEY_Y, COLUMN_TYPE_JSON, STR_BLANK, STR_BLANK)
            );
            CellStyle centerCellStyle = ExcelCommonUtils.getCenterCellStyle(workbook);
            CellStyle wrapTextCellStyle = ExcelCommonUtils.getWrapTextCellStyle(workbook);
            for (int i = 0; i < paramRealtimeRequestDescList.size(); i++) {
                SXSSFRow row = requestDesc.createRow(i + 1);
                ParamRealtimeRequestDescDto paramRealtimeInterfaceDesc = paramRealtimeRequestDescList.get(i);
                buildRowCell(row, null, 0, paramRealtimeInterfaceDesc.getCode());
                buildRowCell(row, null, 1, paramRealtimeInterfaceDesc.getDesc());
                buildRowCell(row, null, 2, paramRealtimeInterfaceDesc.getRemark());
                buildRowCell(row, centerCellStyle, 3, paramRealtimeInterfaceDesc.getRequire());
                buildRowCell(row, centerCellStyle, 4, paramRealtimeInterfaceDesc.getType());
                buildRowCell(row, null, 5, paramRealtimeInterfaceDesc.getLength());
                buildRowCell(row, null, 6, paramRealtimeInterfaceDesc.getFormat());
            }

            String beginValidDate = paramRealtimeApiTab.getFieldName();
            int currentLine = paramRealtimeRequestDescList.size() + 3;
            SXSSFRow rowUnique = requestDesc.createRow(currentLine);
            buildRowCell(rowUnique, titleCellStyle, 0, sheetName + "唯一性约束");
            requestDesc.addMergedRegion(new CellRangeAddress(currentLine, currentLine, 0, 6));

            for (ParamRealtimeApiTabDto item : paramRealtimeApiTabList) {
                String keyStr = item.getCheckName();
                if (StringUtils.isNotBlank(keyStr)) {
                    paramRealtimeDto.getPrimaryKey().put(item.getTabCode(), Arrays.asList(keyStr.split(STR_COMMA)));
                }
                String desc = "主键字段: ";
                if (multipleTab) {
                    desc = item.getTabName() + desc;
                }
                currentLine++;
                SXSSFRow rowUniqueDesc = requestDesc.createRow(currentLine);
                buildRowCell(rowUniqueDesc, null, 0, desc + keyStr);
                requestDesc.addMergedRegion(new CellRangeAddress(currentLine, currentLine, 0, 6));
                if (!multipleTab) {
                    break;
                }
            }

            boolean needAddBeginValidDate = StringUtils.isNotBlank(paramRealtimeApiTab.getFieldName()) && !beginValidDateSpecial.contains(paramRealtimeApiTab.getMenuCode());

            currentLine = currentLine + 3;
            SXSSFRow rowContent = requestDesc.createRow(currentLine);
            buildRowCell(rowContent, titleCellStyle, 0, "参考报文");
            requestDesc.addMergedRegion(new CellRangeAddress(currentLine, currentLine, 0, 6));
            currentLine++;
            List<String> requestContent = new ArrayList<>();
            List<String> head = new ArrayList<>();
            head.add("{");
            head.add("    \"请求地址\": \"http://127.0.0.1:8181/API/apiParam/api/entry\",");
            head.add("    \"参考报文说明\": \"新增或者修改使用,新增时action传值add,修改时传值edit\"");
            requestContent.add("{");
            requestContent.add("    \"systemCode\": \"xxxxxxxxxx\",");
            requestContent.add("    \"mac\": \"xxxxxxxxxx\",");
            requestContent.add("    \"sign\": \"xxxxxxxxxx\",");
            requestContent.add("    \"username\": \"admin\",");
            requestContent.add("    \"function\": \"" + paramRealtimeApiTab.getMenuCode() + "\",");
            requestContent.add("    \"action\": \"add\",");
            requestContent.add("    \"isOverWrite\": \"1\",");
            requestContent.add("    " + KEY_DATA);
            for (int j = 0; j < paramRealtimeApiTabList.size(); j++) {
                ParamRealtimeApiTabDto tab = paramRealtimeApiTabList.get(j);
                String tableCode = changeToLower(tab.getTableCode());
                requestContent.add("        \"" + tab.getTabCode() + "\": [");
                requestContent.add("            {");
                int lastIndex = 0;
                for (int i = 0; i < paramRealtimeApiComponentDtoList.size(); i++) {
                    lastIndex = paramRealtimeApiComponentDtoList.size() - 1;
                    ParamRealtimeApiComponentDto item = paramRealtimeApiComponentDtoList.get(i);
                    String fieldCode = changeToLower(item.getFieldCode());
                    if (skipColumns(tableCode, fieldCode)) {
                        if (i == lastIndex) {
                            deleteLastComma(requestContent);
                        }
                        continue;
                    }
                    if (!StringUtils.equals(tab.getTabCode(), item.getTabCode())) {
                        deleteLastComma(requestContent);
                        continue;
                    }
                    if (StringUtils.equals(beginValidDate, fieldCode) && needAddBeginValidDate) {
                        continue;
                    }
                    String line = "\"" + fieldCode + "\"" + ": " + (StringUtils.isNotBlank(item.getDefaultValue()) ? "\"" + item.getDefaultValue() + "\"" : "\"\"");
                    if (i != lastIndex) {
                        line += ",";
                    }
                    requestContent.add("                " + line);
                }
                /*if (needAddBeginValidDate) {
                    addBeginValidDate(requestContent, beginValidDate);
                }*/
                requestContent.add("            }");
                if (j != paramRealtimeApiTabList.size() - 1) {
                    requestContent.add("        ],");
                } else {
                    requestContent.add("        ]");
                }
                if (!multipleTab) {
                    break;
                }
            }
            requestContent.add("    }");
            requestContent.add("}");
            if (needAddBeginValidDate) {
                addBeginValidDate(head, beginValidDate);
                paramRealtimeDto.setBeginValidDate(beginValidDate);
            }
            head.add("}");
            requestContent.addAll(0, head);
            paramRealtimeDto.setRequestPartData(requestContent.stream().collect(Collectors.toList()));

            requestContent.add(STR_NEXT_LINE);

            requestContent.add("{");
            requestContent.add("    \"请求地址\": \"http://127.0.0.1:8181/API/apiParam/api/entry\",");
            requestContent.add("    \"参考报文说明\": \"删除使用\"");
            requestContent.add("}");
            requestContent.add("{");
            requestContent.add("    \"systemCode\": \"xxxxxxxxxx\",");
            requestContent.add("    \"mac\": \"xxxxxxxxxx\",");
            requestContent.add("    \"sign\": \"xxxxxxxxxx\",");
            requestContent.add("    \"username\": \"admin\",");
            requestContent.add("    \"function\": \"" + paramRealtimeApiTab.getMenuCode() + "\",");
            requestContent.add("    \"action\": \"delete\",");
            requestContent.add("    \"lowId\": \"xxxxxxxxxx\"");
            requestContent.add("}");

            requestContent.add(STR_NEXT_LINE);

            requestContent.add("{");
            requestContent.add("    \"请求地址\": \"http://127.0.0.1:8181/API/apiParam/api/sync\",");
            requestContent.add("    \"参考报文说明\": \"同步使用\"");
            requestContent.add("}");
            requestContent.add("{");
            requestContent.add("    \"systemCode\": \"xxxxxxxxxx\",");
            requestContent.add("    \"mac\": \"xxxxxxxxxx\",");
            requestContent.add("    \"sign\": \"xxxxxxxxxx\",");
            requestContent.add("    \"username\": \"admin\",");
            requestContent.add("    \"function\": \"" + paramRealtimeApiTab.getMenuCode() + "\",");
            requestContent.add("    \"action\": \"sync\",");
            requestContent.add("    \"lowId\": \"xxxxxxxxxx\"");
            requestContent.add("}");

            paramRealtimeDto.setRequestContent(requestContent);
            SXSSFRow row = requestDesc.createRow(currentLine);
            buildRowCell(row, wrapTextCellStyle, 0, requestContent.stream().collect(Collectors.joining(STR_NEXT_LINE)));
            requestDesc.addMergedRegion(new CellRangeAddress(currentLine, currentLine + requestContent.size() - 1, 0, 6));
        }
    }

    private void deleteLastComma(List<String> requestContent) {
        int lastIndex = requestContent.size() - 1;
        String lastLine = requestContent.get(lastIndex);
        if (lastLine.endsWith(STR_COMMA)) {
            requestContent.set(lastIndex, lastLine.substring(0, lastLine.indexOf(STR_COMMA)));
        }
    }
    private void addBeginValidDate(List<String> requestContent, String beginValidDate) {
        int lastIndex = requestContent.size() - 1;
        String lastLine = requestContent.get(lastIndex);
        if (!lastLine.endsWith(STR_COMMA)) {
            lastLine += STR_COMMA;
        }
        lastLine += STR_NEXT_LINE + "    \"" + beginValidDate + "\": \"" + NAME_DESC_BEGIN_VALID_DATE + "\"";
        requestContent.set(lastIndex, lastLine);
    }

    private void buildInterfaceDesc(SXSSFWorkbook workbook) {
        SXSSFSheet interfaceDesc = workbook.createSheet("接口约定");
        CellStyle titleCellStyle = ExcelCommonUtils.getTitleCellStyle(workbook);
        interfaceDesc.setColumnWidth(0, 15 * 256);
        interfaceDesc.setColumnWidth(1, 10 * 256);
        interfaceDesc.setColumnWidth(2, 100 * 256);

        SXSSFRow rowTitle = interfaceDesc.createRow(0);
        buildRowCell(rowTitle, titleCellStyle, 0, "约定名称");
        buildRowCell(rowTitle, titleCellStyle, 1, "编号");
        buildRowCell(rowTitle, titleCellStyle, 2, "约定内容");

        List<ParamRealtimeInterfaceDescDto> paramRealtimeInterfaceDescList = new ArrayList<>();
        paramRealtimeInterfaceDescList.add(new ParamRealtimeInterfaceDescDto("接口约定", "1", "请求方式必须是post"));
        paramRealtimeInterfaceDescList.add(new ParamRealtimeInterfaceDescDto(STR_BLANK, "2", "字符编码使用utf-8"));
        paramRealtimeInterfaceDescList.add(new ParamRealtimeInterfaceDescDto(STR_BLANK, "3",
                "请求头部必须增加属性\n" +
                        "    Content-Type=application/json\n" +
                        "    X-Requested-With=XMLHttpRequest"));
        paramRealtimeInterfaceDescList.add(new ParamRealtimeInterfaceDescDto(STR_BLANK, "4",
                "请求内容必须是json格式的内容\n" +
                        "    java示例:\n" +
                        "        DefaultHttpClient  httpClient = new DefaultHttpClient();\n" +
                        "        HttpPost method = new HttpPost();\n" +
                        "        method.setHeader(\"Content-type\", \"application/json; charset=utf-8\");\n" +
                        "        method.setHeader(\"X-Requested-With\", \"XMLHttpRequest\");\n" +
                        "        String jsonStr = \"{}\";\n" +
                        "        String uri = \"\";\n" +
                        "        StringEntity entity = new StringEntity(jsonStr, \"utf-8\");\n" +
                        "        entity.setContentEncoding(\"UTF-8\");\n" +
                        "        entity.setContentType(\"application/json\");\n" +
                        "        method.setURI(URI.create(uri));\n" +
                        "        method.setEntity(entity);\n" +
                        "        HttpResponse response = httpClient.execute(method);"));
        paramRealtimeInterfaceDescList.add(new ParamRealtimeInterfaceDescDto(STR_BLANK, "5",
                "字段类型说明\n" +
                        "    C: 字符\n" +
                        "    N: 数字\n" +
                        "    I:   整数\n" +
                        "    JSON: JSON"));
        CellStyle centerCellStyle = ExcelCommonUtils.getCenterCellStyle(workbook);
        CellStyle wrapTextCellStyle = ExcelCommonUtils.getWrapTextCellStyle(workbook);
        for (int i = 0; i < paramRealtimeInterfaceDescList.size(); i++) {
            SXSSFRow row = interfaceDesc.createRow(i + 1);
            ParamRealtimeInterfaceDescDto paramRealtimeInterfaceDesc = paramRealtimeInterfaceDescList.get(i);
            buildRowCell(row, centerCellStyle, 0, paramRealtimeInterfaceDesc.getAgreeName());
            buildRowCell(row, centerCellStyle, 1, paramRealtimeInterfaceDesc.getAgreeIndex());
            buildRowCell(row, wrapTextCellStyle, 2, paramRealtimeInterfaceDesc.getAgreeContent());
        }

        interfaceDesc.addMergedRegion(new CellRangeAddress(1, 5, 0, 0));
    }

    private void buildRowCell(SXSSFRow row, CellStyle cellStyle, int rowIndex, String rowName) {
        SXSSFCell cell = row.createCell(rowIndex);
        if (cellStyle != null) {
            cell.setCellStyle(cellStyle);
        }
        cell.setCellValue(rowName);
    }
}
