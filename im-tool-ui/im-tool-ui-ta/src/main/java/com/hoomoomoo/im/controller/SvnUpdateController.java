package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.LogDto;
import com.hoomoomoo.im.task.SvnUpdateTask;
import com.hoomoomoo.im.task.SvnUpdateTaskParam;
import com.hoomoomoo.im.utils.*;
import com.sun.jna.platform.unix.solaris.LibKstat;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.SVN_UPDATE;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/05/09
 */
public class SvnUpdateController extends BaseController implements Initializable {

    @FXML
    private Label workspaceNum;

    @FXML
    private Label failNum;

    @FXML
    private Button svnSubmit;

    @FXML
    private Label svnName;

    @FXML
    private TableView svnLog;

    @FXML
    private TextArea fileLog;

    @FXML
    private TextArea fileLogError;

    @FXML
    void executeSubmit(ActionEvent event) {
        LoggerUtils.info(String.format(BaseConst.MSG_USE, SVN_UPDATE.getName()));
        try {
            if (!TaCommonUtils.checkConfig(fileLogError, SVN_UPDATE.getCode())) {
                return;
            }
            setProgress(0);
            OutputUtils.clearLog(svnLog);
            OutputUtils.clearLog(fileLog);
            OutputUtils.clearLog(fileLogError);
            OutputUtils.clearLog(workspaceNum);
            OutputUtils.clearLog(failNum);

            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            OutputUtils.info(workspaceNum, String.valueOf(appConfigDto.getSvnUpdatePath().size()));
            OutputUtils.info(failNum, STR_0);
            updateProgress(0.01);
            TaskUtils.execute(new SvnUpdateTask(new SvnUpdateTaskParam(this)));
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(fileLogError, CommonUtils.getCurrentDateTime1() + BaseConst.STR_SPACE + e.getMessage());
        }
    }

    public void getSvnUpdate() {
        try {
            svnSubmit.setDisable(true);
            Date date = new Date();
            List<String> updatePath = new ArrayList<>();
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            List<LinkedHashMap<String, String>> pathList = appConfigDto.getSvnUpdatePath();
            Set<String> updateFlag = new HashSet<>();
            int workspaceNumWaitUpdate = appConfigDto.getSvnUpdatePath().size();
            if (CollectionUtils.isNotEmpty(pathList)) {
                for (LinkedHashMap<String, String> item : pathList) {
                    Iterator<String> iterator = item.keySet().iterator();
                    while (iterator.hasNext()) {
                        workspaceNumWaitUpdate--;
                        if (workspaceNumWaitUpdate < 0) {
                            workspaceNumWaitUpdate = 0;
                        }
                        String name = iterator.next();
                        String path = item.get(name);
                        if (updateFlag.contains(name)) {
                            Thread.sleep(500L);
                            OutputUtils.info(fileLogError, CommonUtils.getCurrentDateTime1() + BaseConst.STR_SPACE + "重复路径【 " + name + " 】跳过更新...\n");
                            OutputUtils.info(workspaceNum, String.valueOf(workspaceNumWaitUpdate));
                            continue;
                        }
                        updateFlag.add(name);
                        updatePath.add(path);
                        OutputUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + BaseConst.STR_SPACE + "更新【 " + name + " 】开始\n");
                        OutputUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + BaseConst.STR_SPACE_4 + path + "\n");
                        File updateFile = new File(path);
                        if (FileUtils.isSuffixDirectory(updateFile, BaseConst.FILE_TYPE_SVN, true, false)) {
                            String version = SvnUtils.updateSvn(path, fileLogError);
                            if (version == null) {
                                LoggerUtils.info("更新异常: " + name);
                                infoMsg(name, null, "更新异常");
                                OutputUtils.info(failNum, String.valueOf(Integer.valueOf(failNum.getText()) + 1));
                                OutputUtils.info(fileLogError, CommonUtils.getCurrentDateTime1() + BaseConst.STR_SPACE + "更新【 " + name + " 】异常\n");
                            } else {
                                infoMsg(name, version, "更新完成");
                                OutputUtils.info(fileLog, CommonUtils.getCurrentDateTime1() + BaseConst.STR_SPACE + "更新【 " + name + " 】完成\n");
                            }
                        } else if (FileUtils.isSuffixDirectory(updateFile, BaseConst.FILE_TYPE_GIT, false, true)) {
                            // String pullPath = path + STR_SLASH + FILE_GIT_PULL_BAT;
                            // FileUtils.copyFile(new File(FileUtils.getFilePath(PATH_GIT_PULL)), new File(pullPath));
                            // String res = CmdUtils.exeByFile(pullPath);
                            // updatePath.add(res);
                            // infoMsg(name, "-1", "更新完成");
                            infoMsg(name, "-1", "git目录请手动更新");
                            OutputUtils.info(fileLogError, CommonUtils.getCurrentDateTime1() + BaseConst.STR_SPACE + "【 " + name + " 】git目录请手动更新\n");
                        } else {
                            Thread.sleep(500L);
                            infoMsg(name, "-1", "无需更新");
                            OutputUtils.info(fileLogError, CommonUtils.getCurrentDateTime1() + BaseConst.STR_SPACE + "【 " + name + " 】非svn git目录,无需更新\n");
                        }
                    }
                    OutputUtils.info(workspaceNum, String.valueOf(workspaceNumWaitUpdate));
                }
            }
            setProgress(1);
            LoggerUtils.writeSvnUpdateInfo(date, updatePath);
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(fileLogError, CommonUtils.getCurrentDateTime1() + BaseConst.STR_SPACE + ExceptionMsgUtils.getMsg(e));
        } finally {
            svnSubmit.setDisable(false);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            OutputUtils.clearLog(svnName);
            OutputUtils.clearLog(workspaceNum);
            OutputUtils.clearLog(failNum);
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            if (StringUtils.isNotBlank(appConfigDto.getSvnUsername())) {
                OutputUtils.info(svnName, appConfigDto.getSvnUsername());
            }
            OutputUtils.info(workspaceNum, String.valueOf(appConfigDto.getSvnUpdatePath().size()));
            OutputUtils.info(failNum, STR_0);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    private void infoMsg(String name, String submitNo, String msg) {
        if (StringUtils.isBlank(submitNo)) {
            submitNo = STR_0;
        }
        LogDto logDto = new LogDto();
        logDto.setTime(CommonUtils.getCurrentDateTime1());
        logDto.setName(name);
        logDto.setSubmitNo(submitNo);
        logDto.setMsg(msg);
        OutputUtils.info(svnLog, logDto);
    }
}
