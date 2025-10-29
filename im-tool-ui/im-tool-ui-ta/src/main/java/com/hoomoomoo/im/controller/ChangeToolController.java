package com.hoomoomoo.im.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.*;
import com.hoomoomoo.im.extend.ScriptSqlUtils;
import com.hoomoomoo.im.task.ChangeFunctionTask;
import com.hoomoomoo.im.task.ChangeFunctionTaskParam;
import com.hoomoomoo.im.utils.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.CHANGE_TOOL;

/**
 * @author humm23693
 * @description
 * @package com.hoomoomoo.im.controller
 * @date 2021/9/16
 */
public class ChangeToolController implements Initializable {

    @FXML
    private TextArea logs;

    @FXML
    private AnchorPane autoModePane;

    @FXML
    private AnchorPane menuModePane;

    @FXML
    private Button autoModeBtn;

    @FXML
    private Button menuCodeBtn;

    @FXML
    private TextField code;

    @FXML
    private TextField baseDictPath;

    @FXML
    private TextField paramRealtimeSetPath;

    @FXML
    private Button executeRealtimeBtn;

    @FXML
    private ComboBox dbNum;

    private static String TA_CODE = "00";

    Map<String, Map<String, String>> configDictValue = new LinkedHashMap<>();
    Map<String, String> configDictName = new LinkedHashMap<>();
    Set<String> beginValidDateSpecial = new HashSet<>();

    /**
     * hy 行业 0:参数提示 1:基金行业 2:证券行业 3:个性化行业
     * taCode ta代码
     * gm 公募
     * zx 中信
     * xy 兴业
     * zj 中金
     * gjdf 国金道富
     * gtht 国泰海通
     * sm 分产品自动化
     * navType 分产品自动化清算行情导入方式
     */
    Map<String, String[]> autoModeValue = new LinkedHashMap<>();
    List<RadioButton> buttonList = new ArrayList<>();

    /**
     * newUd
     * all
     */
    Map<String, Boolean[]> menuModeValue = new LinkedHashMap<>();
    List<RadioButton> menuButtonList = new ArrayList<>();

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String msg = String.format(BaseConst.MSG_USE, CHANGE_TOOL.getName());
        LoggerUtils.info(msg);
        LoggerUtils.writeLogInfo(CHANGE_TOOL.getCode(), new Date(), new ArrayList<String>(){{
            add(msg);
        }});
        initAutoMode();
        initMenuMode();
        initDb();

        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        OutputUtils.info(baseDictPath, appConfigDto.getChangeToolBaseDictPath());
        OutputUtils.info(paramRealtimeSetPath, appConfigDto.getChangeToolParamRealtimeSetPath());
    }

    private void initAutoMode() {
        autoModeValue.put("参数提示", new String[]{STR_0});

        autoModeValue.put("基金行业", new String[]{STR_1, TA_CODE, STR_1, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0});
        autoModeValue.put("嘉实基金", new String[]{STR_1, "07", STR_1, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0});
        autoModeValue.put("建信基金", new String[]{STR_1, "53", STR_1, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0});
        autoModeValue.put("泰康基金", new String[]{STR_1, "4C", STR_1, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0});

        autoModeValue.put("证券行业", new String[]{STR_2, TA_CODE, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0, STR_1, STR_1});
        autoModeValue.put("国泰海通", new String[]{STR_2, "JA", STR_0, STR_0, STR_0, STR_0, STR_0, STR_0, STR_1, STR_0});
        autoModeValue.put("东方证券", new String[]{STR_2, "SD", STR_0, STR_0, STR_0, STR_0, STR_0, STR_0, STR_1, STR_1});
        autoModeValue.put("申万宏源", new String[]{STR_2, "SA", STR_0, STR_0, STR_0, STR_0, STR_0, STR_0, STR_1, STR_1});
        autoModeValue.put("广发证券", new String[]{STR_2, "87", STR_0, STR_0, STR_0, STR_0, STR_0, STR_0, STR_1, STR_1});

        autoModeValue.put("中信证券", new String[]{STR_3, "S5", STR_0, STR_1, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0});
        autoModeValue.put("兴业证券", new String[]{STR_3, "XY", STR_0, STR_0, STR_1, STR_0, STR_0, STR_0, STR_0, STR_0});
        autoModeValue.put("中金公司", new String[]{STR_3, "SM", STR_0, STR_0, STR_0, STR_1, STR_0, STR_0, STR_0, STR_0});
        autoModeValue.put("国金道富", new String[]{STR_3, "NB", STR_0, STR_0, STR_0, STR_0, STR_1, STR_0, STR_0, STR_0});

        Map<String, Set<String>> group = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> entry : autoModeValue.entrySet()) {
            String name = entry.getKey();
            String hy = entry.getValue()[0];
            if (group.containsKey(hy)) {
                group.get(hy).add(name);
            } else {
                Set<String> select = new LinkedHashSet<>();
                select.add(name);
                group.put(hy, select);
            }
        }

        double layoutY = 70;
        ToggleGroup toggleGroup = new ToggleGroup();
        for (Map.Entry<String, Set<String>> entry : group.entrySet()) {
            String hyName = entry.getKey();
            switch (entry.getKey()) {
                case STR_0:
                    hyName = "参数提示";
                    break;
                case STR_1:
                    hyName = "基金行业";
                    break;
                case STR_2:
                    hyName = "证券行业";
                    break;
                case STR_3:
                    hyName = "个性化";
                    break;
                default:
                    break;
            }
            Label label = new Label(hyName);
            label.setStyle(STYLE_BOLD);
            label.setLayoutX(35);
            label.setLayoutY(layoutY);
            Set<String> select = entry.getValue();
            HBox hBox = new HBox(10);
            hBox.setLayoutX(110);
            hBox.setLayoutY(layoutY);
            layoutY += 30;
            for (String buttonName : select) {
                RadioButton button = new RadioButton(buttonName);
                button.setToggleGroup(toggleGroup);
                hBox.getChildren().add(button);
                buttonList.add(button);
            }
            autoModePane.getChildren().addAll(label, hBox);
        }
        buildTips();
    }

    private void initMenuMode() {
        menuModeValue.put("新版", new Boolean[]{true, false});
        menuModeValue.put("老版", new Boolean[]{false, false});
        menuModeValue.put("老版(全部)", new Boolean[]{false, true});

        ToggleGroup toggleGroup = new ToggleGroup();
        HBox hBox = new HBox(10);
        hBox.setLayoutX(37);
        hBox.setLayoutY(70);

        for (Map.Entry<String, Boolean[]> entry: menuModeValue.entrySet()) {
            RadioButton button = new RadioButton(entry.getKey());
            button.setToggleGroup(toggleGroup);
            hBox.getChildren().add(button);
            menuButtonList.add(button);
        }
        menuModePane.getChildren().addAll(hBox);
    }

    private void initDb() {
        ObservableList db = dbNum.getItems();
        db.add(STR_2);
        db.add(STR_4);
        db.add(STR_8);
        db.add(STR_16);
        dbNum.getSelectionModel().select(0);
    }

    private void buildTips() {
        OutputUtils.clearLog(logs);
        StringBuilder tips = new StringBuilder();
        tips.append(buildTipsMessage("基金行业", "fund_MultiProcessesLiqDeal"));
        tips.append(buildTipsMessage("证券行业", "fund_AutoLiqByPrd"));
        tips.append(buildTipsMessage("国泰海通", "fund_JaSpecialDeal"));
        tips.append(buildTipsMessage("中信模式", "fund_T1MultiProcessesLiqDeal"));
        tips.append(buildTipsMessage("中信证券", "fund_ParamProcessesLiqDeal"));
        tips.append(buildTipsMessage("兴业模式", "fund_XyMultiProcessesLiqDeal"));
        tips.append(buildTipsMessage("兴业证券", "fund_XyMultiProcessesPrivate"));
        tips.append(buildTipsMessage("中金公司", "fund_ZjMultiProcessesPrivate"));
        OutputUtils.info(logs, tips.toString());
    }

    private String buildTipsMessage(String mode, String val) {
        return "【" + mode + "】" + " 个性化参数 " + "【" + val + "】" + STR_NEXT_LINE_2;
    }

    @FXML
    void executeAutoMode(ActionEvent event) {
        try {
            OutputUtils.clearLog(logs);
            String mode = STR_BLANK;
            for (RadioButton radioButton : buttonList) {
                if (radioButton.isSelected()) {
                    mode = radioButton.getText();
                    break;
                }
            }
            if (StringUtils.isBlank(mode)) {
                OutputUtils.info(logs,"请选择 自动化模式");
                return;
            }
            TaskUtils.execute(new ChangeFunctionTask(new ChangeFunctionTaskParam(this, STR_1, mode)));
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, e.getMessage());
        } finally {
            enableBtn();
        }
    }

    @FXML
    void executeMenuMode(ActionEvent event) {
        try {
            OutputUtils.clearLog(logs);
            String menu = STR_BLANK;
            for (RadioButton radioButton : menuButtonList) {
                if (radioButton.isSelected()) {
                    menu = radioButton.getText();
                    break;
                }
            }
            if (StringUtils.isBlank(menu)) {
                OutputUtils.info(logs,"请选择 菜单模式");
                return;
            }
            TaskUtils.execute(new ChangeFunctionTask(new ChangeFunctionTaskParam(this, STR_2, menu)));
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, e.getMessage());
        } finally {
            enableBtn();
        }
    }

    @FXML
    void showModeResult(ActionEvent event) throws IOException {
        Runtime.getRuntime().exec("explorer /e,/select," + new File(FileUtils.getFilePath(FILE_CHANGE_MODE)).getAbsolutePath());
    }

    @FXML
    void showMenuResult(ActionEvent event) throws IOException {
        Runtime.getRuntime().exec("explorer /e,/select," + new File(FileUtils.getFilePath(FILE_CHANGE_MENU)).getAbsolutePath());
    }

    @FXML
    void executeDbBtn(ActionEvent event) throws IOException {
        OutputUtils.clearLog(logs);
        String codeIn =  CommonUtils.getComponentValue(code);
        String dbNumIn =  CommonUtils.getComponentValue(dbNum);
        if (StringUtils.isBlank(codeIn) || codeIn.length() < 8) {
            OutputUtils.infoContainBr(logs, "基金账号/内部客户号 至少输入8位");
        }
        if (StringUtils.isBlank(dbNumIn)) {
            OutputUtils.infoContainBr(logs, "分库数量 不能为空");
        }
        codeIn = codeIn.substring(codeIn.length() -8);
        int hash = 0;
        int len = codeIn.length();
        int accChar;
        int charNum;
        for (int i=0; i<len; ++i) {
            accChar = codeIn.charAt(i);
            charNum = 0;
            if (Character.isDigit(accChar)) {
                charNum = accChar - '0';
            } else if (Character.isUpperCase(accChar)) {
                charNum = (accChar - 'A') % 10;
            } else if (Character.isLowerCase(accChar)) {
                charNum =(accChar -'a') % 10;
            }
            hash = hash * 10 + charNum;
        }
        int codeValue = Math.abs(hash);
        int dbNumValue = Integer.parseInt(dbNumIn);
        OutputUtils.infoContainBr(logs, "分库号: " + ((codeValue % dbNumValue) + 1));
        OutputUtils.infoContainBr(logs, "分表号: " + ((codeValue / dbNumValue) % 16 + 1));
    }

    private void disableBtn() {
        autoModeBtn.setDisable(true);
        menuCodeBtn.setDisable(true);
    }

    private void enableBtn() {
        autoModeBtn.setDisable(false);
        menuCodeBtn.setDisable(false);
    }

    private void executeStart(String taskMsg) {
        disableBtn();
        OutputUtils.clearLog(logs);
        OutputUtils.infoContainBr(logs, taskMsg + STR_NEXT_LINE);
        OutputUtils.infoContainBr(logs, "生成脚本 开始 ...");
    }

    private void executeEnd(String resFilePath) throws Exception {
        OutputUtils.infoContainBr(logs, STR_NEXT_LINE + "生成脚本 完成 ..." + STR_NEXT_LINE);
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        if (StringUtils.isNotBlank(appConfigDto.getDatabaseScriptUrl())) {
            OutputUtils.infoContainBr(logs, "执行脚本 开始 ...");
            String sql = STR_BLANK;
            try {
                String[] sqlList = FileUtils.readNormalFileToStringSkipAnnotation(resFilePath).split(STR_SEMICOLON);
                if (sqlList != null) {
                    int size = sqlList.length;
                    OutputUtils.info(logs, "执行中 ...");
                    for (int i=0; i<size; i++) {
                        sql = sqlList[i].trim();
                        if (i % 1000 == 0) {
                            OutputUtils.info(logs, STR_POINT_3);
                        }
                        DatabaseUtils.executeSql(sql, null);
                    }
                }
            } catch (IOException e) {
                LoggerUtils.info(e);
                OutputUtils.info(logs, e.getMessage());
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(logs, STR_NEXT_LINE_2 + e.getMessage());
                OutputUtils.info(logs, "执行异常sql: " + sql + STR_NEXT_LINE);
                return;
            } finally {
                DatabaseUtils.closeConnection();
                enableBtn();
            }
            OutputUtils.infoContainBr(logs, STR_NEXT_LINE + "执行脚本 完成 ..." + STR_NEXT_LINE);
            OutputUtils.infoContainBr(logs, "请刷新系统缓存 ...");
        } else {
            enableBtn();
        }
    }

    public void buildMenuModeSql(String taskType) throws Exception {
        Boolean[] param = menuModeValue.get(taskType);
        if (param == null) {
            OutputUtils.repeatInfo(logs, "未匹配执行方法，请检查");
            new Exception("未匹配执行方法，请检查");
        }
        buildMenuSql(taskType, param[0], param[1]);
    }

    public void buildMenuSql( String taskType, boolean newUd, boolean all) throws Exception {
        executeStart(taskType);
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String base = ScriptSqlUtils.baseMenu;
        String paramValue = STR_0;
        if (newUd) {
            base = ScriptSqlUtils.newUedPage;
            paramValue = STR_1;
        }
        String basePath = appConfigDto.getSystemToolCheckMenuFundBasePath() + base;
        List<String> res = new ArrayList<>();
        res.add("-- " + taskType);
        res.add("-- 更新系统参数");
        res.add("update tbparam set param_value = '" + paramValue + "' where param_id = 'IsNewMenuIndex';\n");
        List<String> sqlList = FileUtils.readNormalFile(basePath);
        String resFilePath = FileUtils.getFilePath(FILE_CHANGE_MENU);
        FileUtils.deleteFile(new File(resFilePath));
        FileUtils.writeFile(resFilePath, res);
        res.clear();
        if (CollectionUtils.isNotEmpty(sqlList)) {
            int size = sqlList.size();
            OutputUtils.info(logs, "执行中 ...");
            for (int i=0; i<size; i++) {
                String sql = sqlList.get(i);
                if (!all && sql.contains("交易码  tsys_trans")) {
                    res.add("commit;");
                    break;
                }
                if (!all && !newUd && (sql.contains("tsys_trans") || sql.contains("tsys_subtrans"))) {
                    continue;
                }

                if (i % 1000 == 0) {
                    OutputUtils.info(logs, STR_POINT_3);
                    FileUtils.writeFileAppend(resFilePath, res);
                    res.clear();
                }
                String sqlLower = sql.toLowerCase();
                boolean validSql = sqlLower.contains("delete") || sqlLower.contains("insert") || sqlLower.contains("values");
                if (sql.startsWith(ANNOTATION_NORMAL) && validSql) {
                    sql = sql.replace(ANNOTATION_NORMAL + STR_SPACE, STR_BLANK);
                }
                res.add(sql);
            }
        }
        if (CollectionUtils.isNotEmpty(res)) {
            FileUtils.writeFileAppend(resFilePath, res);
        }
        executeEnd(resFilePath);
    }

    public void buildAutoModeSql(String taskType) throws Exception {
       String[] param = autoModeValue.get(taskType);
        if (param == null) {
            OutputUtils.repeatInfo(logs, "未匹配执行方法，请检查");
            new Exception("未匹配执行方法，请检查");
        }
        if (param.length == 1) {
            buildTips();
        } else {
            TA_CODE = param[1];
            buildAutoModeSql(taskType, param[2], param[3], param[4], param[5], param[6], param[7], param[8], param[9]);
        }
    }

    public void buildAutoModeSql(String taskType, String gm, String zx, String xy, String zj, String gjdf,
                                 String gtht, String sm, String navType) throws Exception {
        boolean xyMode =  STR_1.equals(xy) || STR_1.equals(zj) || STR_1.equals(gjdf);
        executeStart(taskType);
        OutputUtils.info(logs, "执行中 ...");
        List<String> res = new ArrayList<>();
        res.add("-- " + taskType + "\n");

        res.add("-- 开通实时并发清算功能(基金行业)");
        res.add("update tbparam set param_value = '" + gm + "' where param_id = 'fund_MultiProcessesLiqDeal';\n");

        res.add("-- 按照产品日切清算(中信自动化清算模式)");
        if (STR_1.equals(zx) || xyMode) {
            res.add("update tbparam set param_value = '1' where param_id = 'fund_T1MultiProcessesLiqDeal';\n");
        } else {
            res.add("update tbparam set param_value = '0' where param_id = 'fund_T1MultiProcessesLiqDeal';\n");
        }

        res.add("-- 开通参数日期管理功能(中信特有功能)");
        if (STR_1.equals(zx)) {
            res.add("update tbparam set param_value = '1' where param_id = 'fund_ParamProcessesLiqDeal';\n");
        } else {
            res.add("update tbparam set param_value = '0' where param_id = 'fund_ParamProcessesLiqDeal';\n");
        }

        res.add("-- 兴业自动化清算特有功能(兴业特有功能)");
        res.add("update tbparam set param_value = '" + xy + "' where param_id = 'fund_XyMultiProcessesPrivate';\n");

        res.add("-- 清算列表外部发起(兴业自动化清算模式)");
        if (xyMode) {
            res.add("update tbparam set param_value = '1' where param_id = 'fund_XyMultiProcessesLiqDeal';\n");
        } else {
            res.add("update tbparam set param_value = '0' where param_id = 'fund_XyMultiProcessesLiqDeal';\n");
        }

        res.add("-- 开通分产品自动化清算功能(证券行业)");
        res.add("update tbparam set param_value = '" + sm + "' where param_id = 'fund_AutoLiqByPrd';\n");

        res.add("-- 分产品自动化清算行情导入方式");
        res.add("update tbparam set param_value = '" + navType + "' where param_id = 'fund_autoLiqImpNavType';\n");

        res.add("-- 国泰海通特殊处理功能(国君特有功能)");
        if (STR_1.equals(gtht)) {
            res.add("update tbparam set param_value = '1' where param_id = 'fund_JaSpecialDeal';\n");
        } else {
            res.add("update tbparam set param_value = '0' where param_id = 'fund_JaSpecialDeal';\n");
        }

        res.add("-- 开通中金模式自动化清算功能(中金特有功能)");
        res.add("update tbparam set param_value = '" + zj + "' where param_id = 'fund_ZjMultiProcessesPrivate';\n");

        String gfzq = StringUtils.equals("87", TA_CODE) ? STR_1 : STR_0;

        res.add("-- 开通自动化清算支持固定批次处理功能(广发证券特有功能)");
        res.add("update tbparam set param_value = '" + gfzq + "' where param_id = 'fund_AutoLiqSptFixedBatch';\n");

        res.add("-- 开通自动化清算隐藏基金状态和账户处理节点功能(广发证券特有功能)");
        res.add("update tbparam set param_value = '" + gfzq + "' where param_id = 'fund_AutoLiqHideStatusSet';\n");

        res.add("-- 开通自动化数据自动导入功能(广发证券特有功能)");
        res.add("update tbparam set param_value = '" + gfzq + "' where param_id = 'fund_MultiProcessesDataAutoImp';\n");

        res.add("-- 开通自动化清算支持T0产品清算功能(广发证券特有功能)");
        res.add("update tbparam set param_value = '" + gfzq + "' where param_id = 'fund_AutoLiqSptT0Deal';\n");

        res.add("-- 开通自动化清算支持外部API稽核功能(广发证券特有功能)");
        res.add("update tbparam set param_value = '" + gfzq + "' where param_id = 'fund_AutoLiqSptApiAudit';\n");

        res.add("-- 开通自动化清算根据交易列表处理功能(广发证券特有功能)");
        res.add("update tbparam set param_value = '" + gfzq + "' where param_id = 'fund_AutoLiqByTradeList';\n");

        res.add("-- 开通资金清算只导出T0确认文件功能(广发证券特有功能)");
        res.add("update tbparam set param_value = '" + gfzq + "' where param_id = 'fund_ZjqsExpT0CfmFile';\n");

        res.add("-- 开通销售商预设批次导出功能(广发证券特有功能)");
        res.add("update tbparam set param_value = '" + gfzq + "' where param_id = 'fund_AgencyPreExport';\n");

        res.add("-- 开通OTC登记托管功能(广发证券特有功能)");
        res.add("update tbparam set param_value = '" + gfzq + "' where param_id = 'fund_OTCTransferAgent';\n");

        res.add("-- 更新TA代码");
        res.add("update tbparam set param_value = '" + TA_CODE + "' where param_id = 'BTACODE';\n");


        res.add(STR_SPACE);
        String groupCode = STR_BLANK;
        String groupName = "1主流程";
        if (STR_1.equals(gm)) {
            groupCode = "fund_daily_virtual_multi";
        } else if (STR_1.equals(zx)) {
            groupCode = "fund_daily_t1_multi";
        } else if (xyMode) {
            groupCode = "fund_daily_xyt1_multi";
            groupName = "1自动化清算";
        } else if (STR_1.equals(sm)) {
            groupCode = "fund_daily_auto_liq_byprd";
        }
        StringBuilder group = new StringBuilder();
        res.add("-- 流程信息");
        group.append("delete from tbschedulegroup where sche_group_type = '1';\n");
        group.append("insert into tbschedulegroup (sche_page_code, sche_group_code, sche_group_name, sche_group_isuse, sche_group_type) \n");
        group.append("values ('fund_schedule' , '" + groupCode + "' , '" + groupName + "' , '1' , '1');\n");
        res.add(group.toString());
        res.add(STR_SPACE);
        res.add("commit;");
        String resFilePath = FileUtils.getFilePath(FILE_CHANGE_MODE);
        FileUtils.deleteFile(new File(resFilePath));
        if (CollectionUtils.isNotEmpty(res)) {
            FileUtils.writeFileAppend(resFilePath, res);
        }
        executeEnd(resFilePath);
    }

    @FXML
    void executeRealtime(ActionEvent event) {
        OutputUtils.clearLog(logs);
        try {
            String dictPath = baseDictPath.getText();
            if (StringUtils.isBlank(dictPath)) {
                OutputUtils.info(logs, "请设置字典全量脚本文件");
                return;
            }
            if (new File(dictPath).isDirectory()) {
                OutputUtils.info(logs, "字典全量脚本文件必须为文件");
                return;
            }
            String paramPath = paramRealtimeSetPath.getText();
            if (StringUtils.isBlank(paramPath)) {
                OutputUtils.info(logs, "参数电子化目录位置");
                return;
            }
            if (!new File(paramPath).isDirectory()) {
                OutputUtils.info(logs, "参数电子化目录必须为文件夹");
                return;
            }
            TaskUtils.execute(new ChangeFunctionTask(new ChangeFunctionTaskParam(this, STR_3, dictPath, paramPath)));
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.infoContainBr(logs, e.getMessage());
        }
    }

    public void executeRealtimeExe(String dictPath, String paramPath) {
        try {
            executeRealtimeBtn.setDisable(true);
            OutputUtils.infoContainBr(logs, "初始化字典信息 开始");
            initConfigInfo();
            initBaseDict(dictPath);
            initExtDict(paramPath);
            OutputUtils.infoContainBr(logs, "初始化字典信息 结束");
            OutputUtils.infoContainBr(logs, "生成文件 开始");
            buildFile(paramPath);
            OutputUtils.infoContainBr(logs, "生成文件 结束");
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.infoContainBr(logs, e.getMessage());
        } finally {
            executeRealtimeBtn.setDisable(false);
        }
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
                            // LoggerUtils.info("数据字典格式化获取数据错误: " + eleAfter);
                            continue;
                        }
                        String dictCode = ScriptSqlUtils.getSqlFieldValue(dictInfo[0].replace(STR_BRACKETS_LEFT, STR_BLANK));
                        String dictName = ScriptSqlUtils.getSqlFieldValue(dictInfo[1]);
                        String dictKey = ScriptSqlUtils.getSqlFieldValue(dictInfo[2]);
                        String dictPrompt = ScriptSqlUtils.getSqlFieldValue(dictInfo[3]);
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
                                LoggerUtils.info("格式化获取数据错误: " + ele);
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
                                LoggerUtils.info("格式化获取数据错误: " + ele);
                                continue;
                            }
                            ParamRealtimeApiComponentDto paramRealtimeApiComponentDto = new ParamRealtimeApiComponentDto();
                            paramRealtimeApiComponentDtoList.add(paramRealtimeApiComponentDto);
                            paramRealtimeApiComponentDto.setTabCode(ScriptSqlUtils.getSqlFieldValue(sqlInfo[0].replace(STR_BRACKETS_LEFT, STR_BLANK)));
                            paramRealtimeApiComponentDto.setFieldCode(ScriptSqlUtils.getSqlFieldValue(sqlInfo[1]));
                            paramRealtimeApiComponentDto.setFieldName(ScriptSqlUtils.getSqlFieldValue(sqlInfo[2]));
                            paramRealtimeApiComponentDto.setTransType(ScriptSqlUtils.getSqlFieldValue(sqlInfo[3]));
                            paramRealtimeApiComponentDto.setDictKey(ScriptSqlUtils.getSqlFieldValue(sqlInfo[4]));
                            paramRealtimeApiComponentDto.setCheckRules(ScriptSqlUtils.getSqlFieldValue(sqlInfo[5].replace(STR_BRACKETS_RIGHT, STR_BLANK)));
                            if (sqlInfo.length == 8) {
                                paramRealtimeApiComponentDto.setOrderField(ScriptSqlUtils.getSqlFieldValue(sqlInfo[6]));
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
            if (!path.endsWith(".sql")) {
                return;
            }
            OutputUtils.infoContainBr(logs, "处理文件: " +  path);
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
        buildRequestDesc(workbook, paramRealtimeDto);
        buildComponentDesc(workbook, paramRealtimeDto);
        buildDictDesc(workbook, paramRealtimeDto);
        buildFile(workbook, paramRealtimeDto, filePath);
    }

    private void buildFile(SXSSFWorkbook workbook, ParamRealtimeDto paramRealtimeDto, String filePath) throws IOException {
        String excelFilePath = filePath.replace(".sql", ".xlsx");
        String jsonFilePath = filePath.replace(".sql", ".json");
        FileUtils.writeFile(jsonFilePath, paramRealtimeDto.getRequestContent());
        FileOutputStream fileOutputStream = new FileOutputStream(excelFilePath);
        workbook.write(fileOutputStream);
        workbook.dispose();
    }

    private void buildComponentDesc(SXSSFWorkbook workbook, ParamRealtimeDto paramRealtimeDto) throws Exception {
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
            buildRowCell(rowTitle, titleCellStyle, 2, "数据字典");
            buildRowCell(rowTitle, titleCellStyle, 3, "默认值");
            buildRowCell(rowTitle, titleCellStyle, 4, "必填");
            buildRowCell(rowTitle, titleCellStyle, 5, "校验规则");

            componentDesc.setColumnWidth(0, 35 * 256);
            componentDesc.setColumnWidth(1, 35 * 256);
            componentDesc.setColumnWidth(2, 20 * 256);
            componentDesc.setColumnWidth(3, 20 * 256);
            componentDesc.setColumnWidth(4, 10 * 256);
            componentDesc.setColumnWidth(5, 200 * 256);

            CellStyle centerCellStyle = ExcelCommonUtils.getCenterCellStyle(workbook);
            CellStyle wrapTextCellStyle = ExcelCommonUtils.getWrapTextCellStyle(workbook);

            int rowIndex = 0;
            for (ParamRealtimeApiComponentDto item : paramRealtimeApiComponentDtoList) {
                boolean needAdd = StringUtils.isNotBlank(paramRealtimeApiTabDto.getFieldName()) && !beginValidDateSpecial.contains(paramRealtimeApiTabDto.getMenuCode());
                if (StringUtils.equals(paramRealtimeApiTabDto.getFieldName(), item.getFieldCode()) && needAdd) {
                    continue;
                }
                if (!StringUtils.equals(tabCode, item.getTabCode())) {
                    continue;
                }
                SXSSFRow row = componentDesc.createRow(++rowIndex);
                buildRowCell(row, null, 0, item.getFieldCode());
                buildRowCell(row, null, 1, item.getFieldName());
                if (StringUtils.equals(STR_0, item.getTransType()) && StringUtils.isNotBlank(item.getDictKey())) {
                    buildRowCell(row, null, 2, item.getDictKey());
                }
                if (StringUtils.isNotBlank(item.getDefaultValue())) {
                    buildRowCell(row, null, 3, item.getDefaultValue());
                }
                String checkRules = item.getCheckRules();
                String required = KEY_N;
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
                        if (ele.contains("\"required\"") && ele.contains("true")) {
                            required = KEY_Y;
                        } else if (ele.contains("\"checkIsRequired\"")) {
                            required = KEY_Y;
                        }
                        rule.append(ele).append(STR_NEXT_LINE);
                    }
                    checkRules = rule.toString();
                    if (StringUtils.isNotBlank(checkRules)) {
                        checkRules = checkRules.substring(0, checkRules.length() - 1);
                        buildRowCell(row, wrapTextCellStyle, 5, checkRules);
                    }
                }
                buildRowCell(row, centerCellStyle, 4, required);
                if (needAdd) {
                    buildBeginValidDateLine(componentDesc, rowIndex, wrapTextCellStyle, centerCellStyle, paramRealtimeApiTabDto);
                }
            }
        }
    }

    private void buildBeginValidDateLine(SXSSFSheet componentDesc, int rowIndex, CellStyle wrapTextCellStyle, CellStyle centerCellStyle, ParamRealtimeApiTabDto paramRealtimeApiTabDto) {
        SXSSFRow row = componentDesc.createRow(++rowIndex);
        buildRowCell(row, null, 0, paramRealtimeApiTabDto.getFieldName());
        buildRowCell(row, null, 1, "有效起始日期");
        buildRowCell(row, null, 2, NAME_DESC_BEGIN_VALID_DATE);
        buildRowCell(row, centerCellStyle, 4, KEY_Y);
        buildRowCell(row, wrapTextCellStyle, 5,"{\"required\":true,\"message\":\"有效起始日期必填\"}\n{\"validator\":\"isDate\",\"message\":\"日期格式不正确\"}");
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

    private void buildRequestDesc(SXSSFWorkbook workbook, ParamRealtimeDto paramRealtimeDto) throws IOException {
        List<ParamRealtimeApiTabDto> paramRealtimeApiTabList = paramRealtimeDto.getParamRealtimeApiTabList();
        List<ParamRealtimeApiComponentDto> paramRealtimeApiComponentDtoList = paramRealtimeDto.getParamRealtimeApiComponentDtoList();
        if (CollectionUtils.isNotEmpty(paramRealtimeApiTabList) && CollectionUtils.isNotEmpty(paramRealtimeApiComponentDtoList)) {
            ParamRealtimeApiTabDto paramRealtimeApiTab = paramRealtimeApiTabList.get(0);
            String sheetName = paramRealtimeApiTab.getTabName();
            boolean product = StringUtils.equals("fundProductInfoSet", paramRealtimeApiTab.getMenuCode());
            if (product) {
                sheetName = "基金信息";
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
                    new ParamRealtimeRequestDescDto("function", "接口名称", paramRealtimeApiTab.getMenuCode(), KEY_Y, "C", STR_BLANK, STR_BLANK)
            );
            paramRealtimeRequestDescList.add(
                    new ParamRealtimeRequestDescDto("action", "操作类型", "add:新增; edit:修改", KEY_Y, "C", STR_BLANK, STR_BLANK)
            );
            paramRealtimeRequestDescList.add(
                    new ParamRealtimeRequestDescDto("username", "用户名", "接口用户", KEY_Y, "C", STR_BLANK, STR_BLANK)
            );
            paramRealtimeRequestDescList.add(
                    new ParamRealtimeRequestDescDto("sign", "签名", STR_BLANK, KEY_Y, "C", STR_BLANK, STR_BLANK)
            );
            paramRealtimeRequestDescList.add(
                    new ParamRealtimeRequestDescDto("effectiveDate", "生效日期", STR_BLANK, KEY_N, "C", STR_BLANK, STR_BLANK)
            );
            paramRealtimeRequestDescList.add(
                    new ParamRealtimeRequestDescDto("isOverWrite", "是否覆盖", "1:是; 0:否", KEY_Y, "C", STR_BLANK, STR_BLANK)
            );
            paramRealtimeRequestDescList.add(
                    new ParamRealtimeRequestDescDto("data", "请求数据", "data中为参数的相关信息, 其为json格式, 主要数据由各个tab页中的数据组成", KEY_Y, "JSON", STR_BLANK, STR_BLANK)
            );
            CellStyle centerCellStyle = ExcelCommonUtils.getCenterCellStyle(workbook);
            CellStyle wrapTextCellStyle = ExcelCommonUtils.getWrapTextCellStyle(workbook);
            for (int i=0; i<paramRealtimeRequestDescList.size(); i++) {
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
            requestDesc.addMergedRegion(new CellRangeAddress(currentLine, currentLine,0,6));

            for (ParamRealtimeApiTabDto item : paramRealtimeApiTabList) {
                String desc = "主键字段: ";
                if (product) {
                    desc = item.getTabName() + desc;
                }
                currentLine++;
                SXSSFRow rowUniqueDesc = requestDesc.createRow(currentLine);
                buildRowCell(rowUniqueDesc, null, 0, desc + item.getCheckName());
                requestDesc.addMergedRegion(new CellRangeAddress(currentLine, currentLine,0,6));
                if (!product) {
                    break;
                }
            }

            boolean needAdd = StringUtils.isNotBlank(paramRealtimeApiTab.getFieldName()) && !beginValidDateSpecial.contains(paramRealtimeApiTab.getMenuCode());

            currentLine = currentLine + 3;
            SXSSFRow rowContent = requestDesc.createRow(currentLine);
            buildRowCell(rowContent, titleCellStyle, 0, "参考报文");
            requestDesc.addMergedRegion(new CellRangeAddress(currentLine, currentLine,0,6));
            currentLine++;
            List<String> requestContent = new ArrayList<>();
            requestContent.add("{");
            requestContent.add("    \"function\": \"" + paramRealtimeApiTab.getMenuCode() + "\",");
            requestContent.add("    \"action\": \"add\",");
            requestContent.add("    \"username\": \"admin\",");
            requestContent.add("    \"isOverWrite\": \"1\",");
            requestContent.add("    \"effectiveDate\": \"20251010\",");
            requestContent.add("    \"sign\": \"A20F83DADF6263CE5D8596F1F8C5DF37\",");
            requestContent.add("    \"data\": {");
            for (int j=0; j<paramRealtimeApiTabList.size(); j++) {
                ParamRealtimeApiTabDto tab = paramRealtimeApiTabList.get(j);
                requestContent.add("        \"" + tab.getTabCode() + "\": [");
                requestContent.add("            {");
                for (int i = 0; i< paramRealtimeApiComponentDtoList.size(); i++) {
                    ParamRealtimeApiComponentDto item = paramRealtimeApiComponentDtoList.get(i);
                    if (!StringUtils.equals(tab.getTabCode(), item.getTabCode())) {
                        continue;
                    }
                    if (StringUtils.equals(beginValidDate, item.getFieldCode()) && needAdd) {
                        continue;
                    }
                    String line = "\"" + item.getFieldCode() + "\"" + ": " + (StringUtils.isNotBlank(item.getDefaultValue()) ? "\"" + item.getDefaultValue() + "\"" : "\"\"");
                    if (i != paramRealtimeApiComponentDtoList.size() - 1) {
                        line += ",";
                    }
                    requestContent.add("                " + line);
                }
                if (needAdd) {
                    int lastIndex = requestContent.size() - 1;
                    String lastLine = requestContent.get(lastIndex);
                    if (!lastLine.endsWith(STR_COMMA)) {
                        lastLine += STR_COMMA;
                    }
                    lastLine += STR_NEXT_LINE + "                \"" + beginValidDate + "\": " + "\"20251010\"";
                    lastLine += "    // " + NAME_DESC_BEGIN_VALID_DATE;
                    requestContent.set(lastIndex, lastLine);
                }
                requestContent.add("            }");
                if (j != paramRealtimeApiTabList.size() - 1) {
                    requestContent.add("        ],");
                } else {
                    requestContent.add("        ]");
                }
                if (!product) {
                    break;
                }
            }
            requestContent.add("    }");
            requestContent.add("}");
            paramRealtimeDto.setRequestContent(requestContent.stream().collect(Collectors.joining(STR_NEXT_LINE)));
            SXSSFRow row = requestDesc.createRow(currentLine);
            buildRowCell(row, wrapTextCellStyle, 0, paramRealtimeDto.getRequestContent());
            requestDesc.addMergedRegion(new CellRangeAddress(currentLine, currentLine + requestContent.size() - 1, 0, 6));
        }
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
        CellStyle centerCellStyle = ExcelCommonUtils.getCenterCellStyle(workbook);
        CellStyle wrapTextCellStyle = ExcelCommonUtils.getWrapTextCellStyle(workbook);
        for (int i=0; i<paramRealtimeInterfaceDescList.size(); i++) {
            SXSSFRow row = interfaceDesc.createRow(i + 1);
            ParamRealtimeInterfaceDescDto paramRealtimeInterfaceDesc = paramRealtimeInterfaceDescList.get(i);
            buildRowCell(row, centerCellStyle, 0, paramRealtimeInterfaceDesc.getAgreeName());
            buildRowCell(row, centerCellStyle, 1, paramRealtimeInterfaceDesc.getAgreeIndex());
            buildRowCell(row, wrapTextCellStyle, 2, paramRealtimeInterfaceDesc.getAgreeContent());
        }

        interfaceDesc.addMergedRegion(new CellRangeAddress(1,4,0,0));
    }

    private void buildRowCell(SXSSFRow row, CellStyle cellStyle, int rowIndex, String rowName) {
        SXSSFCell cell = row.createCell(rowIndex);
        if (cellStyle != null) {
            cell.setCellStyle(cellStyle);
        }
        cell.setCellValue(rowName);
    }
}
