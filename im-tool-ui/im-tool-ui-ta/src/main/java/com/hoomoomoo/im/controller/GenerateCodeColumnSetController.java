package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.ColumnDto;
import com.hoomoomoo.im.dto.HepTaskDto;
import com.hoomoomoo.im.extend.ConfigColumnMenu;
import com.hoomoomoo.im.extend.HepWaitHandleTaskMenu;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;
import java.util.List;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description
 * @package com.hoomoomoo.im.controller
 * @date 2021/9/16
 */
public class GenerateCodeColumnSetController implements Initializable {

    @FXML
    private Button save;

    @FXML
    private TableView columnInfo;

    @FXML
    private Label tips;


    @FXML
    void saveColumn(ActionEvent event) {

    }

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        List<ColumnDto> tableColumnList = appConfigDto.getTableColumnList();
        List<ColumnDto> configColumnList = appConfigDto.getConfigColumnList();
        if (CollectionUtils.isEmpty(configColumnList)) {
            configColumnList = tableColumnList;
        }
        OutputUtils.clearLog(columnInfo);
        TableColumn columnCode = new TableColumn<>("字段代码");
        columnCode.setCellValueFactory(new PropertyValueFactory<>("columnCode"));
        columnCode.setPrefWidth(300);

        TableColumn columnName = new TableColumn<>("字段名称");
        columnName.setCellValueFactory(new PropertyValueFactory<>("columnName"));
        columnName.setPrefWidth(300);
        columnName.setEditable(true);

        TableColumn columnType = new TableColumn<>("字段类型");
        columnType.setCellValueFactory(new PropertyValueFactory<>("columnType"));
        columnType.setPrefWidth(100);
        columnType.setEditable(false);

        columnInfo.getColumns().addAll(columnCode, columnName, columnType);
        OutputUtils.infoList(columnInfo, configColumnList, false);

        addColumnMenu(appConfigDto);
    }

    private void addColumnMenu(AppConfigDto appConfigDto) {
        columnInfo.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String clickType = event.getButton().toString();
                if (RIGHT_CLICKED.equals(clickType)) {
                    Node node = event.getPickResult().getIntersectedNode();
                    ConfigColumnMenu configColumnMenu = ConfigColumnMenu.getInstance();
                    ColumnDto columnDto = (ColumnDto) columnInfo.getSelectionModel().getSelectedItem();
                    configColumnMenu.getItems().forEach((item) -> {

                    });
                    configColumnMenu.show(node, event.getScreenX(), event.getScreenY());
                    appConfigDto.setColumnDto(columnDto);
                }
            }
        });
    }
}
