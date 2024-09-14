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
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;

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

    private Set<String> autoModeSet = new LinkedHashSet<String>(){{
        add("公募自动化");
        add("中信自动化");
        add("兴业自动化");
        add("中金自动化");
        add("国君自动化");
        add("国金道富自动化");
    }};

    private Set<String> menuModeSet = new LinkedHashSet<String>(){{
        add("新版");
        add("老版");
        add("老版(全部)");
    }};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LoggerUtils.info(String.format(BaseConst.MSG_USE, CHANGE_FUNCTION_TOOL.getName()));
        ObservableList auto = autoMode.getItems();
        if (CollectionUtils.isNotEmpty(autoModeSet)) {
            Iterator<String> ver = autoModeSet.iterator();
            while (ver.hasNext()) {
                auto.add(ver.next());
            }
        }

        ObservableList menu = menuMode.getItems();
        if (CollectionUtils.isNotEmpty(menuModeSet)) {
            Iterator<String> ver = menuModeSet.iterator();
            while (ver.hasNext()) {
                menu.add(ver.next());
            }
        }
    }

    @FXML
    void executeAutoMode(ActionEvent event) {
        try {
            String mode = CommonUtils.getComponentValue(autoMode);
            if (StringUtils.isBlank(mode)) {
                OutputUtils.info(logs,"请选择 自动化清算模式");
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
            String menu = CommonUtils.getComponentValue(menuMode);
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
        OutputUtils.infoContainBr(logs, "生成脚本 完成...");
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        if (StringUtils.isNotBlank(appConfigDto.getDatabaseScriptUrl())) {
            OutputUtils.infoContainBr(logs, "执行脚本 开始...");
            String[] sqlList = new String[0];
            String sql = STR_BLANK;
            try {
                sqlList = FileUtils.readNormalFileToString(resFilePath, true).split(STR_SEMICOLON);
                if (sqlList != null) {
                    for (int i=0; i<sqlList.length; i++) {
                        if (i % 1000 == 0) {
                            OutputUtils.info(logs, STR_POINT);
                        }
                        sql = sqlList[i];
                        DatabaseUtils.executeSql(sql, null);
                    }
                }
            } catch (IOException e) {
                LoggerUtils.info(e);
                OutputUtils.info(logs, e.getMessage());
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(logs, e.getMessage());
                OutputUtils.info(logs, "\n执行异常sql\n" + sql);
                return;
            } finally {
                DatabaseUtils.closeConnection();
                enableBtn();
            }
            OutputUtils.infoContainBr(logs, "执行脚本 完成...");
            OutputUtils.infoContainBr(logs, "请刷新系统缓存...");
        }
    }

    public void buildMenuModeSql(String taskType) throws Exception {
       switch (taskType) {
           case "新版":
               buildMenuSql(taskType,true, false);
               break;
           case "老版":
               buildMenuSql(taskType,false, false);
               break;
           case "老版(全部)":
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
            for (int i=0; i<sqlList.size(); i++) {
                String sql = sqlList.get(i);
                if (i % 1000 == 0) {
                    OutputUtils.info(logs, STR_POINT);
                    FileUtils.writeFile(resFilePath, res, true);
                    res.clear();
                }
                String sqlLower = sql.toLowerCase();
                if (!all && sqlLower.contains("交易码  tsys_trans")) {
                    res.add("commit;");
                    break;
                }
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
        switch (taskType) {
            case "公募自动化":
                buildAutoModeSql(taskType, STR_1, STR_0, STR_0, STR_0, STR_0, STR_0);
                break;
            case "中信自动化":
                buildAutoModeSql(taskType, STR_0, STR_1, STR_0, STR_0, STR_0, STR_0);
                break;
            case "兴业自动化":
                buildAutoModeSql(taskType, STR_0, STR_0, STR_1, STR_0, STR_0, STR_0);
                break;
            case "中金自动化":
                buildAutoModeSql(taskType, STR_0, STR_0, STR_0, STR_1, STR_0, STR_0);
                break;
            case "国金道富自动化":
                buildAutoModeSql(taskType, STR_0, STR_0, STR_0, STR_0, STR_1, STR_0);
                break;
            case "国君自动化":
                buildAutoModeSql(taskType, STR_0, STR_0, STR_0, STR_0, STR_0, STR_1);
                break;
            default:
                new Exception("未匹配执行方法，请检查");
        }
    }

    /**
     *
     * @param gm 公募
     * @param zx 中信
     * @param xy 兴业
     * @param gj 国君
     * @param zj 中金
     */
    public void buildAutoModeSql(String taskType, String gm, String zx, String xy, String zj, String gjdf, String gj) throws Exception {
        executeStart("自动化清算模式 ... " + taskType);
        List<String> res = new ArrayList<>();
        res.add("-- 更新系统参数 ... " + taskType);
        res.add("update tbparam set param_value = 'fund_MultiProcessesLiqDeal' where param_id = '" + gm + "';\n");
        if (STR_1.equals(zx) || STR_1.equals(xy) || STR_1.equals(zj) || STR_1.equals(gjdf)) {
            res.add("update tbparam set param_value = 'fund_T1MultiProcessesLiqDeal' where param_id = '1';\n");
        } else {
            res.add("update tbparam set param_value = 'fund_T1MultiProcessesLiqDeal' where param_id = '0';\n");
        }
        if (STR_1.equals(zx)) {
            res.add("update tbparam set param_value = 'fund_ParamProcessesLiqDeal' where param_id = '1';\n");
        } else {
            res.add("update tbparam set param_value = 'fund_ParamProcessesLiqDeal' where param_id = '0';\n");
        }
        res.add("update tbparam set param_value = 'fund_XyMultiProcessesPrivate' where param_id = '" + xy + "';\n");
        if (STR_1.equals(xy) || STR_1.equals(zj) || STR_1.equals(gjdf)) {
            res.add("update tbparam set param_value = 'fund_XyMultiProcessesLiqDeal' where param_id = '1';\n");
        } else {
            res.add("update tbparam set param_value = 'fund_XyMultiProcessesLiqDeal' where param_id = '0';\n");
        }
        res.add("update tbparam set param_value = 'fund_AutoLiqByPrd' where param_id = '" + gj + "';\n");
        res.add("update tbparam set param_value = 'fund_ZjMultiProcessesPrivate' where param_id = '" + zj + "';\n");

        res.add(STR_SPACE);
        String groupCode = STR_BLANK;
        if (STR_1.equals(gm)) {
            groupCode = "fund_daily_virtual_multi";
        } else if (STR_1.equals(zx)) {
            groupCode = "fund_daily_t1_multi";
        } else if (STR_1.equals(xy) || STR_1.equals(zj) || STR_1.equals(gjdf)) {
            groupCode = "fund_daily_xyt1_multi";
        } else if (STR_1.equals(gj)) {
            groupCode = "fund_daily_auto_liq_byprd";
        }
        StringBuilder group = new StringBuilder();
        group.append("delete from tbschedulegroup where sche_group_type = '1';\n");
        group.append("insert into tbschedulegroup (sche_page_code, sche_group_code, sche_group_name, sche_group_isuse, sche_group_type)\n");
        group.append("values ('fund_schedule' , '" + groupCode + "' , '1主流程' , '1' , '1');\n");
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
