package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.cache.TaskMemoryCache;
import com.hoomoomoo.im.cache.TransMemoryCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import com.hoomoomoo.im.utils.TaCommonUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.util.*;

import static com.hoomoomoo.im.cache.TaskMemoryCache.*;
import static com.hoomoomoo.im.consts.BaseConst.STR_NEXT_LINE;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.PROCESS_INFO;


/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/05/07
 */
public class ProcessInfoController extends BaseController implements Initializable {

    @FXML
    private AnchorPane processInfo;

    @FXML
    private Button submit;

    @FXML
    private TextField filePath;

    @FXML
    private String taCode = "000000";

    @FXML
    private TextArea log;

    private Map<String, String> jobToGroup = new HashMap<>(16);

    private Map<String, String> taskToJob = new HashMap<>(16);


    @FXML
    void executeSelect(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("全部Excel文件", "*.xls"));
        File file = fileChooser.showOpenDialog(processInfo.getScene().getWindow());
        if (file != null) {
            OutputUtils.clearLog(filePath);
            OutputUtils.info(filePath, file.getAbsolutePath());
        }
    }

    @FXML
    void executeSubmit(ActionEvent event) {
        try {
            LoggerUtils.info(String.format(BaseConst.MSG_USE, PROCESS_INFO.getName()));
            if (StringUtils.isBlank(filePath.getText())) {
                OutputUtils.info(log, "请选择流程信息Excel文件" + STR_NEXT_LINE);
                return;
            }
            int index = filePath.getText().lastIndexOf("/");
            if (index == -1) {
                index = filePath.getText().lastIndexOf("\\");
            }
            String path = filePath.getText().substring(0, index);
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            if (StringUtils.isEmpty(appConfigDto.getProcessGeneratePathSchedule())) {
                appConfigDto.setProcessGeneratePathSchedule(path);
            }
            if (StringUtils.isEmpty(appConfigDto.getProcessGeneratePathTrans())) {
                appConfigDto.setProcessGeneratePathTrans(path);
            }

            if (!TaCommonUtils.checkConfig(log, PROCESS_INFO.getCode())) {
                return;
            }
            setProgress(0);
            updateProgress();
            generateScript();
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage() + STR_NEXT_LINE);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            OutputUtils.clearLog(filePath);
            OutputUtils.clearLog(taCode);
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            if (StringUtils.isNotBlank(appConfigDto.getProcessExcelPath())) {
                OutputUtils.info(filePath, appConfigDto.getProcessExcelPath());
            }
            if (StringUtils.isNotBlank(appConfigDto.getProcessTaCode())) {
                OutputUtils.info(taCode, appConfigDto.getProcessTaCode());
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage() + STR_NEXT_LINE);
        }
    }

    public void generateScript() {
        new Thread(() -> {
            try {
                jobToGroup.clear();
                taskToJob.clear();
                submit.setDisable(true);
                Date date = new Date();
                OutputUtils.clearLog(log);
                // 创建生成脚本目录
                AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
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
                    OutputUtils.info(log, "[任务配置]sheet页面不存在" + STR_NEXT_LINE);
                    return;
                } else {
                    OutputUtils.info(log, "任务缓存加载开始" + STR_NEXT_LINE);
                    TaskMemoryCache.initCache(taskSheet);
                    TaskMemoryCache.cleaExistMap();
                    OutputUtils.info(log, "任务缓存加载结束" + STR_NEXT_LINE);
                }
                // 校验task配置合理性
                Map<String, String> check = new HashMap<>(16);
                Map<String, Map<String, Object>> taskMap = TaskMemoryCache.getCacheMap();
                Iterator<String> iterator = taskMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String taskCode = iterator.next();
                    Map<String, Object> task = taskMap.get(taskCode);
                    String checkKey = task.get(FUNCTION_ID) + "-" + task.get(PARENT_FUNCTION_ID) + "-" +  task.get(SCHE_TASK_RESERVE);
                    if (check.containsKey(checkKey)) {
                        String msg = String.format("任务配置中【%s】和【%s】的【功能号】【父功能号】【扩展参数】相同，请调整", check.get(checkKey),taskCode);
                        OutputUtils.info(log, msg + STR_NEXT_LINE);
                        LoggerUtils.info(msg);
                        throw new Exception(msg);
                    } else {
                        check.put(checkKey, taskCode);
                    }
                }

                // step 2.2 生成tbtrans数据
                String transFileName = "tbtrans-fund.sql";
                String transPath = processPathTrans + "/" + transFileName;
                File transFile = new File(transPath);
                BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(transFile), BaseConst.ENCODING_UTF8));
                Sheet tranSheet = workbook.getSheet("交易配置");
                TransMemoryCache.initCache(tranSheet);
                writeTransInfo(bf);
                bf.close();

                // step 3 创建 输出文件
                String fileName = "tbschedule-fund_000000.sql";
                String schedulePath = processSchedule + "/" + fileName;
                File file = new File(schedulePath);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), BaseConst.ENCODING_UTF8));

                // step 3 在输出文件内 写 组信息 和 页 信息
                Sheet flowSheet = workbook.getSheet("基本流程信息");
                if (flowSheet == null) {
                    OutputUtils.info(log, "[基本流程信息]sheet页面不存在" + STR_NEXT_LINE);
                    return;
                }
                writeFlowInfo(taCode, flowSheet, bufferedWriter);

                Map<String, String> jobWithTaskMap;
                bufferedWriter.write("delete from tbscheduletask WHERE substr(sche_task_code, 1, 5) = 'fund_' AND ta_code='" + taCode + "';\n");
                for (Sheet sheet : sheetList) {
                    if (sheet.getName().compareTo("首页") == 0 || sheet.getName().compareTo("基本流程信息") == 0 || sheet.getName().compareTo("任务配置") == 0
                            || sheet.getName().compareTo("交易配置") == 0 || sheet.getName().compareTo("定时任务配置") == 0) {
                        // 跳过其他页
                        continue;
                    }
                    // step 3 读group流程，绑定task
                    String[] sheetNames = sheet.getName().split("&");
                    // step 3.1 命名检查
                    String groupName = BaseConst.STR_SPACE;
                    String groupCode = BaseConst.STR_SPACE;
                    if (sheet.getName().compareTo("自由节点") != 0) {
                        if (sheetNames.length != 2) {
                            OutputUtils.info(log, sheet.getName() + "命名不规范,正常的格式为中文名&英文名,请检查" + STR_NEXT_LINE);
                        } else {
                            groupCode = sheetNames[1];
                        }
                    }
                    groupName = sheetNames[0];
                    OutputUtils.info(log, "开始生成[" + groupName + "]" + STR_NEXT_LINE);
                    // step 3.2 开始写流程文件
                    jobWithTaskMap = writeJobFile(groupCode, groupName, taCode, sheet, bufferedWriter);
                    OutputUtils.info(log, "tbscheduletask生成开始" + STR_NEXT_LINE);
                    // 通过 jobTaskMap 和  Cache  生成 sql
                    writeTaskInfo(taCode, bufferedWriter, jobWithTaskMap);
                    OutputUtils.info(log, "tbscheduletask生成结束" + STR_NEXT_LINE);
                }

                // step 3.2.5 开始写流程文件 tbscheduletrigger
                OutputUtils.info(log, "tbscheduletrigger生成开始" + STR_NEXT_LINE);
                Sheet triggerSheet = workbook.getSheet("定时任务配置");
                if (triggerSheet == null) {
                    OutputUtils.info(log, "[定时任务配置]sheet页面不存在" + STR_NEXT_LINE);
                    return;
                }
                writeTriggerInfo(taCode, triggerSheet, bufferedWriter);
                OutputUtils.info(log, "tbscheduletrigger生成结束" + STR_NEXT_LINE);

                // step 3.3 开始写流程文件 tbscheduletaskregistry
                OutputUtils.info(log, "tbscheduletaskregistry生成开始" + STR_NEXT_LINE);
                writeTaskRegistry(bufferedWriter);
                OutputUtils.info(log, "tbscheduletaskregistry生成结束" + STR_NEXT_LINE);

                bufferedWriter.write(BaseConst.STR_BLANK);
                bufferedWriter.close();
                schedule.setProgress(1);
                List<String> path = new ArrayList<>();
                path.add(transPath);
                path.add(schedulePath);
                LoggerUtils.writeProcessInfo(date, path);
                OutputUtils.info(log, "执行完成" + STR_NEXT_LINE);
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(log, e.getMessage() + STR_NEXT_LINE);
            } finally {
                setProgress(1);
                submit.setDisable(false);
            }
        }).start();
    }

    /**
     * 写 tbtrans表
     *
     * @param bf
     */
    private void writeTransInfo(BufferedWriter bf) throws IOException {
        OutputUtils.info(log, "交易配置信息生成开始" + STR_NEXT_LINE);
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
        OutputUtils.info(log, "交易配置信息生成结束" + STR_NEXT_LINE);
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
        OutputUtils.info(log, "流程基础信息开始" + STR_NEXT_LINE);
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
                if (!StringUtils.equals("fund_daily_opera_process", sheet.getCell(1, k).getContents())) {
                    bufferedWriter.write("-- ");
                }
                bufferedWriter.write("delete from tbschedulegroup where sche_group_code='" + sheet.getCell(1, k).getContents() + "';\n");
                if (!StringUtils.equals("fund_daily_opera_process", sheet.getCell(1, k).getContents())) {
                    bufferedWriter.write("-- ");
                }
                bufferedWriter.write("insert into tbschedulegroup (sche_page_code,sche_group_code,sche_group_name,sche_group_isuse,sche_group_type) \n");
                if (!StringUtils.equals("fund_daily_opera_process", sheet.getCell(1, k).getContents())) {
                    bufferedWriter.write("-- ");
                }
                bufferedWriter.write("values ('" + sheet.getCell(0, k).getContents() + "' , '" + sheet.getCell(1, k).getContents() + "' , '" + sheet.getCell(2, k).getContents() + "' , '" + sheet.getCell(3, k).getContents() + "' , '" + sheet.getCell(4, k).getContents() + "');\n");
                bufferedWriter.write("-- 流程组配置脚本 end\n\n");
            }
        }
        OutputUtils.info(log, "流程基础信息生成结束" + STR_NEXT_LINE);
    }

    /**
     * 生成task脚本
     *
     * @param groupCode
     * @param taCode
     * @param sheet
     * @param bufferedWriter
     */
    private Map<String, String> writeJobFile(String groupCode, String groupName, String taCode, Sheet sheet, BufferedWriter bufferedWriter) throws Exception {
        List<String> parentJobList = new ArrayList<>();
        List<String> secondJobList = new ArrayList<>();

        Map<String, String> jobTaskMap = new HashMap<>();
        int rows = sheet.getRows();

        bufferedWriter.write("-- 一级job配置脚本 begin " + groupName + "\n");
        boolean freeProcess = false;
        if (groupCode.equals("fund_free_process")) {
            freeProcess = true;
            groupCode = " ";
            // 自由节点特殊处理，通过job删除
            bufferedWriter.write("delete from tbschedulejob where ta_code='" + taCode + "' and sche_group_code =' '  and sche_job_code like 'fund_free_job%';\n");
        } else {
            bufferedWriter.write("delete from tbschedulejob where ta_code='" + taCode + "' and sche_group_code = '" + groupCode + "';\n");
        }
        for (int k = 2; k < rows; k++) {
            if (StringUtils.isBlank(getCell(sheet, 2, k)) || "null".equals(getCell(sheet, 2, k))) {
                continue;
            }
            // 一级job 不为空，二级job为空
            if (sheet.getCell(3, k).getContents().equals("1")) {
                String sql = "insert into tbschedulejob (sche_group_code,sche_job_code,sche_job_name," +
                        "sche_parent_job_code,sche_job_isuse,sche_up_job_code,bank_no,ta_code,sche_job_url," +
                        "sche_job_pause,sche_ishidebutton,url_open_mode, sche_job_ishide, sche_job_isskip," +
                        "sche_job_iswait, order_no, sche_depend_undonodes) \nvalues" +
                        " ('"
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
                        + ((getCell(sheet, 11, k).equals("null")) ? "'0'" : getCell(sheet, 11, k)) + ", "
                        + ((getCell(sheet, 15, k).equals("null")) ? getCell(sheet, 9, k).equals("null") ? "'0'" : "'1'" : getCell(sheet, 15, k)) + ","
                        + ((getCell(sheet, 12, k).equals("null")) ? "'0'" : getCell(sheet, 12, k)) + ", "
                        + ((getCell(sheet, 13, k).equals("null")) ? "'0'" : getCell(sheet, 13, k)) + ", "
                        + ((getCell(sheet, 14, k).equals("null")) ? "'0'" : getCell(sheet, 14, k)) + ", "
                        + ((getCell(sheet, 16, k).equals("null")) ? "'0'" : getCell(sheet, 16, k)) + ", "
                        + ((getCell(sheet, 17, k).equals("null")) ? "' '" : getCell(sheet, 17, k))
                        + ");";
                if (freeProcess) {
                    sql = "delete from tbschedulejob where ta_code='" + taCode + "' and sche_job_code = " + getCell(sheet, 2, k) + ";\n" + sql;
                }
                parentJobList.add(sql);
                // 校验job配置合理性
                String jobCode = sheet.getCell(2, k).getContents();
                if (jobToGroup.containsKey(jobCode)) {
                    String msg = String.format("一级JOB【%s】不能同时出现在流程【%s】和【%s】，请调整", jobCode, groupCode, jobToGroup.get(jobCode));
                    LoggerUtils.info(msg);
                    throw new Exception(msg);
                } else {
                    jobToGroup.put(jobCode, groupCode);
                }
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
            if (StringUtils.isBlank(getCell(sheet, 2, k)) || "null".equals(getCell(sheet, 2, k))) {
                continue;
            }
            // 二级JOB
            if (!sheet.getCell(3, k).getContents().equals("1")) {
                String sql = "insert into tbschedulejob (sche_group_code,sche_job_code,sche_job_name," +
                        "sche_parent_job_code,sche_job_isuse,sche_up_job_code,bank_no,ta_code,sche_job_url," +
                        "sche_job_pause,sche_ishidebutton,url_open_mode, sche_job_ishide, sche_job_isskip," +
                        "sche_job_iswait, order_no, sche_depend_undonodes) " +
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
                        + ((getCell(sheet, 11, k).equals("null")) ? "'0'" : getCell(sheet, 11, k)) + ","
                        + ((getCell(sheet, 15, k).equals("null")) ? getCell(sheet, 9, k).equals("null") ? "'0'" : "'1'" : getCell(sheet, 15, k)) + ","
                        + ((getCell(sheet, 12, k).equals("null")) ? "'0'" : getCell(sheet, 12, k)) + ","
                        + ((getCell(sheet, 13, k).equals("null")) ? "'0'" : getCell(sheet, 13, k)) + ","
                        + ((getCell(sheet, 14, k).equals("null")) ? "'0'" : getCell(sheet, 14, k)) + ","
                        + ((getCell(sheet, 16, k).equals("null")) ? "'0'" : getCell(sheet, 16, k)) + ","
                        + ((getCell(sheet, 17, k).equals("null")) ? "' '" : getCell(sheet, 17, k))
                        + ");";
                if (freeProcess) {
                    sql = "delete from tbschedulejob where ta_code='" + taCode + "' and sche_job_code = " + getCell(sheet, 2, k) + ";\n" + sql;
                }
                secondJobList.add(sql);
                // 校验job配置合理性
                String jobCode = sheet.getCell(2, k).getContents();
                if (jobToGroup.containsKey(jobCode)) {
                    String msg = String.format("二级JOB【%s】不能同时出现在流程【%s】和【%s】，请调整", jobCode, groupCode, jobToGroup.get(jobCode));
                    LoggerUtils.info(msg);
                    throw new Exception(msg);
                } else {
                    jobToGroup.put(jobCode, groupCode);
                }
            }
        }
        for (String sql : secondJobList) {
            bufferedWriter.write(sql + "\n");
        }
        bufferedWriter.write("-- 二级job配置脚本 end  " + groupName + "\n\n");
        return jobTaskMap;
    }

    /**
     * 生成trigger脚本
     *
     * @param taCode
     * @param sheet
     * @param bufferedWriter
     */
    private void writeTriggerInfo(String taCode, Sheet sheet, BufferedWriter bufferedWriter) throws IOException {
        int rows = sheet.getRows();

        bufferedWriter.write("\n-- 定时任务配置脚本 begin \n");
        bufferedWriter.write("-- delete from tbscheduletrigger where sche_code like 'fund_%'; \n");

        for (int k = 2; k < rows; k++) {
            String sql = "-- insert into tbscheduletrigger (sche_code_type,sche_code,sche_trigger_status," +
                    "sche_trigger_lasttime,sche_trigger_nexttime,sche_trigger_cron,is_workday_trigger,enable_flag) " +
                    " \n-- values ("
                    + getCell(sheet, 1, k) + ","
                    + getCell(sheet, 2, k) + ","
                    + getCell(sheet, 3, k) + ","
                    + getCell(sheet, 4, k) + ","
                    + getCell(sheet, 5, k) + ","
                    + getCell(sheet, 6, k) + ","
                    + getCell(sheet, 7, k) + ","
                    + getCell(sheet, 8, k)
                    + ");";
            bufferedWriter.write(sql + "\n");
        }
        bufferedWriter.write("-- 定时任务配置脚本 end \n\n");
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
        if (i >= sheet.getColumns()) {
            return "null";
        }
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
    private void writeTaskInfo(String taCode, BufferedWriter bufferedWriter, Map<String, String> jobWithTaskMap) throws Exception {
        bufferedWriter.write("-- SCHEDULETASK 配置 begin\n");
        for (Map.Entry entry : jobWithTaskMap.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            // 将value 拆分为列表
            String[] taskList = value.split("\n");
            String parentTaskCode = "";
            for (String taskCode : taskList) {
                if (StringUtils.isBlank(taskCode)) {
                    continue;
                }
                Map map = TaskMemoryCache.getCacheMap(taskCode);
                if (map == null) {
                    String msg = key + "的TASK[" + taskCode + "]在【任务配置】sheet页中不存在";
                    OutputUtils.info(log, msg + STR_NEXT_LINE);
                    OutputUtils.info(log, msg + STR_NEXT_LINE);
                    LoggerUtils.info(msg);
                    return;
                }
                // 校验task配置合理性
                if (taskToJob.containsKey(taskCode)) {
                    String msg = String.format("任务代码【%s】不能同时出现在一级JOB【%s】和【%s】，请调整", taskCode, key, taskToJob.get(taskCode));
                    LoggerUtils.info(msg);
                    throw new Exception(msg);
                } else {
                    taskToJob.put(taskCode, key);
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
                        + map.get(FUNCTION_ID) + "' , '"
                        + map.get(TaskMemoryCache.BANK_NO) + "' , '"
                        + taCode + "' , '"
                        + map.get(TaskMemoryCache.SCHE_TASK_ISSKIP) + "' , '"
                        + map.get(TaskMemoryCache.SCHE_TASK_SKIPREASON) + "' , "
                        + (map.get(TaskMemoryCache.SCHE_TASK_DELAYTIME).equals(" ") ? " " : "null") + " , '"
                        + map.get(TaskMemoryCache.SCHE_TASK_PAUSE) + "', null , '"
                        + (map.get(SCHE_TASK_RESERVE).equals("") ? " " : map.get(SCHE_TASK_RESERVE)) + "' );";

                String functionId = (String) map.get(FUNCTION_ID);
                String reserve = (String) map.get(SCHE_TASK_RESERVE);
                bufferedWriter.write(taskSql + "\n");
                List<Map<String, Object>> subTaskList = TaskMemoryCache.getCacheMapByFunction(functionId, reserve);
                String parentSubTaskCode = "";
                for (Map<String, Object> subMap : subTaskList) {
                    if (subMap == null || subMap.get(TaskMemoryCache.SCHE_TASK_CODE) == null) {
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
                                + subMap.get(FUNCTION_ID) + "' , '"
                                + subMap.get(TaskMemoryCache.BANK_NO) + "' , '"
                                + taCode + "' , '"
                                + subMap.get(TaskMemoryCache.SCHE_TASK_ISSKIP) + "' , '"
                                + subMap.get(TaskMemoryCache.SCHE_TASK_SKIPREASON) + "' ,  "
                                + (map.get(TaskMemoryCache.SCHE_TASK_DELAYTIME).equals(" ") ? " " : "null") + " , '"
                                + subMap.get(TaskMemoryCache.SCHE_TASK_PAUSE) + "' , '" + functionId + "', '"
                                + (map.get(SCHE_TASK_RESERVE).equals("") ? " " : map.get(SCHE_TASK_RESERVE)) + "' );";
                        // 写 子task SCHE_JOB_CODE 字段填写空
                        bufferedWriter.write(subTaskSql + "\n");
                    }
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
                "delete from tbscheduletaskregistry where substr(sche_task_code, 1, 4) = 'fund' and instr(app_name, '-self-') = 0 and instr(app_name, '-aop-') = 0;\n" +
                "-- 主库交易广播配置 \n" +
                "insert into tbscheduletaskregistry (sche_task_code, app_name, app_group, app_version, \n" +
                "       app_url, sche_app_isuse)\n" +
                "select a.sche_task_code, 'ta-fund-parameter-batch' app_name, 'group' app_group, '1.0' app_version, \n" +
                "       '/fund/pub/batch' app_url, '1' sche_app_isuse\n" +
                "  from tbscheduletask a \n" +
                " where substr(a.sche_task_code, 1, 4) = 'fund' \n" +
                "   and substr(a.function_id, 1, 4) = 'PUB9'\n" +
                "   and a.ta_code = '000000';\n" +
                "\n" +
                "-- 交易库交易广播配置 多个分库的情况下，修改针对不同的app_name多次执行 \n" +
                "insert into tbscheduletaskregistry (sche_task_code, app_name, app_group, app_version, \n" +
                "       app_url, sche_app_isuse,shard_total_count)\n" +
                "select a.sche_task_code, concat('ta-fund-trans-batch', b.db_no) app_name, 'group' app_group, '1.0' app_version, \n" +
                "       '/ta/fund/trans/batch' app_url, '1' sche_app_isuse,case when '{statelessDeploy}'='true' then {TRANSDBNUM} else 1 end shard_total_count\n" +
                "  from tbscheduletask a, {TRANSDBNOSQL} b\n" +
                " where substr(a.sche_task_code, 1, 4) = 'fund' \n" +
                "   and substr(a.function_id, 1, 4) = 'FUND'\n" +
                "   and a.ta_code = '000000';\n" +
                "\n" +
                "-- 账户库交易广播配置 多个分库的情况下，修改针对不同的app_name多次执行 \n" +
                "insert into tbscheduletaskregistry (sche_task_code, app_name, app_group, app_version, \n" +
                "       app_url, sche_app_isuse,shard_total_count)\n" +
                "select a.sche_task_code, concat('ta-fund-account-batch', b.db_no) app_name, 'group' app_group, '1.0' app_version, \n" +
                "       '/ta/acc/fund/batch' app_url, '1' sche_app_isuse,case when '{statelessDeploy}'='true' then {ACCDBNUM} else 1 end shard_total_count\n" +
                "  from tbscheduletask a, {ACCDBNOSQL} b\n" +
                " where substr(a.sche_task_code, 1, 4) = 'fund' \n" +
                "   and substr(a.function_id, 1, 4) = 'ACC9'\n" +
                "   and a.ta_code = '000000';\n";
        bufferedWriter.write(sql);
        bufferedWriter.write("-- tbscheduletaskregistry 配置 end\n ");
        bufferedWriter.write("\ncommit;\n");
    }
}
