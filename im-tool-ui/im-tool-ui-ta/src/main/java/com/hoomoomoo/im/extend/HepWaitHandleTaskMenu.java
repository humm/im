package com.hoomoomoo.im.extend;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.controller.HepTodoController;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.HepTaskDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.JvmCache;
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

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.FileInputStream;

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
        CommonUtils.setIcon(copyTaskSimple, COPY_SIMPLE_ICON, MENUITEM_ICON_SIZE);
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

        MenuItem menuScript = new MenuItem(NAME_MENU_SCRIPT);
        CommonUtils.setIcon(menuScript, ABSTRACT_ICON, MENUITEM_ICON_SIZE);
        menuScript.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override
            public void handle(ActionEvent event) {
                AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
                String info = getScriptContent(appConfigDto);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(info), null);
            }
        });

        MenuItem menuTaskNo = new MenuItem(NAME_MENU_TASK_NO);
        CommonUtils.setIcon(menuTaskNo, TASK_NO_ICON, MENUITEM_ICON_SIZE);
        menuTaskNo.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override
            public void handle(ActionEvent event) {
                AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
                String info = appConfigDto.getHepTaskDto().getTaskNumber();
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(info), null);
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
        getItems().add(copyTask);
        getItems().add(copyTaskSimple);
        getItems().add(updateTask);
        getItems().add(menuScript);
        getItems().add(menuTaskNo);
        getItems().add(detailTask);
    }

    public static HepWaitHandleTaskMenu getInstance() {
        if (instance == null) {
            instance = new HepWaitHandleTaskMenu();
        }
        return instance;
    }

    private String getCopyContent(HepTaskDto item, boolean hasDescribe) {
        StringBuilder info = new StringBuilder();
        info.append("[需求编号]").append(STR_SPACE).append(STR_NEXT_LINE);
        info.append("[修改单编号]").append(STR_SPACE).append(item.getTaskNumber()).append(STR_NEXT_LINE);
        info.append("[修改单版本]").append(STR_SPACE).append(item.getSprintVersion()).append(STR_NEXT_LINE);
        info.append("[需求引入行]").append(STR_SPACE);
        if (hasDescribe) {
            String name = item.getName();
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
        String tips = "-- " + item.getTaskNumber() + STR_SPACE_2 + CommonUtils.getCurrentDateTime3() + STR_SPACE_2 + item.getAssigneeName();
        info.append(tips).append(STR_SPACE_2).append("beg").append(STR_NEXT_LINE);
        info.append(STR_NEXT_LINE);
        info.append(tips).append(STR_SPACE_2).append("end").append(STR_NEXT_LINE);
        return info.toString();
    }
}

