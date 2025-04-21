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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.CHANGE_FUNCTION_TOOL;

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
    private Button autoModeBtn;

    @FXML
    private Button menuCodeBtn;

    @FXML
    private ComboBox autoMode;

    @FXML
    private ComboBox menuMode;

    private static String executeType;
    private static final String executeMenu = "1";
    private static final String executeMode = "2";

    private static String TA_CODE = "";

    private static final String MENU_MODE_NEW = "新版";
    private static final String MENU_MODE_OLD = "老版";
    private static final String MENU_MODE_OLD_ALL = "老版(全部)";

    private static final String AUTO_MODE_JJHY = "***基金行业***";
    private static final String AUTO_MODE_JSJJ = "嘉实基金";
    private static final String AUTO_MODE_ZQHY = "***证券行业***";
    private static final String AUTO_MODE_GTHT = "国泰海通";
    private static final String AUTO_MODE_DFZQ = "东方证券";
    private static final String AUTO_MODE_SWHY = "申万宏源";
    private static final String AUTO_MODE_ZX = "中信证券";
    private static final String AUTO_MODE_XY = "兴业证券";
    private static final String AUTO_MODE_ZJ = "中金公司";
    private static final String AUTO_MODE_GJDF = "国金道富";

    private Set<String> autoModeSet = new LinkedHashSet<String>(){{
        add(AUTO_MODE_JJHY);
        add(AUTO_MODE_JSJJ);

        add(AUTO_MODE_ZQHY);
        add(AUTO_MODE_GTHT);
        add(AUTO_MODE_DFZQ);
        add(AUTO_MODE_SWHY);

        add(AUTO_MODE_ZX);
        add(AUTO_MODE_XY);
        add(AUTO_MODE_ZJ);
        add(AUTO_MODE_GJDF);
    }};

    private Set<String> menuModeSet = new LinkedHashSet<String>(){{
        add(MENU_MODE_NEW);
        add(MENU_MODE_OLD);
        add(MENU_MODE_OLD_ALL);
    }};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String msg = String.format(BaseConst.MSG_USE, CHANGE_FUNCTION_TOOL.getName());
        LoggerUtils.info(msg);
        LoggerUtils.writeLogInfo(CHANGE_FUNCTION_TOOL.getCode(), new Date(), new ArrayList<String>(){{
            add(msg);
        }});
        ObservableList auto = autoMode.getItems();
        if (CollectionUtils.isNotEmpty(autoModeSet)) {
            Iterator<String> ver = autoModeSet.iterator();
            while (ver.hasNext()) {
                auto.add(ver.next());
            }
        }
        autoMode.getSelectionModel().select(0);
        ObservableList menu = menuMode.getItems();
        if (CollectionUtils.isNotEmpty(menuModeSet)) {
            Iterator<String> ver = menuModeSet.iterator();
            while (ver.hasNext()) {
                menu.add(ver.next());
            }
        }
        menuMode.getSelectionModel().select(0);

        StringBuilder tips = new StringBuilder();
        tips.append(buildTipsMessage(AUTO_MODE_JJHY + "(实时并发清算)", "fund_MultiProcessesLiqDeal"));
        tips.append(buildTipsMessage(AUTO_MODE_ZQHY + "(分产品自动化清算)", "fund_AutoLiqByPrd"));
        tips.append(buildTipsMessage(AUTO_MODE_GTHT, "fund_JaSpecialDeal"));
        tips.append(buildTipsMessage(AUTO_MODE_ZX, "fund_ParamProcessesLiqDeal"));
        tips.append(buildTipsMessage(AUTO_MODE_XY, "fund_XyMultiProcessesPrivate"));
        tips.append(buildTipsMessage(AUTO_MODE_ZJ, "fund_ZjMultiProcessesPrivate"));
        OutputUtils.info(logs, tips.toString());
    }

    private String buildTipsMessage(String mode, String val) {
        return "【" + mode + "】" + " 个性化参数 " + "【" + val + "】" + STR_NEXT_LINE;
    }

    @FXML
    void executeAutoMode(ActionEvent event) {
        try {
            OutputUtils.clearLog(logs);
            String mode = CommonUtils.getComponentValue(autoMode);
            if (StringUtils.isBlank(mode)) {
                OutputUtils.info(logs,"请选择 自动化清算模式");
                return;
            }
            executeType = executeMode;
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
            String menu = CommonUtils.getComponentValue(menuMode);
            if (StringUtils.isBlank(menu)) {
                OutputUtils.info(logs,"请选择 菜单模式");
                return;
            }
            executeType = executeMenu;
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
        OutputUtils.infoContainBr(logs, taskMsg);
        OutputUtils.infoContainBr(logs, "生成脚本 开始...");
    }

    private void executeEnd(String resFilePath) throws Exception {
        OutputUtils.infoContainBr(logs, "\n生成脚本 完成...");
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        if (StringUtils.isNotBlank(appConfigDto.getDatabaseScriptUrl())) {
            OutputUtils.infoContainBr(logs, "执行脚本 开始...");
            String sql = STR_BLANK;
            try {
                String[] sqlList = FileUtils.readNormalFileToString(resFilePath, true).split(STR_SEMICOLON);
                if (sqlList != null) {
                    int size = sqlList.length;
                    if (executeType.equals(executeMenu)) {
                        OutputUtils.info(logs, "执行中...");
                    }
                    for (int i=0; i<size; i++) {
                        sql = sqlList[i].trim();
                        if (executeType.equals(executeMode)) {
                            OutputUtils.info(logs, (i == 0 ? STR_BLANK : STR_NEXT_LINE) + "执行sql: " + sql);
                        } else {
                            if (i % 1000 == 0) {
                                OutputUtils.info(logs, STR_POINT_3);
                            }
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
                OutputUtils.info(logs, "\n执行异常sql: " + sql);
                return;
            } finally {
                DatabaseUtils.closeConnection();
                enableBtn();
            }
            OutputUtils.infoContainBr(logs, "\n执行脚本 完成...");
            OutputUtils.infoContainBr(logs, "请刷新系统缓存...");
        } else {
            enableBtn();
        }
    }

    public void buildMenuModeSql(String taskType) throws Exception {
        switch (taskType) {
            case MENU_MODE_NEW:
                buildMenuSql(taskType,true, false);
                break;
            case MENU_MODE_OLD:
                buildMenuSql(taskType,false, false);
                break;
            case MENU_MODE_OLD_ALL:
                buildMenuSql(taskType,false, true);
                break;
            default:
                new Exception("未匹配执行方法，请检查");
        }

    }

    public void buildMenuSql( String taskType, boolean newUd, boolean all) throws Exception {
        executeStart("菜单模式 ... " + taskType);
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String base = ScriptSqlUtils.baseMenu;
        String paramValue = STR_0;
        if (newUd) {
            base = ScriptSqlUtils.newUedPage;
            paramValue = STR_1;
        }
        String basePath = appConfigDto.getSystemToolCheckMenuBasePath() + base;
        List<String> res = new ArrayList<>();
        res.add("-- " + taskType);
        res.add("-- 更新系统参数");
        res.add("update tbparam set param_value = '" + paramValue + "' where param_id = 'IsNewMenuIndex';\n");
        List<String> sqlList = FileUtils.readNormalFile(basePath, false);
        String resFilePath = FileUtils.getFilePath(FILE_CHANGE_MENU);
        FileUtils.deleteFile(new File(resFilePath));
        FileUtils.writeFile(resFilePath, res, false);
        if (CollectionUtils.isNotEmpty(sqlList)) {
            int size = sqlList.size();
            OutputUtils.info(logs, "执行中...");
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
                    FileUtils.writeFile(resFilePath, res, true);
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
            FileUtils.writeFile(resFilePath, res, true);
        }
        executeEnd(resFilePath);
    }

    public void buildAutoModeSql(String taskType) throws Exception {
        TA_CODE = STR_SPACE;
        switch (taskType) {
            case AUTO_MODE_JJHY:
                buildAutoModeSql(taskType, STR_1, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0);
                break;
            case AUTO_MODE_JSJJ:
                TA_CODE = "07";
                buildAutoModeSql(taskType, STR_1, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0);
                break;
            case AUTO_MODE_ZQHY:
                buildAutoModeSql(taskType, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0, STR_1);
                break;
            case AUTO_MODE_GTHT:
                TA_CODE = "JA";
                buildAutoModeSql(taskType, STR_0, STR_0, STR_0, STR_0, STR_0, STR_1, STR_1);
                break;
            case AUTO_MODE_DFZQ:
                TA_CODE = "SD";
                buildAutoModeSql(taskType, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0, STR_1);
                break;
            case AUTO_MODE_SWHY:
                buildAutoModeSql(taskType, STR_0, STR_0, STR_0, STR_0, STR_0, STR_0, STR_1);
                break;
            case AUTO_MODE_ZX:
                TA_CODE = "S5";
                buildAutoModeSql(taskType, STR_0, STR_1, STR_0, STR_0, STR_0, STR_0, STR_0);
                break;
            case AUTO_MODE_XY:
                TA_CODE = "XY";
                buildAutoModeSql(taskType, STR_0, STR_0, STR_1, STR_0, STR_0, STR_0, STR_0);
                break;
            case AUTO_MODE_ZJ:
                TA_CODE = "SM";
                buildAutoModeSql(taskType, STR_0, STR_0, STR_0, STR_1, STR_0, STR_0, STR_0);
                break;
            case AUTO_MODE_GJDF:
                TA_CODE = "NB";
                buildAutoModeSql(taskType, STR_0, STR_0, STR_0, STR_0, STR_1, STR_0, STR_0);
                break;
            default:
                new Exception("未匹配执行方法，请检查");
        }
    }

    /**
     *
     * @param ssbf 公募
     * @param zx 中信
     * @param xy 兴业
     * @param zj 中金
     * @param gjdf 国金道富
     * @param gtja 国泰君安
     * @param fcp 分产品自动化
     */
    public void buildAutoModeSql(String taskType, String ssbf, String zx, String xy, String zj, String gjdf, String gtja, String fcp) throws Exception {
        boolean xyMode =  STR_1.equals(xy) || STR_1.equals(zj) || STR_1.equals(gjdf);
        executeStart(taskType);
        OutputUtils.info(logs, "执行中...");
        List<String> res = new ArrayList<>();
        res.add("-- " + taskType + "\n");

        res.add("-- 开通实时并发清算功能(基金行业)");
        res.add("update tbparam set param_value = '" + ssbf + "' where param_id = 'fund_MultiProcessesLiqDeal';\n");

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

        res.add("-- 是否兴业自动化清算特有功能(兴业特有功能)");
        res.add("update tbparam set param_value = '" + xy + "' where param_id = 'fund_XyMultiProcessesPrivate';\n");

        res.add("-- 清算列表外部发起(兴业自动化清算模式)");
        if (xyMode) {
            res.add("update tbparam set param_value = '1' where param_id = 'fund_XyMultiProcessesLiqDeal';\n");
        } else {
            res.add("update tbparam set param_value = '0' where param_id = 'fund_XyMultiProcessesLiqDeal';\n");
        }

        res.add("-- 开通分产品自动化清算功能(证券行业)");
        res.add("update tbparam set param_value = '" + fcp + "' where param_id = 'fund_AutoLiqByPrd';\n");

        res.add("-- 分产品自动化清算行情导入方式");
        if (STR_1.equals(gtja) || STR_0.equals(fcp)) {
            res.add("update tbparam set param_value = '0' where param_id = 'fund_autoLiqImpNavType';\n");
        } else {
            res.add("update tbparam set param_value = '1' where param_id = 'fund_autoLiqImpNavType';\n");
        }

        res.add("-- 国泰君安特殊处理功能(国君特有功能)");
        if (STR_1.equals(gtja)) {
            res.add("update tbparam set param_value = '1' where param_id = 'fund_JaSpecialDeal';\n");
        } else {
            res.add("update tbparam set param_value = '0' where param_id = 'fund_JaSpecialDeal';\n");
        }

        res.add("-- 开通中金模式自动化清算功能(中金特有功能)");
        res.add("update tbparam set param_value = '" + zj + "' where param_id = 'fund_ZjMultiProcessesPrivate';\n");

        res.add("-- 更新TA代码");
        res.add("update tbparam set param_value = '" + TA_CODE + "' where param_id = 'BTACODE';\n");

        res.add(STR_SPACE);
        String groupCode = STR_BLANK;
        String groupName = "1主流程";
        if (STR_1.equals(ssbf)) {
            groupCode = "fund_daily_virtual_multi";
        } else if (STR_1.equals(zx)) {
            groupCode = "fund_daily_t1_multi";
        } else if (xyMode) {
            groupCode = "fund_daily_xyt1_multi";
            groupName = "1自动化清算";
        } else if (STR_1.equals(fcp)) {
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
            FileUtils.writeFile(resFilePath, res, true);
        }
        executeEnd(resFilePath);
    }
}
