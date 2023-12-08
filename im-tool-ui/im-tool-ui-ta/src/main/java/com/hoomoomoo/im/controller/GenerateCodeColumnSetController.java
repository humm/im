package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.ColumnDto;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;

import java.net.URL;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.STYLE_CENTER;

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


    @SneakyThrows
    @FXML
    void saveColumn(ActionEvent event) {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        List<ColumnDto> configColumnList = appConfigDto.getConfigColumnList();
    }

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ArrayList<TableColumn> columnList = new ArrayList<TableColumn>();
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        List<ColumnDto> tableColumnList = appConfigDto.getTableColumnList();
        List<ColumnDto> configColumnList = appConfigDto.getConfigColumnList();
        if (CollectionUtils.isEmpty(configColumnList)) {
            configColumnList = tableColumnList;
            appConfigDto.setConfigColumnList(tableColumnList);
        }

        Set<String> modeList = new HashSet<>();
        modeList.add("0");
        modeList.add("1");

        TableColumn columnCode = new TableColumn<>("字段代码");
        columnCode.setCellValueFactory(new PropertyValueFactory<>("columnCode"));
        columnCode.setPrefWidth(300);
        columnCode.setEditable(false);
        columnList.add(columnCode);

        TableColumn columnName = new TableColumn<>("字段名称");
        columnName.setCellValueFactory(new PropertyValueFactory<>("columnName"));
        columnName.setPrefWidth(300);
        columnName.setOnEditCommit(event -> {
            getColumnInfo(event).setColumnName(getColumnValue(event));
        });
        columnList.add(columnName);

        TableColumn<ColumnDto, String> columnType = new TableColumn<>("字段类型");
        columnType.setCellValueFactory(new PropertyValueFactory<>("columnType"));
        columnType.setPrefWidth(100);
        columnType.setStyle(STYLE_CENTER);
        columnType.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(modeList)));
        //columnList.add(columnType);

        columnInfo.getColumns().add(columnType);
        columnInfo.getColumns().addAll(columnList);
        columnInfo.setEditable(true);

        OutputUtils.clearLog(columnInfo);
        OutputUtils.infoList(columnInfo, configColumnList, false);
    }

    private ColumnDto getColumnInfo(javafx.event.Event event) {
        int index = ((TableColumn.CellEditEvent<ColumnDto, Object>) event).getTablePosition().getRow();
        return ((TableColumn.CellEditEvent<ColumnDto, Object>) event).getTableView().getItems().get(index);
    }

    private String getColumnValue(javafx.event.Event event) {
        return ((TableColumn.CellEditEvent<ColumnDto, Object>) event).getNewValue().toString();
    }
}
