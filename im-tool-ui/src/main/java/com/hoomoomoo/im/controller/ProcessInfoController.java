package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.cache.TaskMemoryCache;
import com.hoomoomoo.im.cache.TransMemoryCache;
import com.hoomoomoo.im.consts.FunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/05/07
 */
public class ProcessInfoController implements Initializable {

    @FXML
    private AnchorPane processInfo;

    @FXML
    private Button submit;

    @FXML
    private ProgressIndicator schedule;

    @FXML
    private TextField filePath;

    @FXML
    private Button selectFile;

    @FXML
    private TextField taCode;

    @FXML
    private TableView<?> log;

    private double progress = 0;


    @FXML
    void executeSelect(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("全部Excel文件", "*.xls")
        );
        /*fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("全部Excel文件", "*.xlsx")
        );*/
        File file = fileChooser.showOpenDialog(processInfo.getScene().getWindow());
        if (file != null) {
            OutputUtils.clearLog(filePath);
            OutputUtils.info(filePath, file.getAbsolutePath());
        }
    }

    @FXML
    void executeSubmit(ActionEvent event) throws Exception {
        if (!CommonUtils.checkConfig(log, FunctionConfig.PROCESS_INFO.getCode())) {
            return;
        }
        setProgress(0);
        if (StringUtils.isBlank(filePath.getText())) {
            OutputUtils.info(log, "请选择流程信息Excel文件");
            return;
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
        try {
            progress = value;
            Platform.runLater(() -> {
                schedule.setProgress(progress);
            });
            schedule.requestFocus();
        } catch (Exception e) {
            LoggerUtils.info(e.toString());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            OutputUtils.clearLog(filePath);
            OutputUtils.clearLog(taCode);
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (StringUtils.isNotBlank(appConfigDto.getProcessExcelPath())) {
                OutputUtils.info(filePath, appConfigDto.getProcessExcelPath());
            }
            if (StringUtils.isNotBlank(appConfigDto.getProcessTaCode())) {
                OutputUtils.info(taCode, appConfigDto.getProcessTaCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateScript() {
        new Thread(() -> {
            try {
                submit.setDisable(true);
                Date date = new Date();
                OutputUtils.clearLog(log);
                // 创建生成脚本目录
                AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                String processSchedule = appConfigDto.getProcessGeneratePathSchedule();
                String processPathTrans = appConfigDto.getProcessGeneratePathTrans();
                File pathFolderSchedule = new File(processSchedule);
                if (!pathFolderSchedule.exists()) {
                    pathFolderSchedule.mkdirs();
                }
                File pathFolderTrans = new File(processPathTrans);
                if (!pathFolderTrans.exists()) {
                    pathFolderTrans.mkdirs();
                }

                // 打开workbook
                Workbook workbook = Workbook.getWorkbook(new File(filePath.getText()));

                // step 2.1 加载任务配置 到缓存
                Sheet[] sheetList = workbook.getSheets();
                Sheet taskSheet = workbook.getSheet("任务配置");
                if (taskSheet == null) {
                    OutputUtils.info(log, "[任务配置]sheet页面不存在");
                    return;
                } else {
                    OutputUtils.info(log, "任务缓存加载开始");
                    TaskMemoryCache.initCache(taskSheet);
                    TaskMemoryCache.cleaExistMap();
                    OutputUtils.info(log, "任务缓存加载结束");
                }
                // step 2.2 生成tbtrans数据
                String transFileName = "02tbtrans-fund.sql";
                String transPath = processPathTrans + "/" + transFileName;
                File transFile = new File(transPath);
                BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(transFile), ENCODING_UTF8));
                Sheet tranSheet = workbook.getSheet("交易配置");
                TransMemoryCache.initCache(tranSheet);
                writeTransInfo(bf);
                bf.close();

                // step 3 创建 输出文件
                String fileName = "tbschedule-fund_000000.sql";
                String schedulePath = processSchedule + "/" + fileName;
                File file = new File(schedulePath);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), ENCODING_UTF8));

                // step 3 在输出文件内 写 组信息 和 页 信息
                Sheet flowSheet = workbook.getSheet("基本流程信息");
                writeFlowInfo(taCode.getText(), flowSheet, bufferedWriter);

                if (flowSheet == null) {
                    OutputUtils.info(log, "[基本流程信息]sheet页面不存在");
                    return;
                }

                Map<String, String> jobWithTaskMap;
                bufferedWriter.write("delete from tbscheduletask WHERE substr(sche_task_code, 1, 5) = 'fund_' AND ta_code='" + taCode.getText() + "';\n");
                for (Sheet sheet : sheetList) {
                    if (sheet.getName().compareTo("首页") == 0 || sheet.getName().compareTo("基本流程信息") == 0 || sheet.getName().compareTo("任务配置") == 0 || sheet.getName().compareTo("交易配置") == 0) {
                        // 跳过其他页
                        continue;
                    }
                    // step 3 读group流程，绑定task
                    String[] sheetNames = sheet.getName().split("&");
                    // step 3.1 命名检查
                    String groupName = STR_SPACE;
                    String groupCode = STR_SPACE;
                    if (sheet.getName().compareTo("自由节点") != 0) {
                        if (sheetNames.length != 2) {
                            OutputUtils.info(log, sheet.getName() + "命名不规范,正常的格式为中文名&英文名,请检查");
                        } else {
                            groupCode = sheetNames[1];
                        }
                    }
                    groupName = sheetNames[0];
                    OutputUtils.info(log, "开始生成[" + groupName + "]");
                    // step 3.2 开始写流程文件
                    jobWithTaskMap = writeJobFile(groupCode, groupName, taCode.getText(), sheet, bufferedWriter);
                    OutputUtils.info(log, "tbscheduletask生成开始");
                    // 通过 jobTaskMap 和  Cache  生成 sql
                    writeTaskInfo(taCode.getText(), bufferedWriter, jobWithTaskMap);
                    OutputUtils.info(log, "tbscheduletask生成结束");
                }

                // step 3.3 开始写流程文件 tbscheduletaskregistry
                OutputUtils.info(log, "tbscheduletaskregistry生成开始");
                writeTaskRegistry(bufferedWriter);
                OutputUtils.info(log, "tbscheduletaskregistry生成结束");

                bufferedWriter.write(STR_EMPTY);
                bufferedWriter.close();
                schedule.setProgress(1);
                List<String> path = new ArrayList<>();
                path.add(transPath);
                path.add(schedulePath);
                LoggerUtils.writeProcessInfo(date, path);
            } catch (Exception e) {
                e.printStackTrace();
                OutputUtils.info(log, e.toString());
            } finally {
                submit.setDisable(false);
            }
            setProgress(1);
        }).start();
    }

    /**
     * 写 tbtrans表
     *
     * @param bf
     */
    private void writeTransInfo(BufferedWriter bf) throws IOException {
        OutputUtils.info(log, "交易配置信息生成开始");
        bf.write("-- ***************************************************\n");
        bf.write("-- TA6.0 交易配置\n");
        bf.write("-- 版权所述：TA研发二部\n");
        bf.write("-- ***************************************************\n");
        Map<String, Map<String, Object>> list = TransMemoryCache.getCacheMap();
        for (Map.Entry entry : list.entrySet()) {
            String transCode = (String) entry.getKey();
            Map<String, Object> row = (Map<String, Object>) entry.getValue();
            bf.write("delete from tbtrans where trans_code = '" + transCode + "';\n");
            bf.write("insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) " +
                    "\nvalues ('" + transCode + "'," +
                    "'" + row.get(TransMemoryCache.TRANS_NAME) + "'," +
                    "'" + row.get(TransMemoryCache.ENABLE_FLAG) + "'," +
                    "'" + row.get(TransMemoryCache.CHANNELS) + "'," +
                    "'" + row.get(TransMemoryCache.HOST_ONLINE) + "'," +
                    "'" + row.get(TransMemoryCache.TRANS_TYPE) + "'," +
                    "'" + row.get(TransMemoryCache.MONITOR_FLAG) + "'," +
                    "'" + row.get(TransMemoryCache.LOG_LEVEL) + "'," +
                    "'" + row.get(TransMemoryCache.CANCEL_FLAG) + "'," +
                    "'" + row.get(TransMemoryCache.ERASE_FLAG) + "'," +
                    "'" + (row.get(TransMemoryCache.MON_TRANS_TYPE).equals("") ? " " : row.get(TransMemoryCache.MON_TRANS_TYPE)) + "'," +
                    "'" + (row.get(TransMemoryCache.RESERVE1).equals("") ? " " : row.get(TransMemoryCache.RESERVE1)) + "'," +
                    "'" + (row.get(TransMemoryCache.RESERVE2).equals("") ? " " : row.get(TransMemoryCache.RESERVE2)) + "'," +
                    "'" + (row.get(TransMemoryCache.RESERVE3).equals("") ? " " : row.get(TransMemoryCache.RESERVE3)) + "');\n");
        }
        bf.write("commit;");
        OutputUtils.info(log, "交易配置信息生成结束");
    }

    /**
     * 写流程基础数据
     *
     * @param taCode
     * @param sheet
     * @param bufferedWriter
     * @throws IOException
     */
    private void writeFlowInfo(String taCode, Sheet sheet, BufferedWriter bufferedWriter) throws IOException {
        OutputUtils.info(log, "流程基础信息开始");
        bufferedWriter.write("-- ***************************************************\n");
        bufferedWriter.write("-- TA6.0流程\n");
        bufferedWriter.write("-- 版权所述：TA研发二部\n");
        bufferedWriter.write("-- ***************************************************\n");
        boolean pageFlag = true;
        int rows = sheet.getRows();
        for (int k = 2; k < rows; k++) {
            if (sheet.getCell(0, k).getContents().equals("") && pageFlag) {
                k += 3;
                pageFlag = false;
            }
            if (pageFlag) {
                bufferedWriter.write("-- 流程页配置脚本 begin\n");
                bufferedWriter.write("delete from tbschedulepage WHERE sche_page_code='" + sheet.getCell(0, k).getContents() + "';\n");
                bufferedWriter.write("insert into tbschedulepage (sche_page_code,sche_page_name,sche_page_isuse,sche_page_belong) \nvalues ('" + sheet.getCell(0, k).getContents() +
                        "' , '" + sheet.getCell(1, k).getContents() + "' , '" + sheet.getCell(2, k).getContents() + "' , '" + sheet.getCell(3, k).getContents() + "');\n");
                bufferedWriter.write("-- 流程页配置脚本 end\n\n");

            } else {
                bufferedWriter.write("-- 流程组配置脚本 begin\n");
                bufferedWriter.write("delete from tbschedulegroup where sche_group_code='" + sheet.getCell(1, k).getContents() + "';\n");
                bufferedWriter.write("insert into tbschedulegroup (sche_page_code,sche_group_code,sche_group_name,sche_group_isuse,sche_group_type) \nvalues ('" + sheet.getCell(0, k).getContents() +
                        "' , '" + sheet.getCell(1, k).getContents() + "' , '" + sheet.getCell(2, k).getContents() + "' , '" + sheet.getCell(3, k).getContents() + "' , '" + sheet.getCell(4, k).getContents() + "');\n");
                bufferedWriter.write("-- 流程组配置脚本 end\n\n");
            }
        }
        OutputUtils.info(log, "流程基础信息生成结束");
    }

    /**
     * 生成task脚本
     *
     * @param groupCode
     * @param taCode
     * @param sheet
     * @param bufferedWriter
     */
    private Map<String, String> writeJobFile(String groupCode, String groupName, String taCode, Sheet sheet, BufferedWriter bufferedWriter) throws IOException {
        List<String> parentJobList = new ArrayList<>();
        List<String> secondJobList = new ArrayList<>();

        Map<String, String> jobTaskMap = new HashMap<>();
        int rows = sheet.getRows();

        bufferedWriter.write("-- 一级job配置脚本 begin " + groupName + "\n");
        if (groupCode.equals("fund_free_process")) {
            groupCode = " ";
            // 自由节点特殊处理，通过job删除
            bufferedWriter.write("delete from tbschedulejob where ta_code='" + taCode + "' and sche_group_code =' '  and sche_job_code like 'fund_free_job%';\n");
        } else {
            bufferedWriter.write("delete from tbschedulejob where ta_code='" + taCode + "' and sche_group_code like '" + groupCode.substring(0, 13) + "%';\n");
        }
        for (int k = 2; k < rows; k++) {

            // 一级job 不为空，二级job为空
            if (sheet.getCell(3, k).getContents().equals("1")) {
                String sql = "insert into tbschedulejob (sche_group_code,sche_job_code,sche_job_name," +
                        "sche_parent_job_code,sche_job_isuse,sche_up_job_code,bank_no,ta_code,sche_job_url," +
                        "sche_job_pause,sche_ishidebutton,url_open_mode, sche_job_ishide, sche_job_isskip) \nvalues ('"
                        + groupCode + "',"
                        + getCell(sheet, 2, k) + ","
                        + getCell(sheet, 0, k) + ","
                        + getCell(sheet, 1, k) + ","
                        + getCell(sheet, 6, k) + ","
                        + "null,"
                        + getCell(sheet, 7, k) + ",'"
                        + taCode + "',"
                        + getCell(sheet, 9, k) + ","
                        + getCell(sheet, 10, k) + ", "
                        + ((getCell(sheet, 11, k).equals("null")) ? "'0'" : "'1'") + ", "
                        + ((getCell(sheet, 9, k).equals("null")) ? "'0'" : "'1'") + ", "
                        + ((getCell(sheet, 12, k).equals("null")) ? "'0'" : "'1'") + ", "
                        + ((getCell(sheet, 13, k).equals("null")) ? "'0'" : "'1'")
                        + ");";
                parentJobList.add(sql);
            }

            // 存在子task服务 生成task 脚本
            if (!sheet.getCell(5, k).getContents().equals("")) {
                if (sheet.getCell(3, k).getContents().equals("1")) {
                    // 主job 携带 task
                    jobTaskMap.put(sheet.getCell(2, k).getContents(), sheet.getCell(5, k).getContents());
                } else {
                    // 二级job 携带 task
                    jobTaskMap.put(sheet.getCell(2, k).getContents(), sheet.getCell(5, k).getContents());
                }
            }
        }
        for (String sql : parentJobList) {
            bufferedWriter.write(sql + "\n");
        }
        bufferedWriter.write("-- 一级job配置脚本 end  " + groupName + "\n\n");

        bufferedWriter.write("-- 二级job配置脚本 begin  " + groupName + "\n");
        for (int k = 2; k < rows; k++) {
            // 二级JOB
            if (!sheet.getCell(3, k).getContents().equals("1")) {
                String sql = "insert into tbschedulejob (sche_group_code,sche_job_code,sche_job_name," +
                        "sche_parent_job_code,sche_job_isuse,sche_up_job_code,bank_no,ta_code,sche_job_url," +
                        "sche_job_pause,sche_ishidebutton,url_open_mode, sche_job_ishide, sche_job_isskip) " +
                        "\nvalues ('"
                        + groupCode + "',"
                        + getCell(sheet, 2, k) + ","
                        + getCell(sheet, 0, k) + ","
                        + getCell(sheet, 1, k) + ","
                        + getCell(sheet, 6, k) + ","
                        + getCell(sheet, 4, k) + ","
                        + getCell(sheet, 7, k) + ",'"
                        + taCode + "',"
                        + getCell(sheet, 9, k) + ","
                        + getCell(sheet, 10, k) + ","
                        + ((getCell(sheet, 11, k).equals("null")) ? "'0'" : "'1'") + ","
                        + ((getCell(sheet, 9, k).equals("null")) ? "'0'" : "'1'") + ","
                        + ((getCell(sheet, 12, k).equals("null")) ? "'0'" : "'1'") + ","
                        + ((getCell(sheet, 13, k).equals("null")) ? "'0'" : "'1'")
                        + ");";
                secondJobList.add(sql);
            }
        }
        for (String sql : secondJobList) {
            bufferedWriter.write(sql + "\n");
        }
        bufferedWriter.write("-- 二级job配置脚本 end  " + groupName + "\n\n");
        return jobTaskMap;
    }

    /**
     * 获取表格数据
     *
     * @param sheet
     * @param i
     * @param j
     * @return
     */
    public String getCell(Sheet sheet, int i, int j) {
        if (sheet.getCell(i, j).getContents().equals("")) {
            return "null";
        } else {
            return "'" + sheet.getCell(i, j).getContents() + "'";
        }
    }

    /**
     * 传入组名和ta代码生成脚本
     *
     * @param taCode         ta代码
     * @param bufferedWriter 写buffer
     * @param jobWithTaskMap 携带task 的job列表
     * @throws IOException
     */
    private void writeTaskInfo(String taCode, BufferedWriter bufferedWriter, Map<String, String> jobWithTaskMap) throws IOException {
        bufferedWriter.write("-- SCHEDULETASK 配置 begin\n");
        for (Map.Entry entry : jobWithTaskMap.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            // 将value 拆分为列表
            String[] taskList = value.split("\n");
            String parentTaskCode = "";
            for (String taskCode : taskList) {
                Map map = TaskMemoryCache.getCacheMap(taskCode);
                if (map == null) {
                    OutputUtils.info(log, key + "的TASK[" + taskCode + "]在【任务配置】sheet页中不存在");
                    return;
                }
                // 写主task
                String taskSql = "insert into tbscheduletask (sche_job_code, sche_task_code, sche_task_name, " +
                        "sche_parent_task_code, sche_task_redo, sche_task_timeout, sche_task_retrycount," +
                        " sche_task_isuse, sche_task_ishide, sche_task_memo, sche_task_dependencies, function_id, " +
                        "bank_no, ta_code, sche_task_isskip, sche_task_skipreason, sche_task_delaytime, sche_task_pause," +
                        " parent_function_id,sche_reserve)\n" +
                        "values('"
                        + key + "' , '"
                        + taskCode + "' , '"
                        + map.get(TaskMemoryCache.SCHE_TASK_NAME) + "' , '"
                        + parentTaskCode + "' , '"
                        + map.get(TaskMemoryCache.SCHE_TASK_REDO) + "' , '"
                        + map.get(TaskMemoryCache.SCHE_TASK_TIMEOUT) + "' , "
                        + (map.get(TaskMemoryCache.SCHE_TASK_RETRYCOUNT).equals(" ") ? " " : "null") + " , '"
                        + map.get(TaskMemoryCache.SCHE_TASK_ISUSE) + "' , '"
                        + map.get(TaskMemoryCache.SCHE_TASK_ISHIDE) + "' , '"
                        + map.get(TaskMemoryCache.SCHE_TASK_MEMO) + "' , '"
                        + map.get(TaskMemoryCache.SCHE_TASK_DEPENDENCIES) + "' , '"
                        + map.get(TaskMemoryCache.FUNCTION_ID) + "' , '"
                        + map.get(TaskMemoryCache.BANK_NO) + "' , '"
                        + taCode + "' , '"
                        + map.get(TaskMemoryCache.SCHE_TASK_ISSKIP) + "' , '"
                        + map.get(TaskMemoryCache.SCHE_TASK_SKIPREASON) + "' , "
                        + (map.get(TaskMemoryCache.SCHE_TASK_DELAYTIME).equals(" ") ? " " : "null") + " , '"
                        + map.get(TaskMemoryCache.SCHE_TASK_PAUSE) + "', null , '"
                        + (map.get(TaskMemoryCache.SCHE_TASK_RESERVE).equals("") ? " " : map.get(TaskMemoryCache.SCHE_TASK_RESERVE)) + "' );";

                String functionId = (String) map.get(TaskMemoryCache.FUNCTION_ID);
                String reserve = (String) map.get(TaskMemoryCache.SCHE_TASK_RESERVE);
                bufferedWriter.write(taskSql + "\n");
                List<Map<String, Object>> subTaskList = TaskMemoryCache.getCacheMapByFunction(functionId, reserve);
                String parentSubTaskCode = "";
                for (Map<String, Object> subMap : subTaskList) {
                    if (subMap == null) {
                        // 无子task
                        continue;
                    } else {
                        // 写分task
                        String subTaskSql = "insert into tbscheduletask (sche_job_code, sche_task_code, sche_task_name, " +
                                "sche_parent_task_code, sche_task_redo, sche_task_timeout, sche_task_retrycount," +
                                " sche_task_isuse, sche_task_ishide, sche_task_memo, sche_task_dependencies, function_id, " +
                                "bank_no, ta_code, sche_task_isskip, sche_task_skipreason, sche_task_delaytime, sche_task_pause," +
                                " parent_function_id,sche_reserve)\n" +
                                "values( null ,'"
                                + subMap.get(TaskMemoryCache.SCHE_TASK_CODE) + "' , '"
                                + subMap.get(TaskMemoryCache.SCHE_TASK_NAME) + "' , '"
                                + parentSubTaskCode + "' , '"
                                + subMap.get(TaskMemoryCache.SCHE_TASK_REDO) + "' , '"
                                + subMap.get(TaskMemoryCache.SCHE_TASK_TIMEOUT) + "' , "
                                + (map.get(TaskMemoryCache.SCHE_TASK_RETRYCOUNT).equals(" ") ? " " : "null") + " , '"
                                + subMap.get(TaskMemoryCache.SCHE_TASK_ISUSE) + "' , '"
                                + subMap.get(TaskMemoryCache.SCHE_TASK_ISHIDE) + "' , '"
                                + subMap.get(TaskMemoryCache.SCHE_TASK_MEMO) + "' , '"
                                + subMap.get(TaskMemoryCache.SCHE_TASK_DEPENDENCIES) + "' , '"
                                + subMap.get(TaskMemoryCache.FUNCTION_ID) + "' , '"
                                + subMap.get(TaskMemoryCache.BANK_NO) + "' , '"
                                + taCode + "' , '"
                                + subMap.get(TaskMemoryCache.SCHE_TASK_ISSKIP) + "' , '"
                                + subMap.get(TaskMemoryCache.SCHE_TASK_SKIPREASON) + "' ,  "
                                + (map.get(TaskMemoryCache.SCHE_TASK_DELAYTIME).equals(" ") ? " " : "null") + " , '"
                                + subMap.get(TaskMemoryCache.SCHE_TASK_PAUSE) + "' , '" + functionId + "', '"
                                + (map.get(TaskMemoryCache.SCHE_TASK_RESERVE).equals("") ? " " : map.get(TaskMemoryCache.SCHE_TASK_RESERVE)) + "' );";
                        // 写 子task SCHE_JOB_CODE 字段填写空
                        bufferedWriter.write(subTaskSql + "\n");
                        // TaskMemoryCache.setExistMap(functionId);

                    }
                    // parentSubTaskCode = (String) subMap.get(TaskMemoryCache.SCHE_TASK_CODE);
                }

                parentTaskCode = taskCode;

            }

        }
        bufferedWriter.write("-- SCHEDULETASK 配置 end\n ");
    }

    /**
     * 传入组名和ta代码生成脚本
     *
     * @param bufferedWriter 写buffer
     * @throws IOException
     */
    private void writeTaskRegistry(BufferedWriter bufferedWriter) throws IOException {
        bufferedWriter.write("\n-- tbscheduletaskregistry 配置 begin\n");
        String sql = "-- 删除fund的 registry\n" +
                "delete from tbscheduletaskregistry where substr(sche_task_code, 1, 4) = 'fund';\n" +
                "/* 主库交易广播配置 */\n" +
                "insert into tbscheduletaskregistry (sche_task_code, app_name, app_group, app_version, \n" +
                "       app_url, sche_app_isuse)\n" +
                "select a.sche_task_code, 'ta-fund-parameter-batch' app_name, 'group' app_group, '1.0' app_version, \n" +
                "       '/fund/pub/batch' app_url, '1' sche_app_isuse\n" +
                "  from tbscheduletask a \n" +
                " where substr(a.sche_task_code, 1, 4) = 'fund' \n" +
                "   and substr(a.function_id, 1, 4) = 'PUB9'\n" +
                "   and a.ta_code = '000000';\n" +
                "\n" +
                "/* 交易库交易广播配置 多个分库的情况下，修改针对不同的app_name多次执行 */\n" +
                "insert into tbscheduletaskregistry (sche_task_code, app_name, app_group, app_version, \n" +
                "       app_url, sche_app_isuse)\n" +
                "select a.sche_task_code, concat('ta-fund-trans-batch', b.area_num) app_name, 'group' app_group, '1.0' app_version, \n" +
                "       '/ta/fund/trans/batch' app_url, '1' sche_app_isuse\n" +
                "  from tbscheduletask a, (\n" +
                "\tselect 1 as area_num from tbareainfo where busin_type = '5' and 1 <= tot_area_num\n" +
                "\tunion all\n" +
                "\tselect 2 as area_num from tbareainfo where busin_type = '5' and 2 <= tot_area_num\n" +
                "\tunion all\n" +
                "\tselect 3 as area_num from tbareainfo where busin_type = '5' and 3 <= tot_area_num\n" +
                "\tunion all\n" +
                "\tselect 4 as area_num from tbareainfo where busin_type = '5' and 4 <= tot_area_num\n" +
                "\tunion all\n" +
                "\tselect 5 as area_num from tbareainfo where busin_type = '5' and 5 <= tot_area_num\n" +
                "\tunion all\n" +
                "\tselect 6 as area_num from tbareainfo where busin_type = '5' and 6 <= tot_area_num\n" +
                "\tunion all\n" +
                "\tselect 7 as area_num from tbareainfo where busin_type = '5' and 7 <= tot_area_num\n" +
                "\tunion all\n" +
                "\tselect 8 as area_num from tbareainfo where busin_type = '5' and 8 <= tot_area_num\n" +
                "\tunion all\n" +
                "\tselect 9 as area_num from tbareainfo where busin_type = '5' and 9 <= tot_area_num\n" +
                "\tunion all\n" +
                "\tselect 10 as area_num from tbareainfo where busin_type = '5' and 10 <= tot_area_num\n" +
                " ) b  \n" +
                " where substr(a.sche_task_code, 1, 4) = 'fund' \n" +
                "   and substr(a.function_id, 1, 4) = 'FUND'\n" +
                "   and a.ta_code = '000000';\n" +
                "\n" +
                "/* 账户库交易广播配置 多个分库的情况下，修改针对不同的app_name多次执行 */\n" +
                "insert into tbscheduletaskregistry (sche_task_code, app_name, app_group, app_version, \n" +
                "       app_url, sche_app_isuse)\n" +
                "select a.sche_task_code, concat('ta-fund-account-batch', b.area_num) app_name, 'group' app_group, '1" +
                ".0' app_version, \n" +
                "       '/ta/acc/fund/batch' app_url, '1' sche_app_isuse\n" +
                "  from tbscheduletask a, (\n" +
                "\tselect 1 as area_num from tbareainfo where busin_type = '9' and 1 <= tot_area_num\n" +
                "\tunion all\n" +
                "\tselect 2 as area_num from tbareainfo where busin_type = '9' and 2 <= tot_area_num\n" +
                "\tunion all\n" +
                "\tselect 3 as area_num from tbareainfo where busin_type = '9' and 3 <= tot_area_num\n" +
                "\tunion all\n" +
                "\tselect 4 as area_num from tbareainfo where busin_type = '9' and 4 <= tot_area_num\n" +
                "\tunion all\n" +
                "\tselect 5 as area_num from tbareainfo where busin_type = '9' and 5 <= tot_area_num\n" +
                "\tunion all\n" +
                "\tselect 6 as area_num from tbareainfo where busin_type = '9' and 6 <= tot_area_num\n" +
                "\tunion all\n" +
                "\tselect 7 as area_num from tbareainfo where busin_type = '9' and 7 <= tot_area_num\n" +
                "\tunion all\n" +
                "\tselect 8 as area_num from tbareainfo where busin_type = '9' and 8 <= tot_area_num\n" +
                "\tunion all\n" +
                "\tselect 9 as area_num from tbareainfo where busin_type = '9' and 9 <= tot_area_num\n" +
                "\tunion all\n" +
                "\tselect 10 as area_num from tbareainfo where busin_type = '9' and 10 <= tot_area_num\n" +
                " ) b \n" +
                " where substr(a.sche_task_code, 1, 4) = 'fund' \n" +
                "   and substr(a.function_id, 1, 4) = 'ACC9'\n" +
                "   and a.ta_code = '000000';\n";
        bufferedWriter.write(sql);
        bufferedWriter.write("-- tbscheduletaskregistry 配置 end\n ");
        bufferedWriter.write("\ncommit;\n");
    }
}
