package com.hoomoomoo.im.extend;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.controller.HepTodoController;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.HepTaskDto;
import com.hoomoomoo.im.utils.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.controller.HepTodoController.OPERATE_TYPE_CUSTOM_UPDATE;


public class HepWaitHandleTaskMenu extends ContextMenu {

    private static HepWaitHandleTaskMenu instance;
    private HepWaitHandleTaskMenu() {
        MenuItem copyTask = new MenuItem(NAME_MENU_COPY);
        CommonUtils.setIcon(copyTask, COPY_ICON, MENUITEM_ICON_SIZE);
        copyTask.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override
            public void handle(ActionEvent event) {
                AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
                HepTaskDto item = appConfigDto.getHepTaskDto();
                String info = getCopyContent(item, true);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(info), null);
            }
        });

        MenuItem copyTaskSimple = new MenuItem(NAME_MENU_SIMPLE_COPY);
        CommonUtils.setIcon(copyTaskSimple, COPY_ICON, MENUITEM_ICON_SIZE);
        copyTaskSimple.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override
            public void handle(ActionEvent event) {
                AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
                HepTaskDto item = appConfigDto.getHepTaskDto();
                String info = getCopyContent(item, false);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(info), null);
            }
        });

        MenuItem menuScriptCopy = new MenuItem(NAME_MENU_SCRIPT_COPY);
        CommonUtils.setIcon(menuScriptCopy, COPY_ICON, MENUITEM_ICON_SIZE);
        menuScriptCopy.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override
            public void handle(ActionEvent event) {
                AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
                String info = getScriptContent(appConfigDto);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(info), null);
            }
        });

        MenuItem menuTaskNoCopy = new MenuItem(NAME_MENU_TASK_NO_COPY);
        CommonUtils.setIcon(menuTaskNoCopy, COPY_ICON, MENUITEM_ICON_SIZE);
        menuTaskNoCopy.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override
            public void handle(ActionEvent event) {
                AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
                String info = appConfigDto.getHepTaskDto().getTaskNumber();
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(info), null);
            }
        });

        MenuItem updateTask = new MenuItem(NAME_MENU_UPDATE);
        CommonUtils.setIcon(updateTask, UPDATE_ICON, MENUITEM_ICON_SIZE);
        updateTask.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override
            public void handle(ActionEvent event) {
                AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
                HepTaskDto item = appConfigDto.getHepTaskDto();
                item.setOperateType(OPERATE_TYPE_CUSTOM_UPDATE);
                HepTodoController hepWaitHandleTaskController = JvmCache.getHepTodoController();
                hepWaitHandleTaskController.completeTask(item);
            }
        });

        MenuItem detailTask = new MenuItem(NAME_MENU_DETAIL);
        CommonUtils.setIcon(detailTask, DETAIL_ICON, MENUITEM_ICON_SIZE);
        detailTask.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override
            public void handle(ActionEvent event) {
                AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
                appConfigDto.setPageType(PAGE_TYPE_HEP_DETAIL);
                Stage stage = appConfigDto.getChildStage();
                // 每次页面都重新打开
                if (stage != null) {
                    stage.close();
                    appConfigDto.setChildStage(null);
                }
                Parent root = new FXMLLoader().load(new FileInputStream(FileUtils.getFilePath(PATH_BLANK_SET_FXML)));
                Scene scene = new Scene(root);
                scene.getStylesheets().add(FileUtils.getFileUrl(PATH_STARTER_CSS).toExternalForm());
                stage = new Stage();
                stage.getIcons().add(new Image(PATH_ICON));
                stage.setScene(scene);
                stage.setTitle("任务详情");
                stage.setResizable(false);
                stage.show();
                appConfigDto.setChildStage(stage);
                stage.setOnCloseRequest(columnEvent -> {
                    appConfigDto.getChildStage().close();
                    appConfigDto.setChildStage(null);
                });
            }
        });

        MenuItem menuMarkDev = new MenuItem(NAME_MENU_MARK_DEV);
        CommonUtils.setIcon(menuMarkDev, COMPLETE_ICON, MENUITEM_ICON_SIZE);
        menuMarkDev.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override
            public void handle(ActionEvent event) {
                handleDev(STR_1, NAME_MENU_MARK_DEV);
            }
        });

        MenuItem menuCancelDev = new MenuItem(NAME_MENU_CANCEL_DEV);
        CommonUtils.setIcon(menuCancelDev, CANCEL_ICON, MENUITEM_ICON_SIZE);
        menuCancelDev.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override
            public void handle(ActionEvent event) {
                handleDev(STR_0, NAME_MENU_CANCEL_DEV);
            }
        });

        MenuItem menuMarkSubmit = new MenuItem(NAME_MENU_MARK_SUBMIT);
        CommonUtils.setIcon(menuMarkSubmit, COMPLETE_ICON, MENUITEM_ICON_SIZE);
        menuMarkSubmit.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override
            public void handle(ActionEvent event) {
                handleSubmit(STR_1, NAME_MENU_MARK_SUBMIT);
            }
        });

        MenuItem menuCancelSubmit = new MenuItem(NAME_MENU_CANCEL_SUBMIT);
        CommonUtils.setIcon(menuCancelSubmit, CANCEL_ICON, MENUITEM_ICON_SIZE);
        menuCancelSubmit.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override
            public void handle(ActionEvent event) {
                handleSubmit(STR_0, NAME_MENU_CANCEL_SUBMIT);
            }
        });

        MenuItem menuTaskLevelSimple = new MenuItem(NAME_MENU_TASK_LEVEL_SIMPLE);
        CommonUtils.setIcon(menuTaskLevelSimple, CANCEL_LEVEL_ICON, MENUITEM_ICON_SIZE);
        menuTaskLevelSimple.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override
            public void handle(ActionEvent event) {
                handleLevel(STR_0, NAME_MENU_TASK_LEVEL_SIMPLE);
            }
        });

        MenuItem menuTaskLevelGeneral = new MenuItem(NAME_MENU_TASK_LEVEL_GENERAL);
        CommonUtils.setIcon(menuTaskLevelGeneral, CANCEL_LEVEL_ICON, MENUITEM_ICON_SIZE);
        menuTaskLevelGeneral.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override
            public void handle(ActionEvent event) {
                handleLevel(STR_1, NAME_MENU_TASK_LEVEL_GENERAL);
            }
        });

        MenuItem menuTaskLevelDifficulty = new MenuItem(NAME_MENU_TASK_LEVEL_DIFFICULTY);
        CommonUtils.setIcon(menuTaskLevelDifficulty, CANCEL_LEVEL_ICON, MENUITEM_ICON_SIZE);
        menuTaskLevelDifficulty.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override
            public void handle(ActionEvent event) {
                handleLevel(STR_2, NAME_MENU_TASK_LEVEL_DIFFICULTY);
            }
        });

        MenuItem menuTaskLevelQuestion = new MenuItem(NAME_MENU_TASK_LEVEL_QUESTION);
        CommonUtils.setIcon(menuTaskLevelQuestion, CANCEL_LEVEL_ICON, MENUITEM_ICON_SIZE);
        menuTaskLevelQuestion.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override
            public void handle(ActionEvent event) {
                handleLevel(STR_3, NAME_MENU_TASK_LEVEL_QUESTION);
            }
        });

        MenuItem menuTaskLevelErrorVersion = new MenuItem(NAME_MENU_TASK_LEVEL_CANCEL_ERROR_VERSION);
        CommonUtils.setIcon(menuTaskLevelErrorVersion, CANCEL_LEVEL_ICON, MENUITEM_ICON_SIZE);
        menuTaskLevelErrorVersion.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override
            public void handle(ActionEvent event) {
                handleLevelSpecial(STR_1, NAME_MENU_TASK_LEVEL_CANCEL_ERROR_VERSION);
            }
        });

        MenuItem menuTaskLevelOnlySelf = new MenuItem(NAME_MENU_TASK_LEVEL_CANCEL_ONLY_SELF);
        CommonUtils.setIcon(menuTaskLevelOnlySelf, CANCEL_LEVEL_ICON, MENUITEM_ICON_SIZE);
        menuTaskLevelOnlySelf.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override
            public void handle(ActionEvent event) {
                handleLevelSpecial(STR_2, NAME_MENU_TASK_LEVEL_CANCEL_ONLY_SELF);
            }
        });

        MenuItem menuTaskLevelClear = new MenuItem(NAME_MENU_TASK_LEVEL_CLEAR);
        CommonUtils.setIcon(menuTaskLevelClear, CANCEL_LEVEL_CLEAR_ICON, MENUITEM_ICON_SIZE);
        menuTaskLevelClear.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override
            public void handle(ActionEvent event) {
                handleLevel(STR_4, NAME_MENU_TASK_LEVEL_CLEAR);
            }
        });

        getItems().add(copyTask);
        getItems().add(copyTaskSimple);
        getItems().add(menuScriptCopy);
        getItems().add(menuTaskNoCopy);
        //getItems().add(detailTask);
        getItems().add(updateTask);
        getItems().add(menuMarkDev);
        getItems().add(menuCancelDev);
        getItems().add(menuMarkSubmit);
        getItems().add(menuCancelSubmit);
        getItems().add(menuTaskLevelSimple);
        getItems().add(menuTaskLevelGeneral);
        getItems().add(menuTaskLevelDifficulty);
        getItems().add(menuTaskLevelQuestion);
        getItems().add(menuTaskLevelErrorVersion);
        getItems().add(menuTaskLevelOnlySelf);
        getItems().add(menuTaskLevelClear);
    }

    public static HepWaitHandleTaskMenu getInstance() {
        if (instance == null) {
            instance = new HepWaitHandleTaskMenu();
        }
        return instance;
    }

    private void handleDev(String type, String btnName) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        HepTaskDto item = appConfigDto.getHepTaskDto();
        String taskNumber = item.getTaskNumber();
        String eleValue = taskNumber + STR_SEMICOLON + type;
        String eleIndex = taskNumber + STR_SEMICOLON;
        String path = FileUtils.getFilePath(PATH_DEFINE_TASK_DEV_STAT);
        File demandExtendStat = new File(path);
        if (!demandExtendStat.exists()) {
            demandExtendStat.createNewFile();
        }
        List<String> taskNumberList = FileUtils.readNormalFile(path);
        if (!taskNumberList.contains(eleIndex + STR_0) && !taskNumberList.contains(eleIndex + STR_1)) {
            taskNumberList.add(eleValue);
        } else {
            for (int i=0; i<taskNumberList.size(); i++) {
                String ele = taskNumberList.get(i);
                if (ele.contains(eleIndex)) {
                    taskNumberList.set(i, eleValue);
                    break;
                }
            }
        }
        FileUtils.writeFile(path, taskNumberList);
        String msg = btnName + "成功";
        HepTodoController activeHepTodoController = JvmCache.getActiveHepTodoController();
        OutputUtils.info(activeHepTodoController.notice, TaCommonUtils.getMsgContainTimeContainBr(msg));
        activeHepTodoController.doExecuteQuery();
    }

    private void handleSubmit(String type, String btnName) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        HepTaskDto item = appConfigDto.getHepTaskDto();
        String taskNumber = item.getTaskNumber();
        String eleValue = taskNumber + STR_SEMICOLON + type;
        String eleIndex = taskNumber + STR_SEMICOLON;
        String path = FileUtils.getFilePath(PATH_DEFINE_TASK_STATUS_STAT);
        File taskExtendStat = new File(path);
        if (!taskExtendStat.exists()) {
            taskExtendStat.createNewFile();
        }
        List<String> taskNumberList = FileUtils.readNormalFile(path);
        if (!taskNumberList.contains(eleIndex + STR_0) && !taskNumberList.contains(eleIndex + STR_1)) {
            taskNumberList.add(eleValue);
        } else {
            for (int i=0; i<taskNumberList.size(); i++) {
                String ele = taskNumberList.get(i);
                if (ele.contains(eleIndex)) {
                    taskNumberList.set(i, eleValue);
                    break;
                }
            }
        }
        FileUtils.writeFile(path, taskNumberList);
        String msg = btnName + "成功";
        HepTodoController activeHepTodoController = JvmCache.getActiveHepTodoController();
        OutputUtils.info(activeHepTodoController.notice, TaCommonUtils.getMsgContainTimeContainBr(msg));
        activeHepTodoController.doExecuteQuery();
    }

    private void handleLevel(String type, String btnName) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        HepTaskDto item = appConfigDto.getHepTaskDto();
        String taskNumber = item.getTaskNumber();
        String eleValue = taskNumber + STR_SEMICOLON + type;
        String eleIndex = taskNumber + STR_SEMICOLON;
        String path = FileUtils.getFilePath(PATH_DEFINE_TASK_LEVEL_STAT);
        File taskExtendStat = new File(path);
        if (!taskExtendStat.exists()) {
            taskExtendStat.createNewFile();
        }
        List<String> taskNumberList = FileUtils.readNormalFile(path);
        remarkLevel(taskNumberList, eleValue, eleIndex);
        taskNumber = item.getDemandNo();
        if (StringUtils.isNotBlank(taskNumber)) {
            eleValue = taskNumber + STR_SEMICOLON + type;
            eleIndex = taskNumber + STR_SEMICOLON;
            remarkLevel(taskNumberList, eleValue, eleIndex);
        }
        FileUtils.writeFile(path, taskNumberList);
        String msg = btnName + "成功";
        HepTodoController activeHepTodoController = JvmCache.getActiveHepTodoController();
        OutputUtils.info(activeHepTodoController.notice, TaCommonUtils.getMsgContainTimeContainBr(msg));
        activeHepTodoController.doExecuteQuery();
    }

    private void handleLevelSpecial(String type, String btnName) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        HepTaskDto item = appConfigDto.getHepTaskDto();
        String taskNumber = item.getTaskNumber();
        String path = FileUtils.getFilePath(PATH_DEFINE_TASK_LEVEL_ERROR_VERSION_STAT);
        if (StringUtils.equals(type, STR_2)) {
            path = FileUtils.getFilePath(PATH_DEFINE_TASK_LEVEL_ONLY_SELF_STAT);
        }
        File taskExtendStat = new File(path);
        if (!taskExtendStat.exists()) {
            taskExtendStat.createNewFile();
        }
        List<String> taskNumberList = FileUtils.readNormalFile(path);
        if (!taskNumberList.contains(taskNumber)) {
            taskNumberList.add(taskNumber);
        } else {
            return;
        }
        FileUtils.writeFile(path, taskNumberList);
        String msg = btnName + "成功";
        HepTodoController activeHepTodoController = JvmCache.getActiveHepTodoController();
        OutputUtils.info(activeHepTodoController.notice, TaCommonUtils.getMsgContainTimeContainBr(msg));
        activeHepTodoController.doExecuteQuery();
    }

    private void remarkLevel(List<String> taskNumberList, String eleValue, String eleIndex) {
        if (!taskNumberList.contains(eleIndex + STR_0) && !taskNumberList.contains(eleIndex + STR_1) && !taskNumberList.contains(eleIndex + STR_2)) {
            taskNumberList.add(eleValue);
        } else {
            for (int i=0; i<taskNumberList.size(); i++) {
                String ele = taskNumberList.get(i);
                if (ele.contains(eleIndex)) {
                    taskNumberList.set(i, eleValue);
                    break;
                }
            }
        }
    }

    private String getCopyContent(HepTaskDto item, boolean hasDescribe) {
        StringBuilder info = new StringBuilder();
        String taskDemandNo = item.getDemandNo();
        if (StringUtils.isBlank(taskDemandNo)) {
            taskDemandNo = STR_SPACE;
        }
        info.append("[需求编号]").append(STR_SPACE).append(taskDemandNo).append(STR_NEXT_LINE);
        info.append("[修改单编号]").append(STR_SPACE).append(item.getTaskNumber()).append(STR_NEXT_LINE);
        info.append("[修改单版本]").append(STR_SPACE).append(CommonUtils.getComplexVer(item.getSprintVersion())).append(STR_NEXT_LINE);
        info.append("[需求引入行]").append(STR_SPACE);
        if (hasDescribe) {
            String name = item.getName().replace("【分支已提交】", STR_BLANK);
            if (name.contains(STR_BRACKETS_2_RIGHT)) {
                name = name.substring(name.indexOf(STR_BRACKETS_2_RIGHT) + 1);
            } else if (name.contains(STR_BRACKETS_3_RIGHT)) {
                name = name.substring(name.indexOf(STR_BRACKETS_3_RIGHT) + 1);
            }
            info.append(STR_NEXT_LINE).append("[需求描述]").append(STR_SPACE).append(name);
        }
        return info.toString();
    }

    public static String getScriptContent(AppConfigDto appConfigDto) {
        HepTaskDto item = appConfigDto.getHepTaskDto();
        StringBuilder info = new StringBuilder();
        String tips = "-- " + item.getTaskNumber() + STR_SPACE_2 + CommonUtils.getCurrentDateTime3() + STR_SPACE_2 + item.getAssigneeId();
        info.append(tips).append(STR_SPACE_2).append("beg").append(STR_NEXT_LINE);
        info.append(STR_NEXT_LINE);
        info.append(tips).append(STR_SPACE_2).append("end").append(STR_NEXT_LINE);
        return info.toString();
    }
}

