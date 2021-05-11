package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.FunctionType;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.LogDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/05/03
 */
public class FundInfoController implements Initializable {

    @FXML
    private AnchorPane fundInfo;

    @FXML
    private Button scriptSubmit;

    @FXML
    private ProgressIndicator fundSchedule;

    @FXML
    private TextField filePath;

    @FXML
    private Button selectFile;

    @FXML
    private RadioButton modeAll;

    @FXML
    private RadioButton modeUpdate;

    @FXML
    private TableView<?> fundLog;

    private double progress = 0;

    Map<String, String> COMPONENT_KIND = new ConcurrentHashMap(16);

    Map<String, Map<String, String>> PRD_TEMPLATE = new ConcurrentHashMap(16);

    private static String CURRENT_TEMPLATE_CODE;

    private static String CURRENT_TEMPLATE_NAME;

    private static String SCRIPT_TYPE;

    @FXML
    void executeSelect(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("全部Excel文件", "*.xls")
        );
        /*fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("全部Excel文件", "*.xlsx")
        );*/
        File file = fileChooser.showOpenDialog(fundInfo.getScene().getWindow());
        if (file != null) {
            OutputUtils.clearLog(filePath);
            OutputUtils.info(filePath, file.getAbsolutePath());
        }
    }

    @FXML
    void selectModeAll(ActionEvent event) {
        OutputUtils.selected(modeAll, true);
        OutputUtils.selected(modeUpdate, false);
    }

    @FXML
    void selectModeUpdate(ActionEvent event) {
        OutputUtils.selected(modeAll, false);
        OutputUtils.selected(modeUpdate, true);
    }

    @FXML
    void executeSubmit(ActionEvent event) throws Exception {
        if (!CommonUtils.checkConfig(fundLog, FunctionType.FUND_INFO.getType())) {
            return;
        }
        setProgress(0);
        if (StringUtils.isBlank(filePath.getText())) {
            infoMsg("请选择基金信息Excel文件");
            return;
        }
        boolean selectModeAll = modeAll.isSelected();
        boolean selectModeUpdate = modeUpdate.isSelected();
        if (selectModeAll == false && selectModeUpdate == false) {
            OutputUtils.selected(modeAll, true);
            OutputUtils.selected(modeUpdate, false);
        }
        selectModeAll = modeAll.isSelected();
        if (selectModeAll) {
            SCRIPT_TYPE = String.valueOf(modeAll.getUserData());
        } else {
            SCRIPT_TYPE = String.valueOf(modeUpdate.getUserData());
        }
        updateProgress();
        generateScript();
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
        progress = value;
        Platform.runLater(() -> {
            fundSchedule.setProgress(progress);
        });
        fundSchedule.requestFocus();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            OutputUtils.clearLog(filePath);
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (StringUtils.isNotBlank(appConfigDto.getFundExcelPath())) {
                OutputUtils.info(filePath, appConfigDto.getFundExcelPath());
            }
            String mode = appConfigDto.getFundGenerateMode();
            if (StringUtils.isBlank(mode)) {
                OutputUtils.selected(modeAll, false);
                OutputUtils.selected(modeUpdate, false);
                return;
            }
            if (STR_1.equals(mode)) {
                OutputUtils.selected(modeAll, true);
                OutputUtils.selected(modeUpdate, false);
                return;
            }
            if (STR_2.equals(mode)) {
                OutputUtils.selected(modeAll, false);
                OutputUtils.selected(modeUpdate, true);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateScript() {
        new Thread(() -> {
            try {
                scriptSubmit.setDisable(true);
                Date date = new Date();
                OutputUtils.clearLog(fundLog);
                // 创建生成脚本目录
                AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                File pathFolder = new File(appConfigDto.getFundGeneratePath());
                if (!pathFolder.exists()) {
                    pathFolder.mkdirs();
                }
                // 打开workbook
                Workbook workbook = Workbook.getWorkbook(new File(filePath.getText()));

                // 生成基金配置数据
                String fileName = "15fund-product-field.sql";
                String productPath = appConfigDto.getFundGeneratePath() + "/" + fileName;
                File productFile = new File(productPath);
                FileWriter fileWriter = new FileWriter(productFile);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                // 1.1写入头部信息
                bufferedWriter.write("-- ***************************************************\n");
                bufferedWriter.write("-- TA6.0基金信息\n");
                bufferedWriter.write("-- 版权所述：TA研发二部\n");
                bufferedWriter.write("-- ***************************************************\n\n");

                // 加载sheet页数据
                Sheet[] sheetList = workbook.getSheets();

                // 字段信息
                Sheet dataElement = workbook.getSheet("基金&tbdataelement");

                // 模板信息
                Sheet prdTemplate = workbook.getSheet("基金模板&tbprdtemplate");

                // 2.1获取ComponentKind
                if (dataElement != null) {
                    initComponentKind(dataElement);
                }

                // 2.2获取模板信息
                if (prdTemplate != null) {
                    initPrdTemplate(prdTemplate);
                }

                // 2.2.1写 tbdataelement 数据
                if (dataElement != null) {
                    writeDataelement(bufferedWriter, dataElement);
                }

                Iterator<String> iterator = PRD_TEMPLATE.keySet().iterator();
                while (iterator.hasNext()) {
                    CURRENT_TEMPLATE_CODE = iterator.next();
                    CURRENT_TEMPLATE_NAME = PRD_TEMPLATE.get(CURRENT_TEMPLATE_CODE).get("templateName");

                    // 2.3写头部信息
                    writeHeadInfo(bufferedWriter);

                    // 2.4写 tbprdtemplate 数据
                    if (prdTemplate != null) {
                        writePrdTemplate(bufferedWriter, prdTemplate);
                    }

                    // 2.5写 tbelementgroup 数据
                    Sheet elementGroup = workbook.getSheet("基金分组&tbelementgroup");
                    if (elementGroup != null) {
                        writeElementGroup(bufferedWriter, elementGroup);
                    }

                    // 2.6写 tbtemplaterelgroup 数据
                    Sheet templateRelGroup = workbook.getSheet("分组模板&tbtemplaterelgroup");
                    if (templateRelGroup != null) {
                        writeTemplateRelGroup(bufferedWriter, templateRelGroup);
                    }

                    //2.8 写其他sheet页配置信息 列表 新增 修改
                    for (Sheet sheet : sheetList) {
                        if (sheet.getName().equals("基金列表&tbpageelement") || sheet.getName().equals("基金新增&tbpageelement") || sheet.getName().equals("基金修改&tbpageelement")) {
                            // 写tbpageelement数据
                            writePageElement(bufferedWriter, sheet);
                        }
                        if (sheet.getName().equals("基金&tbdataelement")) {
                            continue;
                        }
                        if (sheet.getName().equals("基金分组&tbelementgroup")) {
                            continue;
                        }
                        if (sheet.getName().equals("分组模板&tbtemplaterelgroup")) {
                            continue;
                        }
                        if (sheet.getName().equals("基金模板&tbprdtemplate")) {
                            continue;
                        }
                    }
                }
                bufferedWriter.write("commit;\n");
                bufferedWriter.close();
                fileWriter.close();
                fundSchedule.setProgress(1);
                LoggerUtils.writeFundInfo(date, productPath);
            } catch (Exception e) {
                e.printStackTrace();
                infoMsg(e.getMessage());
            } finally {
                scriptSubmit.setDisable(false);
            }
            setProgress(1);
        }).start();
    }

    /**
     * 获取 tbdataelement 信息
     *
     * @param sheet
     */
    private void initComponentKind(Sheet sheet) throws IOException {
        infoMsg("初始化component_kind 开始");
        COMPONENT_KIND.clear();
        int rows = sheet.getRows();
        for (int i = 1; i < rows; i++) {
            String column = getCellReal(sheet, 4, i);
            String kind = getCellReal(sheet, 11, i);
            if (STR_EMPTY.equals(kind.trim())) {
                COMPONENT_KIND.put(column, "'1'");
            } else {
                COMPONENT_KIND.put(column, "'" + kind + "'");
            }
        }
        infoMsg("初始化component_kind 结束");
    }

    /**
     * 获取表格数据
     *
     * @param sheet
     * @param i
     * @param j
     * @return
     */
    private String getCell(Sheet sheet, int i, int j) {
        return "'" + getCellReal(sheet, i, j) + "'";
    }

    /**
     * 获取表格实际值
     *
     * @param sheet
     * @param i
     * @param j
     * @author: humm23693
     * @date: 2021/04/17
     * @return:
     */
    private String getCellReal(Sheet sheet, int i, int j) {
        if (sheet.getCell(i, j).getContents().equals("")) {
            return STR_SPACE;
        } else {
            return sheet.getCell(i, j).getContents();
        }
    }

    /**
     * @param sheet
     * @author: 初始化模板信息
     * @date: 2021/04/21
     * @return:
     */
    private void initPrdTemplate(Sheet sheet) {
        infoMsg("初始化模板信息 开始");
        PRD_TEMPLATE.clear();
        int rows = sheet.getRows();
        for (int i = 1; i < rows; i++) {
            String templateCode = getCellReal(sheet, 2, i);
            String remark = getCellReal(sheet, 7, i);
            Map template = new HashMap(2);
            template.put("templateCode", templateCode);
            template.put("templateName", remark);
            PRD_TEMPLATE.put(templateCode, template);
        }
        infoMsg("初始化模板信息 结束");
    }

    /**
     * 写 tbdataelement 表
     *
     * @param bufferedWriter
     */
    private void writeDataelement(BufferedWriter bufferedWriter, Sheet sheet) throws IOException {
        infoMsg(sheet.getName() + "生成 开始");
        int rows = sheet.getRows();
        bufferedWriter.write("-- " + sheet.getName() + " 开始 \n");
        if (STR_1.equals(SCRIPT_TYPE)) {
            bufferedWriter.write("delete from tbdataelement;\n");
        }
        for (int i = 1; i < rows; i++) {
            if (STR_EMPTY.equals(getCellReal(sheet, 1, i).trim())) {
                continue;
            }
            if (!STR_1.equals(SCRIPT_TYPE)) {
                if (getCellReal(sheet, 0, i).contains(ANNOTATION)) {
                    continue;
                }
                bufferedWriter.write("delete from tbdataelement where id = " + getCell(sheet, 1, i) + ";\n");
            }
            String sql = "insert into tbdataelement (id, table_name, table_kind, field_code, persistence_flag, dict_key, rel_table, rel_field, rel_condition, reserve) values (";
            if (getCellReal(sheet, 0, i).contains(ANNOTATION)) {
                sql = "-- insert into tbdataelement (id, table_name, table_kind, field_code, persistence_flag, dict_key, rel_table, rel_field, rel_condition, reserve) values (";
            }
            sql += getCell(sheet, 1, i) + ","
                    + getCell(sheet, 2, i) + ","
                    + getCell(sheet, 3, i) + ","
                    + getCell(sheet, 4, i) + ","
                    + getCell(sheet, 5, i) + ","
                    + getCell(sheet, 6, i) + ","
                    + getCell(sheet, 7, i) + ","
                    + getCell(sheet, 8, i) + ","
                    + getCell(sheet, 9, i) + ","
                    + getCell(sheet, 10, i)
                    + ");";
            bufferedWriter.write(sql);
            bufferedWriter.write("\n");
        }
        bufferedWriter.write("-- " + sheet.getName() + " 结束 \n");
        bufferedWriter.write("\n");
        infoMsg(sheet.getName() + "生成 结束");
    }

    /**
     * 写 tbprdtemplate 表
     *
     * @param bufferedWriter
     */
    private void writeHeadInfo(BufferedWriter bufferedWriter) throws IOException {
        if (!STR_1.equals(SCRIPT_TYPE)) {
            return;
        }
        infoMsg(CURRENT_TEMPLATE_NAME + " 头部信息生成 开始");
        bufferedWriter.write("-- " + CURRENT_TEMPLATE_NAME + " 头部信息 开始 \n");
        bufferedWriter.write("delete from tbprdtemplate where template_code like '" + CURRENT_TEMPLATE_CODE + "%';\n");
        bufferedWriter.write("delete from tbtemplaterelgroup where template_code like '" + CURRENT_TEMPLATE_CODE + "%';\n");
        bufferedWriter.write("delete from tbpageelement where id like '" + CURRENT_TEMPLATE_CODE + "%';\n");
        bufferedWriter.write("delete from tbelementgroup where id like '" + CURRENT_TEMPLATE_CODE + "%';\n");
        bufferedWriter.write("-- " + CURRENT_TEMPLATE_NAME + " 头部信息 结束 \n\n");
        infoMsg(CURRENT_TEMPLATE_NAME + " 头部信息生成 结束");
    }

    /**
     * 写 tbprdtemplate 表
     *
     * @param bufferedWriter
     */
    private void writePrdTemplate(BufferedWriter bufferedWriter, Sheet sheet) throws IOException {
        infoMsg(sheet.getName() + " " + CURRENT_TEMPLATE_NAME + "生成 开始");
        int rows = sheet.getRows();
        bufferedWriter.write("-- " + sheet.getName() + " " + CURRENT_TEMPLATE_NAME + " 开始 \n");
        for (int i = 1; i < rows; i++) {
            if (STR_EMPTY.equals(getCellReal(sheet, 1, i).trim())) {
                continue;
            }
            if (!getCellReal(sheet, 2, i).trim().startsWith(CURRENT_TEMPLATE_CODE)) {
                continue;
            }
            if (!STR_1.equals(SCRIPT_TYPE)) {
                if (getCellReal(sheet, 0, i).contains(ANNOTATION)) {
                    continue;
                }
                bufferedWriter.write("delete from tbprdtemplate where template_code = " + getCell(sheet, 2, i) + ";\n");
            }
            String sql = "insert into tbprdtemplate (bank_no, template_code, template_short_name, template_name, prd_type, life_cycle_url, remark, remark1, remark2, remark3) values (";
            if (getCellReal(sheet, 0, i).contains(ANNOTATION)) {
                sql = "-- insert into tbprdtemplate (bank_no, template_code, template_short_name, template_name, prd_type, life_cycle_url, remark, remark1, remark2, remark3) values (";
            }
            sql += getCell(sheet, 1, i) + ","
                    + getCell(sheet, 2, i) + ","
                    + getCell(sheet, 3, i) + ","
                    + getCell(sheet, 4, i) + ","
                    + getCell(sheet, 5, i) + ","
                    + getCell(sheet, 6, i) + ","
                    + getCell(sheet, 7, i) + ","
                    + getCell(sheet, 8, i) + ","
                    + getCell(sheet, 9, i) + ","
                    + getCell(sheet, 10, i)
                    + ");";
            bufferedWriter.write(sql);
            bufferedWriter.write("\n");
        }
        bufferedWriter.write("-- " + sheet.getName() + " " + CURRENT_TEMPLATE_NAME + " 结束 \n");
        bufferedWriter.write("\n");
        infoMsg(sheet.getName() + " " + CURRENT_TEMPLATE_NAME + "生成 结束");
    }

    /**
     * 写 tbelementgroup 表
     *
     * @param bufferedWriter
     */
    private void writeElementGroup(BufferedWriter bufferedWriter, Sheet sheet) throws IOException {
        infoMsg(sheet.getName() + " " + CURRENT_TEMPLATE_NAME + "生成 开始");
        int rows = sheet.getRows();
        bufferedWriter.write("-- " + sheet.getName() + " " + CURRENT_TEMPLATE_NAME + " 开始 \n");
        for (int i = 1; i < rows; i++) {
            if (STR_EMPTY.equals(getCellReal(sheet, 1, i).trim())) {
                continue;
            }
            if (!getCellReal(sheet, 1, i).trim().startsWith(CURRENT_TEMPLATE_CODE)) {
                continue;
            }
            if (!STR_1.equals(SCRIPT_TYPE)) {
                if (getCellReal(sheet, 0, i).contains(ANNOTATION)) {
                    continue;
                }
                bufferedWriter.write("delete from tbelementgroup where id = " + getCell(sheet, 1, i) + ";\n");
            }
            String sql = "insert into tbelementgroup (id,parent_id,group_code,group_name,group_kind,group_label ,control_kind ,true_value,control_table,control_order,on_show,on_hide,on_init,on_submit,reserve) values (";
            if (getCellReal(sheet, 0, i).contains(ANNOTATION)) {
                sql = "-- insert into tbelementgroup (id,parent_id,group_code,group_name,group_kind,group_label ,control_kind ,true_value,control_table,control_order,on_show,on_hide,on_init,on_submit,reserve) values (";
            }
            sql += getCell(sheet, 1, i) + ","
                    + getCell(sheet, 2, i) + ","
                    + getCell(sheet, 3, i) + ","
                    + getCell(sheet, 4, i) + ","
                    + getCell(sheet, 5, i) + ","
                    + getCell(sheet, 6, i) + ","
                    + getCell(sheet, 7, i) + ","
                    + getCell(sheet, 8, i) + ","
                    + getCell(sheet, 9, i) + ","
                    + getCell(sheet, 10, i) + ","
                    + getCell(sheet, 11, i) + ","
                    + getCell(sheet, 12, i) + ","
                    + getCell(sheet, 13, i) + ","
                    + getCell(sheet, 14, i) + ","
                    + getCell(sheet, 15, i)
                    + ");";
            bufferedWriter.write(sql);
            bufferedWriter.write("\n");
        }
        bufferedWriter.write("-- " + sheet.getName() + " " + CURRENT_TEMPLATE_NAME + " 结束 \n");
        bufferedWriter.write("\n");
        infoMsg(sheet.getName() + " " + CURRENT_TEMPLATE_NAME + "生成 结束");
    }

    /**
     * 写 tbtemplaterelgroup 表
     *
     * @param bufferedWriter
     */
    private void writeTemplateRelGroup(BufferedWriter bufferedWriter, Sheet sheet) throws IOException {
        infoMsg(sheet.getName() + " " + CURRENT_TEMPLATE_NAME + "生成 开始");
        int rows = sheet.getRows();
        bufferedWriter.write("-- " + sheet.getName() + " " + CURRENT_TEMPLATE_NAME + " 开始 \n");
        for (int i = 1; i < rows; i++) {
            if (STR_EMPTY.equals(getCellReal(sheet, 1, i).trim())) {
                continue;
            }
            if (!getCellReal(sheet, 1, i).trim().startsWith(CURRENT_TEMPLATE_CODE)) {
                continue;
            }
            if (!STR_1.equals(SCRIPT_TYPE)) {
                if (getCellReal(sheet, 0, i).contains(ANNOTATION)) {
                    continue;
                }
                bufferedWriter.write("delete from tbtemplaterelgroup where id = " + getCell(sheet, 1, i) + ";\n");
            }
            String sql = "insert into tbtemplaterelgroup(id, menu_code, template_code, req_kind, group_id, group_order) values (";
            if (getCellReal(sheet, 0, i).contains(ANNOTATION)) {
                sql = "-- insert into tbtemplaterelgroup(id, menu_code, template_code, req_kind, group_id, group_order) values (";
            }
            sql += getCell(sheet, 1, i) + ","
                    + getCell(sheet, 2, i) + ","
                    + getCell(sheet, 3, i) + ","
                    + getCell(sheet, 4, i) + ","
                    + getCell(sheet, 5, i) + ","
                    + getCell(sheet, 6, i)
                    + ");";
            bufferedWriter.write(sql);
            bufferedWriter.write("\n");
        }
        bufferedWriter.write("-- " + sheet.getName() + " " + CURRENT_TEMPLATE_NAME + " 结束 \n");
        bufferedWriter.write("\n");
        infoMsg(sheet.getName() + " " + CURRENT_TEMPLATE_NAME + "生成 结束");
    }

    /**
     * 写 tbpageelement 表
     *
     * @param bufferedWriter
     */
    private void writePageElement(BufferedWriter bufferedWriter, Sheet sheet) throws IOException {
        infoMsg(sheet.getName() + " " + CURRENT_TEMPLATE_NAME + "生成 开始");
        int rows = sheet.getRows();
        bufferedWriter.write("-- " + sheet.getName() + " " + CURRENT_TEMPLATE_NAME + " 开始 \n");
        for (int i = 1; i < rows; i++) {
            if (STR_EMPTY.equals(getCellReal(sheet, 1, i).trim())) {
                continue;
            }
            if (!getCellReal(sheet, 1, i).trim().startsWith(CURRENT_TEMPLATE_CODE)) {
                continue;
            }
            if (!STR_1.equals(SCRIPT_TYPE)) {
                if (getCellReal(sheet, 0, i).contains(ANNOTATION)) {
                    continue;
                }
                bufferedWriter.write("delete from tbpageelement where id = " + getCell(sheet, 1, i) + ";\n");
            }
            String sql = "insert into tbpageelement (id, data_id, group_id, element_order, element_code, element_name, component_kind, component_length, prefix_label, suffix_label, display_flag, readonly_flag, line_flag, required_flag, location_flag, sort_flag, default_value, show_format, check_format, on_init, on_change, on_submit, empty_text, visable, suffix_cls, prompt, max_length, min_length, max_value, min_value, reserve) values (";
            if (getCellReal(sheet, 0, i).contains(ANNOTATION)) {
                sql = "-- insert into tbpageelement (id, data_id, group_id, element_order, element_code, element_name, component_kind, component_length, prefix_label, suffix_label, display_flag, readonly_flag, line_flag, required_flag, location_flag, sort_flag, default_value, show_format, check_format, on_init, on_change, on_submit, empty_text, visable, suffix_cls, prompt, max_length, min_length, max_value, min_value, reserve) values (";
            }
            // 获取component_kind
            String componentKind = getComponentKind(sheet.getName(), getCellReal(sheet, 5, i));

            sql += getCell(sheet, 1, i) + ","
                    + getCell(sheet, 2, i) + ","
                    + getCell(sheet, 3, i) + ","
                    + getCell(sheet, 4, i) + ","
                    + getCell(sheet, 5, i) + ","
                    + getCell(sheet, 6, i) + ","
                    + componentKind + ","
                    + getCell(sheet, 8, i) + ","
                    + getCell(sheet, 9, i) + ","
                    + getCell(sheet, 10, i) + ","
                    + getCell(sheet, 11, i) + ","
                    + getCell(sheet, 12, i) + ","
                    + getCell(sheet, 13, i) + ","
                    + getCell(sheet, 14, i) + ","
                    + getCell(sheet, 15, i) + ","
                    + getCell(sheet, 16, i) + ","
                    + getCell(sheet, 17, i) + ","
                    + getCell(sheet, 18, i) + ","
                    + getCell(sheet, 19, i) + ","
                    + getCell(sheet, 20, i) + ","
                    + getCell(sheet, 21, i) + ","
                    + getCell(sheet, 22, i) + ","
                    + getCell(sheet, 23, i) + ","
                    + getCell(sheet, 24, i) + ","
                    + getCell(sheet, 25, i) + ","
                    + getCell(sheet, 26, i) + ","
                    + getCell(sheet, 27, i) + ","
                    + getCell(sheet, 28, i) + ","
                    + getCell(sheet, 29, i) + ","
                    + getCell(sheet, 30, i) + ","
                    + getCell(sheet, 31, i) + ""
                    + ");";
            bufferedWriter.write(sql);
            bufferedWriter.write("\n");
        }
        bufferedWriter.write("-- " + sheet.getName() + " " + CURRENT_TEMPLATE_NAME + " 结束 \n");
        bufferedWriter.write("\n");
        infoMsg(sheet.getName() + " " + CURRENT_TEMPLATE_NAME + "生成 结束");
    }

    /**
     * 获取 component_kind
     *
     * @param sheetName
     * @param column
     * @author: humm23693
     * @date: 2021/04/17
     * @return:
     */
    private String getComponentKind(String sheetName, String column) {
        String componentKind = "' '";
        // 列表页面
        if (sheetName.equals("基金列表&tbpageelement")) {
            // 选择框
            if ("'A'".equals(COMPONENT_KIND.get(column)) || "'H'".equals(COMPONENT_KIND.get(column))) {
                componentKind = "'Z'";
            }
            // 日期选择框
            if ("'5'".equals(COMPONENT_KIND.get(column))) {
                componentKind = "'L'";
            }
        } else {
            // 新增 修改页面
            componentKind = COMPONENT_KIND.get(column);
        }
        return componentKind;
    }

    private void infoMsg(String msg) {
        LogDto logDto = new LogDto();
        logDto.setTime(CommonUtils.getCurrentDateTime1());
        logDto.setMsg(msg);
        OutputUtils.info(fundLog, logDto);
    }
}
