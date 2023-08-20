package com.hoomoomoo.im.service;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.controller.HepWaitHandleTaskController;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.HepTaskDto;
import com.hoomoomoo.im.utils.CommonUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import lombok.SneakyThrows;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.controller.HepWaitHandleTaskController.OPERATE_TYPE_CUSTOM_UPDATE;


public class HepWaitHandleTaskMenu extends ContextMenu {

    private static HepWaitHandleTaskMenu instance;
    private HepWaitHandleTaskMenu() {
        MenuItem copyTask = new MenuItem(NAME_MENU_COPY);
        CommonUtils.setIcon(copyTask, COPY_ICON, MENUITEM_ICON_SIZE);
        copyTask.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override
            public void handle(ActionEvent event) {
                AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                HepTaskDto item = appConfigDto.getHepTaskDto();
                StringBuilder info = new StringBuilder();
                info.append("[需求编号]").append(STR_SPACE).append(STR_NEXT_LINE);
                info.append("[修改单编号]").append(STR_SPACE).append(item.getTaskNumber()).append(STR_NEXT_LINE);
                info.append("[修改单版本]").append(STR_SPACE).append(item.getSprintVersion()).append(STR_NEXT_LINE);
                info.append("[需求引入行]").append(STR_SPACE).append(STR_NEXT_LINE);
                String name = item.getName();
                if (name.contains(STR_BRACKETS_2_RIGHT)) {
                    name = name.split(STR_BRACKETS_2_RIGHT)[1];
                }
                info.append("[需求描述]").append(STR_SPACE).append(name);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(info.toString()), null);
            }
        });
        MenuItem updateTask = new MenuItem(NAME_MENU_UPDATE);
        CommonUtils.setIcon(updateTask, UPDATE_ICON, MENUITEM_ICON_SIZE);
        updateTask.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override
            public void handle(ActionEvent event) {
                AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                HepTaskDto item = appConfigDto.getHepTaskDto();
                item.setOperateType(OPERATE_TYPE_CUSTOM_UPDATE);
                HepWaitHandleTaskController hepWaitHandleTaskController = new HepWaitHandleTaskController();
                hepWaitHandleTaskController.completeTask(item);
            }
        });
        getItems().add(copyTask);
        getItems().add(updateTask);
    }

    public static HepWaitHandleTaskMenu getInstance() {
        if (instance == null) {
            instance = new HepWaitHandleTaskMenu();
        }
        return instance;
    }
}

