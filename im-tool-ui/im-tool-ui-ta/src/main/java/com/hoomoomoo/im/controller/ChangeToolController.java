package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

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
    private ComboBox dbNum;

    private static String TA_CODE = "00";

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
    }

    /**
     * hy 行业 0:参数提示 1:基金行业 2:证券行业 3:个性化行业
     * taCode ta代码
     * gm 公募
     * zx 中信
     * xy 兴业
     * zj 中金
     * gj 国金道富
     * gt 国泰海通
     * sm 分产品自动化
     * navType 分产品自动化清算行情导入方式
     */
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
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            if (StringUtils.isNotBlank(appConfigDto.getFinalVer())) {
                CommonUtils.showTipsByError(appConfigDto.getFinalVer(), 30 * 1000);
                return;
            }
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
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            if (StringUtils.isNotBlank(appConfigDto.getFinalVer())) {
                CommonUtils.showTipsByError(appConfigDto.getFinalVer(), 30 * 1000);
                return;
            }
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
    void executeDbBtn(ActionEvent event) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        if (StringUtils.isNotBlank(appConfigDto.getFinalVer())) {
            CommonUtils.showTipsByError(appConfigDto.getFinalVer(), 30 * 1000);
            return;
        }
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

    public void buildAutoModeSql(String taskType, String gm, String zx, String xy, String zj, String gj,
                                 String gt, String sm, String navType) throws Exception {
        boolean xyMode =  STR_1.equals(xy) || STR_1.equals(zj) || STR_1.equals(gj);
        executeStart(taskType);
        OutputUtils.info(logs, "执行中 ...");
        List<String> res = new ArrayList<>();
        res.add("-- " + taskType + "\n");

        res.add("-- 开通实时并发清算功能(基金行业)");
        res.add(generateSql("开通实时并发清算功能", "fund_MultiProcessesLiqDeal", gm));

        res.add("-- 按照产品日切清算(中信自动化清算模式)");
        res.add(generateSql("按照产品日切清算", "fund_T1MultiProcessesLiqDeal", STR_1.equals(zx) || xyMode ? STR_1 : STR_0));

        res.add("-- 开通参数日期管理功能(中信特有功能)");
        res.add(generateSql("开通参数日期管理功能", "fund_ParamProcessesLiqDeal", zx));

        res.add("-- 是否兴业自动化清算特有功能(兴业特有功能)");
        res.add(generateSql("是否兴业自动化清算特有功能", "fund_XyMultiProcessesPrivate", xy));

        res.add("-- 清算列表外部发起(兴业自动化清算模式)");
        res.add(generateSql("清算列表外部发起", "fund_XyMultiProcessesLiqDeal", xyMode ? STR_1 : STR_0));

        res.add("-- 开通分产品自动化清算功能(证券行业)");
        res.add(generateSql("开通分产品自动化清算功能", "fund_AutoLiqByPrd", sm));

        res.add("-- 分产品自动化清算行情导入方式");
        res.add(generateSql("分产品自动化清算行情导入方式", "fund_autoLiqImpNavType", navType));

        res.add("-- 国泰海通特殊处理功能(国泰海通特有功能)");
        res.add(generateSql("国泰海通特殊处理功能", "fund_JaSpecialDeal", gt));

        res.add("-- 开通中金模式自动化清算功能(中金特有功能)");
        res.add(generateSql("开通中金模式自动化清算功能", "fund_ZjMultiProcessesPrivate", zj));

        String gf = StringUtils.equals("87", TA_CODE) ? STR_1 : STR_0;

        res.add("-- 开通自动化清算支持固定批次处理功能");
        res.add(generateSql("开通自动化清算支持固定批次处理功能", "fund_AutoLiqSptFixedBatch", gf));

        res.add("-- 开通自动化清算隐藏基金状态节点");
        res.add(generateSql("开通自动化清算隐藏基金状态节点", "fund_AutoLiqHideStatusSet", gf));

        res.add("-- 开通自动化数据自动导入");
        res.add(generateSql("开通自动化数据自动导入", "fund_MultiProcessesDataAutoImp", gf));

        res.add("-- 开通自动化清算支持T0产品清算");
        res.add(generateSql("开通自动化清算支持T0产品清算", "fund_AutoLiqSptT0Deal", gf));

        res.add("-- 开通自动化清算支持外部API稽核");
        res.add(generateSql("开通自动化清算支持外部API稽核", "fund_AutoLiqSptApiAudit", gf));

        res.add("-- 开通自动化清算根据交易列表处理功能");
        res.add(generateSql("开通自动化清算根据交易列表处理功能", "fund_AutoLiqByTradeList", gf));

        res.add("-- 开通资金清算只导出T0确认文件");
        res.add(generateSql("开通资金清算只导出T0确认文件", "fund_ZjqsExpT0CfmFile", gf));

        res.add("-- 开通销售商预设批次导出功能");
        res.add(generateSql("开通销售商预设批次导出功能", "fund_AgencyPreExport", gf));

        res.add("-- 开通OTC登记托管功能");
        res.add(generateSql("开通OTC登记托管功能", "fund_OTCTransferAgent", gf));

        res.add("-- 开通TA5系统特性");
        res.add(generateSql("开通TA5系统特性", "fund_Ta5Features", gf));

        res.add("-- TA代码");
        res.add(generateSql("TA代码", "BTACODE", TA_CODE));

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

    private String generateSql(String paramName, String paramId, String paramValue) {
        StringBuilder sql = new StringBuilder();
        sql.append(String.format("delete from tbparam where param_id = '%s';\n", paramId));
        sql.append("insert into tbparam (ta_code, param_id, param_name, param_value, value_name, belong_type, modi_flag, reserve1)\n");
        sql.append(String.format("values ('000000', '%s', '%s', '%s', ' ', '5', '1', ' ');\n", paramId, paramName, paramValue));
        return sql.toString();
    }
}
