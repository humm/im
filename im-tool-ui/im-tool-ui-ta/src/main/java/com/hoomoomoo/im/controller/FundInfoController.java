package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.LogDto;
import com.hoomoomoo.im.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.FUND_INFO;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/05/03
 */
public class FundInfoController extends BaseController implements Initializable {

    @FXML
    private AnchorPane fundInfo;

    @FXML
    private Button scriptSubmit;

    @FXML
    private TextField filePath;

    @FXML
    private TableView fundLog;

    Map<String, String> COMPONENT_KIND = new ConcurrentHashMap(16);

    Map<String, Map<String, String>> PRD_TEMPLATE = new ConcurrentHashMap(16);

    private static String CURRENT_TEMPLATE_CODE;

    private static String CURRENT_TEMPLATE_NAME;

    private static boolean STD = false;

    List<String> cache = new ArrayList<>(16);


    @FXML
    void executeSelect(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("全部Excel文件", "*.xls"));
        File file = fileChooser.showOpenDialog(fundInfo.getScene().getWindow());
        if (file != null) {
            OutputUtils.clearLog(filePath);
            OutputUtils.info(filePath, file.getAbsolutePath());
        }
    }

    @FXML
    void executeClear(ActionEvent event) {
        cache.clear();
        infoMsg("缓存清除完成");
    }

    @FXML
    void executeSubmit(ActionEvent event) {
        try {
            LoggerUtils.info(String.format(BaseConst.MSG_USE, FUND_INFO.getName()));
            if (!TaCommonUtil.checkConfig(fundLog, FUND_INFO.getCode())) {
                return;
            }
            setProgress(0);
            if (StringUtils.isBlank(filePath.getText())) {
                infoMsg("请选择基金信息Excel文件");
                return;
            }
            updateProgress();
            generateScript();
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(fundLog, e.getMessage());
        }
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
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    private void generateScript() {
        new Thread(() -> {
            try {
                scriptSubmit.setDisable(true);
                Date date = new Date();
                OutputUtils.clearLog(fundLog);
                String configSqlPath = filePath.getText().replace(".xls", ".oracle.sql");
                if (CollectionUtils.isEmpty(cache)) {
                    cache = FileUtils.readNormalFile(configSqlPath, false);
                }

                // 创建生成脚本目录
                AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                File pathFolder = new File(appConfigDto.getFundGeneratePath());
                if (!pathFolder.exists()) {
                    pathFolder.mkdirs();
                }
                // 打开workbook
                Workbook workbook = Workbook.getWorkbook(new File(filePath.getText()));

                Sheet tbSceneInfo = workbook.getSheet("场景信息tbsceneinfo");
                if (tbSceneInfo != null) {
                    STD = true;
                }
                // 生成基金配置数据
                String fileName = "15fund-product-field.oracle.sql";
                if (STD) {
                    fileName = "15fund-product-field-std.oracle.sql";
                }
                String productPath = appConfigDto.getFundGeneratePath() + "/" + fileName;
                File productFile = new File(productPath);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(productFile), BaseConst.ENCODING_UTF8));

                // 1.1写入头部信息
                bufferedWriter.write("-- ***************************************************\n");
                bufferedWriter.write("-- TA6.0基金信息\n");
                bufferedWriter.write("-- 版权所述：TA研发二部\n");
                bufferedWriter.write("-- ***************************************************\n\n");

                // 加载sheet页数据
                Sheet[] sheetList = workbook.getSheets();

                // 字段信息
                Sheet dataElement = workbook.getSheet("基金tbdataelement");

                // 2.1获取ComponentKind
                if (dataElement != null) {
                    initComponentKind(dataElement);
                }

                // 模板信息
                Sheet prdTemplate = workbook.getSheet("基金模板tbprdtemplate");

                // 2.2获取模板信息
                if (prdTemplate != null) {
                    initPrdTemplate(prdTemplate);
                }

                // 2.2.1写 tbdataelement 数据
                if (dataElement != null) {
                    writeDataelement(bufferedWriter, dataElement);
                }

                if (tbSceneInfo != null) {
                    writeSceneInfo(bufferedWriter, tbSceneInfo);
                }
                Sheet tbScenTemplate = workbook.getSheet("场景模板tbscentemplate");
                if (tbScenTemplate != null) {
                    writeScenTemplate(bufferedWriter, tbScenTemplate);
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
                    Sheet elementGroup = workbook.getSheet("基金分组tbelementgroup");
                    if (elementGroup != null) {
                        writeElementGroup(bufferedWriter, elementGroup);
                    }

                    // 2.6写 tbtemplaterelgroup 数据
                    Sheet templateRelGroup = workbook.getSheet("分组模板tbtemplaterelgroup");
                    if (templateRelGroup != null) {
                        writeTemplateRelGroup(bufferedWriter, templateRelGroup);
                    }

                    //2.8 写其他sheet页配置信息 列表 新增 修改
                    for (Sheet sheet : sheetList) {
                        if (sheet.getName().equals("基金列表tbpageelement") || sheet.getName().equals("基金新增tbpageelement") || sheet.getName().equals("基金修改tbpageelement")) {
                            // 写tbpageelement数据
                            writePageElement(bufferedWriter, sheet);
                        }
                        if (sheet.getName().equals("基金tbdataelement")) {
                            continue;
                        }
                        if (sheet.getName().equals("基金分组tbelementgroup")) {
                            continue;
                        }
                        if (sheet.getName().equals("分组模板tbtemplaterelgroup")) {
                            continue;
                        }
                        if (sheet.getName().equals("基金模板tbprdtemplate")) {
                            continue;
                        }
                    }
                }
                bufferedWriter.write("commit;\n");
                bufferedWriter.close();

                List<String> content = FileUtils.readNormalFile(productPath, false);

                infoMsg("mysql版本生成 开始");
                String fileNameMysql = "15fund-product-field.mysql.sql";
                if (STD) {
                    fileNameMysql = "15fund-product-field-std.mysql.sql";
                }
                String productPathMysql = appConfigDto.getFundGeneratePath() + "/" + fileNameMysql;
                FileUtils.writeFile(productPathMysql, content, false);
                infoMsg("mysql版本生成 结束");

                infoMsg("pg版本生成 开始");
                String fileNamePg = "15fund-product-field.pg.sql";
                if (STD) {
                    fileNamePg = "15fund-product-field-std.pg.sql";
                }
                String productPathPg = appConfigDto.getFundGeneratePath() + "/" + fileNamePg;
                if (CollectionUtils.isNotEmpty(content)) {
                    for (int j=0; j<content.size(); j++) {
                        String item = content.get(j);
                        item = item.replace("delete from tbpageelement where id like", "delete from tbpageelement where cast(id as varchar) like");
                        item = item.replace("delete from tbelementgroup where id like", "delete from tbelementgroup where cast(id as varchar) like");
                        item = item.replace("delete from tbdataelement where id like", "delete from tbdataelement where cast(id as varchar) like");
                        content.set(j, item);
                    }
                }
                FileUtils.writeFile(productPathPg, content, false);
                infoMsg("pg版本生成 结束");
                List<String> logList = new ArrayList(3);
                logList.add(productPath);
                logList.add(productPathPg);
                logList.add(productPathMysql);
                LoggerUtils.writeFundInfo(date, logList);
                infoMsg("生成升级脚本 开始");
                List<String> sqlInfo = FileUtils.readNormalFile(configSqlPath, false);
                List<String> sql = buildSql(appConfigDto, cache, sqlInfo);
                if (CollectionUtils.isEmpty(sql)) {
                    sql.add("-- 脚本无差异");
                }
                FileUtils.writeFile(configSqlPath.replace("oracle", "temp"), sql, false);
                infoMsg("生成升级脚本 结束");
                infoMsg("执行完成");
                schedule.setProgress(1);
            } catch (Exception e) {
                LoggerUtils.info(e);
                infoMsg(e.getMessage());
            } finally {
                setProgress(1);
                scriptSubmit.setDisable(false);
            }
        }).start();
    }

    private List<String> buildSql(AppConfigDto appConfigDto, List<String> oldSql, List<String> newSql) throws Exception {
        List<String> sql = new ArrayList<>();
        if (CollectionUtils.isEmpty(oldSql)) {
            return sql;
        }
        Map<String, String> oldSqlMap = new LinkedHashMap<>();
        Map<String, String> newSqlMap = new LinkedHashMap<>();
        List<String> deleteSql = new ArrayList<>();
        List<String> addSql = new ArrayList<>();
        for (String item : oldSql) {
            oldSqlMap.put(item, item);
        }
        for (String item : newSql) {
            newSqlMap.put(item, item);
        }
        for (int i=0; i<oldSql.size(); i++) {
            String item = oldSql.get(i);
            if (!newSqlMap.containsKey(item) && i != 0) {
                String partSql = oldSql.get(i-1) + SYMBOL_NEXT_LINE + oldSql.get(i);
                deleteSql.add(partSql);
            }
        }
        for (int i=0; i<newSql.size(); i++) {
            String item = newSql.get(i);
            if (!oldSqlMap.containsKey(item) && i != 0) {
                String partSql = newSql.get(i-1) + SYMBOL_NEXT_LINE + newSql.get(i);
                addSql.add(partSql);
            }
        }
        ScriptUpdateController scriptUpdateController = new ScriptUpdateController();
        appConfigDto.setScriptUpdateGenerateType(STR_1);
        List<String> delete = scriptUpdateController.generatesql(appConfigDto, String.join(SYMBOL_EMPTY, deleteSql));
        appConfigDto.setScriptUpdateGenerateType(STR_2);
        List<String> add = scriptUpdateController.generatesql(appConfigDto, String.join(SYMBOL_EMPTY, addSql));
        if (CollectionUtils.isEmpty(add)) {
            sql.addAll(delete);
        } else {
            for (String ele : delete) {
                boolean hasKey = false;
                inner: for (String item : add) {
                    if (item.replaceAll(SYMBOL_NEXT_LINE, SYMBOL_EMPTY).contains(ele.replaceAll(SYMBOL_NEXT_LINE, SYMBOL_EMPTY))) {
                        hasKey = true;
                        break inner;
                    }
                }
                if (!hasKey) {
                    sql.add(ele);
                }
            }
            sql.addAll(add);
        }
        return sql;
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
            if (SYMBOL_EMPTY.equals(kind.trim())) {
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
            return BaseConst.SYMBOL_SPACE;
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
        bufferedWriter.write("delete from tbdataelement where id like '" + getCell(sheet, 1, 1).substring(1, 5) + "%';\n");
        for (int i = 1; i < rows; i++) {
            if (SYMBOL_EMPTY.equals(getCellReal(sheet, 1, i).trim())) {
                continue;
            }
            String sql = "insert into tbdataelement (id, table_name, table_kind, field_code, persistence_flag, " +
                    "dict_key, rel_table, rel_field, rel_condition, reserve) \nvalues (";
            if (getCellReal(sheet, 0, i).contains(BaseConst.ANNOTATION_TYPE_NORMAL)) {
                sql = "-- insert into tbdataelement (id, table_name, table_kind, field_code, persistence_flag, dict_key, rel_table, rel_field, rel_condition, reserve) \n-- values (";
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
        infoMsg(CURRENT_TEMPLATE_NAME + " 头部信息生成 开始");
        bufferedWriter.write("-- " + CURRENT_TEMPLATE_NAME + " 头部信息 开始 \n");
        bufferedWriter.write("delete from tbprdtemplate where prd_type = '5' and template_code = '" + CURRENT_TEMPLATE_CODE + "';\n");
        bufferedWriter.write("delete from tbtemplaterelgroup where template_code = '" + CURRENT_TEMPLATE_CODE + "';\n");
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
    private void writeSceneInfo(BufferedWriter bufferedWriter, Sheet sheet) throws IOException {
        infoMsg(sheet.getName() + " 生成 开始");
        int rows = sheet.getRows();
        bufferedWriter.write("-- " + sheet.getName() + " 开始 \n");
        for (int i = 1; i < rows; i++) {
            bufferedWriter.write("delete from tbsceneinfo where scene_code = " + getCell(sheet, 1, i) + ";\n");
            String sql = "insert into tbsceneinfo (scene_code, scene_name, scene_type) \nvalues (";
            if (getCellReal(sheet, 0, i).contains(BaseConst.ANNOTATION_TYPE_NORMAL)) {
                sql = "-- insert into tbsceneinfo (scene_code, scene_name, scene_type) \n-- values (";
            }
            sql += getCell(sheet, 1, i) + ","
                    + getCell(sheet, 2, i) + ","
                    + getCell(sheet, 3, i);
            sql += ");";
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
    private void writeScenTemplate(BufferedWriter bufferedWriter, Sheet sheet) throws IOException {
        infoMsg(sheet.getName() + " 生成 开始");
        int rows = sheet.getRows();
        bufferedWriter.write("-- " + sheet.getName() + " 开始 \n");
        for (int i = 1; i < rows; i++) {
            bufferedWriter.write("delete from tbscentemplate where template_code = " + getCell(sheet, 1, i) + ";\n");
            String sql = "insert into tbscentemplate (template_code, template_name, scene_code, rel_template_code) \nvalues (";
            if (getCellReal(sheet, 0, i).contains(BaseConst.ANNOTATION_TYPE_NORMAL)) {
                sql = "-- insert into tbscentemplate (template_code, template_name, scene_code, rel_template_code) \n-- values (";
            }
            sql += getCell(sheet, 1, i) + ","
                    + getCell(sheet, 2, i) + ","
                    + getCell(sheet, 3, i)+ ","
                    + getCell(sheet, 4, i);
            sql += ");";
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
    private void writePrdTemplate(BufferedWriter bufferedWriter, Sheet sheet) throws IOException {
        infoMsg(sheet.getName() + " " + CURRENT_TEMPLATE_NAME + "生成 开始");
        int rows = sheet.getRows();
        bufferedWriter.write("-- " + sheet.getName() + " " + CURRENT_TEMPLATE_NAME + " 开始 \n");
        for (int i = 1; i < rows; i++) {
            if (SYMBOL_EMPTY.equals(getCellReal(sheet, 1, i).trim())) {
                continue;
            }
            if (!getCellReal(sheet, 2, i).trim().startsWith(CURRENT_TEMPLATE_CODE)) {
                continue;
            }
            String sql = "insert into tbprdtemplate (bank_no, template_code, template_short_name, template_name, prd_type, life_cycle_url, remark, remark1, remark2, remark3) \nvalues (";
            if (getCellReal(sheet, 0, i).contains(BaseConst.ANNOTATION_TYPE_NORMAL)) {
                sql = "-- insert into tbprdtemplate (bank_no, template_code, template_short_name, template_name, prd_type, life_cycle_url, remark, remark1, remark2, remark3) \n-- values (";
            }
            if (STD) {
                sql = sql.replace(")", ", template_type, template_type_detail)");
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
                    + getCell(sheet, 10, i);
            if (STD) {
                sql += "," + getCell(sheet, 11, i) + "," + getCell(sheet, 12, i);
            }
            sql += ");";
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
            if (SYMBOL_EMPTY.equals(getCellReal(sheet, 1, i).trim())) {
                continue;
            }
            if (!getCellReal(sheet, 1, i).trim().startsWith(CURRENT_TEMPLATE_CODE)) {
                continue;
            }
            String sql = "insert into tbelementgroup (id,parent_id,group_code,group_name,group_kind,group_label ,control_kind ,true_value,control_table,control_order,on_show,on_hide,on_init,on_submit,reserve) \nvalues (";
            if (getCellReal(sheet, 0, i).contains(BaseConst.ANNOTATION_TYPE_NORMAL)) {
                sql = "-- insert into tbelementgroup (id,parent_id,group_code,group_name,group_kind,group_label ,control_kind ,true_value,control_table,control_order,on_show,on_hide,on_init,on_submit,reserve) \n-- values (";
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
        int columns = sheet.getColumns();
        bufferedWriter.write("-- " + sheet.getName() + " " + CURRENT_TEMPLATE_NAME + " 开始 \n");
        for (int i = 1; i < rows; i++) {
            if (SYMBOL_EMPTY.equals(getCellReal(sheet, 1, i).trim())) {
                continue;
            }
            if (!getCellReal(sheet, 1, i).trim().startsWith(CURRENT_TEMPLATE_CODE)) {
                continue;
            }
            String sql = "insert into tbtemplaterelgroup(id, menu_code, template_code, req_kind, group_id, group_order) \nvalues (";
            if (getCellReal(sheet, 0, i).contains(BaseConst.ANNOTATION_TYPE_NORMAL)) {
                sql = "-- insert into tbtemplaterelgroup(id, menu_code, template_code, req_kind, group_id, group_order) \n-- values (";
            }
            if (STD) {
                sql = sql.replace(")", ", flag, reserve, app_group)");
            }
            sql += getCell(sheet, 1, i) + ","
                    + getCell(sheet, 2, i) + ","
                    + getCell(sheet, 3, i) + ","
                    + getCell(sheet, 4, i) + ","
                    + getCell(sheet, 5, i) + ","
                    + getCell(sheet, 6, i);
            if (STD) {
                sql += "," + getCell(sheet, 7, i) + "," + getCell(sheet, 8, i) + "," + getCell(sheet, 9, i);
            }
            sql += ");";
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
        int columns = sheet.getColumns();
        bufferedWriter.write("-- " + sheet.getName() + " " + CURRENT_TEMPLATE_NAME + " 开始 \n");
        for (int i = 1; i < rows; i++) {
            if (SYMBOL_EMPTY.equals(getCellReal(sheet, 1, i).trim())) {
                continue;
            }
            if (!getCellReal(sheet, 1, i).trim().startsWith(CURRENT_TEMPLATE_CODE)) {
                continue;
            }
            String sql = "insert into tbpageelement (id, data_id, group_id, element_order, element_code, element_name, component_kind, component_length, prefix_label, suffix_label, display_flag, readonly_flag, line_flag, required_flag, location_flag, sort_flag, default_value, show_format, check_format, on_init, on_change, on_submit, empty_text, visable, suffix_cls, prompt, max_length, min_length, max_value, min_value, reserve) \nvalues (";
            if (getCellReal(sheet, 0, i).contains(BaseConst.ANNOTATION_TYPE_NORMAL)) {
                sql = "-- insert into tbpageelement (id, data_id, group_id, element_order, element_code, element_name, component_kind, component_length, prefix_label, suffix_label, display_flag, readonly_flag, line_flag, required_flag, location_flag, sort_flag, default_value, show_format, check_format, on_init, on_change, on_submit, empty_text, visable, suffix_cls, prompt, max_length, min_length, max_value, min_value, reserve) \n-- values (";
            }
            if (STD) {
                sql = sql.replace(")", ", top_flag)");
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
                    + getCell(sheet, 31, i);
            if (STD) {
                sql += "," + getCell(sheet, 32, i);
            }
            sql += ");";
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
        if (sheetName.equals("基金列表tbpageelement")) {
            // 选择框
            if ("'A'".equals(COMPONENT_KIND.get(column)) || "'H'".equals(COMPONENT_KIND.get(column))) {
                componentKind = "'Z'";
            } else if ("'5'".equals(COMPONENT_KIND.get(column))) {
                // 日期选择框
                componentKind = "'L'";
            } else {
                if (!"'1'".equals(COMPONENT_KIND.get(column))) {
                    componentKind = COMPONENT_KIND.get(column);
                }
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
