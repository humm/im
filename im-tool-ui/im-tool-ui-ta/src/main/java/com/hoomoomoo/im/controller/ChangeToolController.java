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
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private TextField tablePath;

    @FXML
    private Button executeRealtimeBtn;

    @FXML
    private Button errorTipsResult;

    @FXML
    private Label errorTips;

    @FXML
    private ComboBox dbNum;

    private static String TA_CODE = "00";
    private static String KEY_DATA = "\"data\": {";

    private static int paramRealtimeSetNum = 0;
    private static int tableColumnsNum = 0;

    private static Map<String, Map<String, String>> configDictValue = new LinkedHashMap<>();
    private static Map<String, String> configDictName = new LinkedHashMap<>();
    private static Set<String> beginValidDateSpecial = new HashSet<>();
    private static Map<String, Map<String, String>> tableColumnsConfig = new HashMap<>();
    private static List<List<String>> errorTableColumnInfo = new ArrayList<>();
    private static List<List<String>> errorConfigColumnInfo = new ArrayList<>();
    private static List<String> modifyInfo = new ArrayList<>();

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
        OutputUtils.info(tablePath, appConfigDto.getChangeToolTablePath());
        errorTips.setVisible(false);
        errorTipsResult.setVisible(false);
    }

    private void initAutoMode() {
        autoModeValue.put("参数提示", new String[]{STR_0});

        autoModeValue.put("基金行业", new String[]{STR_1, TA_CODE, STR_1, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0});
        autoModeValue.put("嘉实基金", new String[]{STR_1, "07", STR_1, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0});
        autoModeValue.put("建信基金", new String[]{STR_1, "53", STR_1, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0});
        autoModeValue.put("泰康基金", new String[]{STR_1, "4C", STR_1, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0});

        autoModeValue.put("证券行业", new String[]{STR_2, TA_CODE, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0, STR_1, STR_1});
        autoModeValue.put("国泰海通", new String[]{STR_2, "JA", STR_0, STR_0, STR_0, STR_0, STR_0, STR_1, STR_1, STR_0});
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
            LoggerUtils.error(e);
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
            LoggerUtils.error(e);
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
    void showParamRealtimeSetResult(ActionEvent event) throws IOException {
        Runtime.getRuntime().exec("explorer /e,/select," + new File(FileUtils.getFilePath(FILE_CHANGE_PARAM_REALTIME_SET)).getAbsolutePath());
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
                LoggerUtils.error(e);
                OutputUtils.info(logs, e.getMessage());
            } catch (Exception e) {
                LoggerUtils.error(e);
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

        res.add("-- 国泰海通特殊处理功能(国泰海通特有功能)");
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

        res.add("-- 开通TA5系统特性(广发证券特有功能)");
        res.add("update tbparam set param_value = '" + gfzq + "' where param_id = 'fund_Ta5Features';\n");

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
        Platform.runLater(() -> {
            try {
                CommonUtils.checkVersion(ConfigCache.getAppConfigDtoCache(), true);
            } catch (Exception e) {
                if (!(e instanceof SocketTimeoutException)) {
                    LoggerUtils.error(e);
                }
            }
        });
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
                OutputUtils.info(logs, "请设置开通脚本目录位置");
                return;
            }
            if (!new File(paramPath).isDirectory()) {
                OutputUtils.info(logs, "开通脚本目录必须为文件夹");
                return;
            }
            String path = tablePath.getText();
            if (StringUtils.isBlank(path)) {
                OutputUtils.info(logs, "请表结构目录");
                return;
            }
            if (!new File(path).isDirectory()) {
                OutputUtils.info(logs, "表结构目录必须为文件夹");
                return;
            }
            TaskUtils.execute(new ChangeFunctionTask(new ChangeFunctionTaskParam(this, STR_3, dictPath, paramPath, path)));
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
            errorTableColumnInfo.clear();
            errorConfigColumnInfo.clear();
            modifyInfo.clear();
            executeRealtimeBtn.setDisable(true);
            OutputUtils.infoContainBr(logs, "初始化字典信息 开始");
            initConfigInfo();
            initBaseDict(dictPath);
            initExtDict(paramPath);
            OutputUtils.infoContainBr(logs, "初始化字典信息 结束");
            OutputUtils.infoContainBr(logs, "初始化表结构信息 开始");
            initTableInfo(tablePath);
            OutputUtils.infoContainBr(logs, "初始化表结构信息 结束");
            OutputUtils.infoContainBr(logs, "生成文件 开始");
            buildFile(paramPath);
            if (paramRealtimeSetNum == 0) {
                OutputUtils.infoContainBr(logs, "未扫描到参数电子化配置脚本，请检查【开通脚本目录】配置是否正确");
                errorTips.setVisible(true);
            }
            if (tableColumnsNum == 0) {
                OutputUtils.infoContainBr(logs, "未扫描到表结构，请检查【表结构目录】配置是否正确");
                errorTips.setVisible(true);
            }
            OutputUtils.infoContainBr(logs, "生成文件 结束");
            if (CollectionUtils.isNotEmpty(modifyInfo)) {
                OutputUtils.infoContainBr(logs, "修改明细");
                for (String ele : modifyInfo) {
                    OutputUtils.infoContainBr(logs, ele);
                }
            }
            // 忽略提示信息
            skipErrorTips(errorTableColumnInfo);
            skipErrorTips(errorConfigColumnInfo);

            StringBuilder errorMessage = new StringBuilder();
            if (CollectionUtils.isNotEmpty(errorTableColumnInfo)) {
                String msg;
                for (List<String> ele : errorTableColumnInfo) {
                    if (StringUtils.isNotBlank(ele.get(3))) {
                        msg = String.format("%s  %s  %s  %s  %s", ele.get(0), ele.get(1), "已配置字段未获取到表结构字段信息", ele.get(2), ele.get(3));
                    } else {
                        msg = String.format("%s  %s  %s  %s", ele.get(0), ele.get(1), "已配置字段未获取到表结构信息", ele.get(2));
                    }
                    errorMessage.append(msg + STR_NEXT_LINE);
                }
            }
            if (CollectionUtils.isNotEmpty(errorConfigColumnInfo)) {
                for (List<String> ele : errorConfigColumnInfo) {
                    String msg = String.format("%s  %s  %s  %s  %s", ele.get(0), ele.get(1), "未配置字段已获取到表结构字段信息", ele.get(2), ele.get(3));
                    errorMessage.append(msg + STR_NEXT_LINE);
                }
            }
            if (StringUtils.isNotBlank(errorMessage)) {
                OutputUtils.infoContainBr(logs, "错误明细信息");
                OutputUtils.infoContainBr(logs, errorMessage.toString());
                FileUtils.writeFile(FileUtils.getFilePath(FILE_CHANGE_PARAM_REALTIME_SET), Arrays.asList(errorMessage.toString()));
                errorTips.setVisible(true);
                errorTipsResult.setVisible(true);
            } else {
                FileUtils.writeFile(FileUtils.getFilePath(FILE_CHANGE_PARAM_REALTIME_SET), Arrays.asList("完美无瑕"));
            }
        } catch (Exception e) {
            LoggerUtils.error(e);
            OutputUtils.infoContainBr(logs, e.getMessage());
        } finally {
            executeRealtimeBtn.setDisable(false);
        }
    }

    private void skipErrorTips(List<List<String>> error) throws IOException {
        Iterator<List<String>> iterator = error.listIterator();
        Map<String, List<String>> skipConfig = JSON.parseObject(FileUtils.readNormalFileToString(FileUtils.getFilePath(PATH_PARAM_REALTIME_SET_SKIP_JSON)), Map.class);
        while (iterator.hasNext()) {
            List<String> skipInfo = iterator.next();
            String table = skipInfo.get(2);
            String column = skipInfo.get(3);
            if (StringUtils.isBlank(column)) {
                continue;
            }
            if (skipConfig.containsKey(table) || skipConfig.containsKey(KEY_GLOBAL_TABLE)) {
                List<String> columns = new ArrayList<>();
                List<String> tableColumns = skipConfig.get(table);
                if (CollectionUtils.isNotEmpty(tableColumns)) {
                    columns.addAll(tableColumns);
                }
                columns.addAll(skipConfig.get(KEY_GLOBAL_TABLE));
                for (String col : columns) {
                    if (StringUtils.equals(column.trim(), col.trim())) {
                        iterator.remove();
                        continue;
                    }
                }
            }
        }
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
        OutputUtils.infoContainBr(logs, "扫描文件 " + tablePath);
        String tableContent = FileUtils.readNormalFileToString(tablePath);
        if (StringUtils.isBlank(tableContent)) {
            return;
        }
        String[] table = tableContent.split("create table");
        for (int i=1; i<table.length; i++) {
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
                for (int j=0; j<eleConfig.length; j++) {
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
                        fieldType = eleConfig[j+1];
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
        OutputUtils.infoContainBr(logs, "扫描文件 " + path);
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
            paramRealtimeSetNum++;
            if (!path.endsWith(".sql")) {
                return;
            }
            OutputUtils.infoContainBr(logs, "扫描文件 " +  path);
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
        buildComponentDesc(workbook, paramRealtimeDto, new File(filePath).getName());
        buildDictDesc(workbook, paramRealtimeDto);
        buildFile(workbook, paramRealtimeDto, filePath);
    }

    private void buildFile(SXSSFWorkbook workbook, ParamRealtimeDto paramRealtimeDto, String filePath) throws IOException {
        String excelFileBakPath = filePath.replace(".sql", KEY_BACKUP + ".xlsx");
        String jsonFilePath = filePath.replace(".sql", ".json");
        String jsonSceneFilePath = filePath.replace(".sql", "Scene.json");
        List<String> requestContent = paramRealtimeDto.getRequestContent();
        FileUtils.writeFile(jsonFilePath, requestContent.stream().collect(Collectors.joining(STR_NEXT_LINE)));
        FileUtils.writeFile(jsonSceneFilePath, buildSceneContent(requestContent).stream().collect(Collectors.joining(STR_NEXT_LINE)));
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
            Files.move(Paths.get(excelFileBakPath),  Paths.get(excelFilePath), StandardCopyOption.ATOMIC_MOVE);
        }
    }

    private List<String> buildSceneContent(List<String> requestContent) {
        List<String> sceneContent = new ArrayList<>();
        sceneContent.add(requestContent.get(0));
        int dataIndex = 0;
        for (int i=0; i<requestContent.size(); i++) {
            if (requestContent.get(i).contains(KEY_DATA)) {
                dataIndex = i;
                break;
            }
        }
        sceneContent.add("    \"systemCode\": \"TA\",");
        sceneContent.add("    \"mac\": \"xxxxxxxxxx\",");
        sceneContent.add("    \"username\": \"admin\",");
        sceneContent.add("    \"sign\": \"xxxxxxxxxx\",");
        sceneContent.add("    \"user\": \"productcenter\",");
        sceneContent.add("    \"operatorId\": \"admin\",");
        sceneContent.add("    \"checkerId\": \"system\",");
        sceneContent.add("    \"importDate\": \"\",");
        sceneContent.add("    \"finishDate\": \"\",");
        sceneContent.add("    \"operType\": \"\",");
        sceneContent.add("    \"sceneCode\": \"场景编码\",");
        sceneContent.add("    \"projectCode\": \"xxx\",");
        sceneContent.add("    \"projectName\": \"xxx\",");
        sceneContent.add("    \"memo\": \"备注\",");
        sceneContent.addAll(requestContent.subList(dataIndex, requestContent.size()));
        return sceneContent;
    }

    private void buildComponentDesc(SXSSFWorkbook workbook, ParamRealtimeDto paramRealtimeDto, String fileName) throws Exception {
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
                        errorConfigColumnInfo.add(Arrays.asList(fileName, paramRealtimeApiTabDto.getTabName(), tableCode, key));
                    }
                }
            }
            int rowIndex = 0;
            boolean errorTableColumnInfoExists;
            for (ParamRealtimeApiComponentDto item : paramRealtimeApiComponentDtoList) {
                errorTableColumnInfoExists = false;
                String fieldCode = changeToLower(item.getFieldCode());
                boolean needAdd = StringUtils.isNotBlank(paramRealtimeApiTabDto.getFieldName()) && !beginValidDateSpecial.contains(paramRealtimeApiTabDto.getMenuCode());
                if (StringUtils.equals(paramRealtimeApiTabDto.getFieldName(), fieldCode) && needAdd) {
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
                            if (StringUtils.equals(ele.get(0), fileName) && StringUtils.equals(ele.get(1), paramRealtimeApiTabDto.getTabName())
                                    && StringUtils.equals(ele.get(2), tableCode) && StringUtils.equals(ele.get(3), fieldCode)) {
                                errorTableColumnInfoExists = true;
                                break;
                            }
                        }
                        if (!errorTableColumnInfoExists) {
                            errorTableColumnInfo.add(Arrays.asList(fileName, paramRealtimeApiTabDto.getTabName(), tableCode, fieldCode));
                        }
                    }
                } else {
                    if (StringUtils.isNotBlank(tabCode)) {
                        for (List<String> ele : errorTableColumnInfo) {
                            if (StringUtils.equals(ele.get(0), fileName) && StringUtils.equals(ele.get(1), paramRealtimeApiTabDto.getTabName())
                                    && StringUtils.equals(ele.get(2), tableCode)) {
                                errorTableColumnInfoExists = true;
                                break;
                            }
                        }
                        if (!errorTableColumnInfoExists) {
                            errorTableColumnInfo.add(Arrays.asList(fileName, paramRealtimeApiTabDto.getTabName(), tableCode, STR_BLANK));
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
        buildRowCell(row, centerCellStyle, 6, KEY_N);
        buildRowCell(row, wrapTextCellStyle, 7,"{\"required\":true,\"message\":\"有效起始日期必填\"}\n{\"validator\":\"isDate\",\"message\":\"日期格式不正确\"}");
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
                    new ParamRealtimeRequestDescDto("systemCode", "系统标识", "启用鉴权功能必填", KEY_N, COLUMN_TYPE_C, STR_BLANK, STR_BLANK)
            );
            paramRealtimeRequestDescList.add(
                    new ParamRealtimeRequestDescDto("mac", "mac地址", "鉴权功能使用", KEY_N, COLUMN_TYPE_C, STR_BLANK, STR_BLANK)
            );
            paramRealtimeRequestDescList.add(
                    new ParamRealtimeRequestDescDto("username", "用户代码", "用户信息", KEY_Y, COLUMN_TYPE_C, STR_BLANK, STR_BLANK)
            );
            paramRealtimeRequestDescList.add(
                    new ParamRealtimeRequestDescDto("sign", "签名", "启用鉴权功能必填", KEY_N, COLUMN_TYPE_C, STR_BLANK, STR_BLANK)
            );
            paramRealtimeRequestDescList.add(
                    new ParamRealtimeRequestDescDto("function", "接口代码", "推送接口标识", KEY_Y, COLUMN_TYPE_C, STR_BLANK, STR_BLANK)
            );
            paramRealtimeRequestDescList.add(
                    new ParamRealtimeRequestDescDto("action", "操作类型", "add:新增; edit:修改; delete:删除", KEY_Y, COLUMN_TYPE_C, STR_BLANK, STR_BLANK)
            );
            paramRealtimeRequestDescList.add(
                    new ParamRealtimeRequestDescDto("isOverWrite", "是否覆盖", "1:是; 0:否", KEY_Y, COLUMN_TYPE_C, STR_BLANK, STR_BLANK)
            );
            paramRealtimeRequestDescList.add(
                    new ParamRealtimeRequestDescDto("data", "请求数据", "data中为参数的相关信息, 其为json格式, 主要数据由各个tab页中的数据组成", KEY_Y, COLUMN_TYPE_JSON, STR_BLANK, STR_BLANK)
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
                String keyStr = item.getCheckName();
                if (StringUtils.isNotBlank(keyStr)) {
                    paramRealtimeDto.getPrimaryKey().put(item.getTabCode(), Arrays.asList(keyStr.split(STR_COMMA)));
                }
                String desc = "主键字段: ";
                if (product) {
                    desc = item.getTabName() + desc;
                }
                currentLine++;
                SXSSFRow rowUniqueDesc = requestDesc.createRow(currentLine);
                buildRowCell(rowUniqueDesc, null, 0, desc + keyStr);
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
            requestContent.add("    \"systemCode\": \"TA\",");
            requestContent.add("    \"mac\": \"xxxxxxxxxx\",");
            requestContent.add("    \"username\": \"admin\",");
            requestContent.add("    \"sign\": \"xxxxxxxxxx\",");
            requestContent.add("    \"function\": \"" + paramRealtimeApiTab.getMenuCode() + "\",");
            requestContent.add("    \"action\": \"add\",");
            requestContent.add("    \"isOverWrite\": \"1\",");
            requestContent.add("    " + KEY_DATA);
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
            paramRealtimeDto.setRequestContent(requestContent);
            SXSSFRow row = requestDesc.createRow(currentLine);
            buildRowCell(row, wrapTextCellStyle, 0, requestContent.stream().collect(Collectors.joining(STR_NEXT_LINE)));
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
