package com.hoomoomoo.im.extend;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.ColumnDto;
import com.hoomoomoo.im.utils.CommonUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import lombok.SneakyThrows;

import static com.hoomoomoo.im.consts.BaseConst.*;


public class ConfigColumnMenu extends ContextMenu {

    private static ConfigColumnMenu instance;

    private ConfigColumnMenu() {
        MenuItem menuDict = new MenuItem(NAME_MENU_DCIT);
        initEvent(menuDict);
        MenuItem menuDate = new MenuItem(NAME_MENU_DATE);
        initEvent(menuDate);
        MenuItem menuDecimal = new MenuItem(NAME_MENU_DECIMAL);
        initEvent(menuDecimal);
    }

    private void initEvent(MenuItem menuDict) {
        menuDict.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override
            public void handle(ActionEvent event) {
                AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
                ColumnDto item = appConfigDto.getColumnDto();
            }
        });
        getItems().add(menuDict);
    }

    public static ConfigColumnMenu getInstance() {
        if (instance == null) {
            instance = new ConfigColumnMenu();
        }
        return instance;
    }

}

