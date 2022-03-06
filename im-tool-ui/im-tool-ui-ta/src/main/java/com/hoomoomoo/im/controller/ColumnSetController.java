package com.hoomoomoo.im.controller;

import com.alibaba.fastjson.JSONArray;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.ColumnInfoDto;
import com.hoomoomoo.im.dto.GenerateCodeDto;
import com.hoomoomoo.im.dto.LogDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.net.URL;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.FunctionConfig.CONFIG_SET;

/**
 * @author humm23693
 * @description
 * @package com.hoomoomoo.im.controller
 * @date 2021/9/16
 */
public class ColumnSetController implements Initializable {

    @FXML
    private Button save;

    @FXML
    private TableView columnInfo;

    private List<ColumnInfoDto> columnInfoDtoList;

    @FXML
    void columnNameEdit(TableColumn.CellEditEvent event) {
        updateColumnInfo(columnInfo.getSelectionModel().getSelectedIndex(), STR_1, event.getNewValue().toString());
    }

    @FXML
    void columnDictEdit(TableColumn.CellEditEvent event) {
        updateColumnInfo(columnInfo.getSelectionModel().getSelectedIndex(), STR_2, event.getNewValue().toString());
    }

    @FXML
    void columnMultiEdit(TableColumn.CellEditEvent event) {
        updateColumnInfo(columnInfo.getSelectionModel().getSelectedIndex(), STR_3, event.getNewValue().toString());
    }

    @FXML
    void columnDateEdit(TableColumn.CellEditEvent event) {
        updateColumnInfo(columnInfo.getSelectionModel().getSelectedIndex(), STR_4, event.getNewValue().toString());
    }

    @FXML
    void columnPrecisionEdit(TableColumn.CellEditEvent event) {
        updateColumnInfo(columnInfo.getSelectionModel().getSelectedIndex(), STR_5, event.getNewValue().toString());
    }

    @FXML
    void columnRequiredEdit(TableColumn.CellEditEvent event) {
        updateColumnInfo(columnInfo.getSelectionModel().getSelectedIndex(), STR_6, event.getNewValue().toString());
    }

    @FXML
    void columnDefaultEdit(TableColumn.CellEditEvent event) {
        updateColumnInfo(columnInfo.getSelectionModel().getSelectedIndex(), STR_7, event.getNewValue().toString());
    }

    @FXML
    void columnOrderEdit(TableColumn.CellEditEvent event) {
        updateColumnInfo(columnInfo.getSelectionModel().getSelectedIndex(), STR_8, event.getNewValue().toString());
    }

    @FXML
    void columnWidthEdit(TableColumn.CellEditEvent event) {
        updateColumnInfo(columnInfo.getSelectionModel().getSelectedIndex(), STR_9, event.getNewValue().toString());
    }

    private void updateColumnInfo(int rowNum, String updateType, String value) {
        for (int i=0; i<columnInfoDtoList.size(); i++) {
            ColumnInfoDto columnInfoDto = columnInfoDtoList.get(i);
            if (i == rowNum) {
                switch (updateType) {
                    case STR_1:
                        columnInfoDto.setColumnName(value);
                        break;
                    case STR_2:
                        columnInfoDto.setColumnDict(value);
                        break;
                    case STR_3:
                        columnInfoDto.setColumnMulti(value);
                        break;
                    case STR_4:
                        columnInfoDto.setColumnDate(value);
                        break;
                    case STR_5:
                        columnInfoDto.setColumnPrecision(value);
                        break;
                    case STR_6:
                        columnInfoDto.setColumnRequired(value);
                        break;
                    case STR_7:
                        columnInfoDto.setColumnDefault(value);
                        break;
                    case STR_8:
                        columnInfoDto.setColumnOrder(value);
                        break;
                    case STR_9:
                        columnInfoDto.setColumnWidth(value);
                        break;
                    default:
                        break;
                }
                break;
            }
        }
        if (STR_8.equals(updateType)) {
            OutputUtils.clearLog(columnInfo);
            Collections.sort(columnInfoDtoList);
            OutputUtils.infoList(columnInfo, columnInfoDtoList, false);
        }
    }

    @FXML
    void onSave(ActionEvent event) throws Exception {
        save.setDisable(true);
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            appConfigDto.getGenerateCodeDto().setColumn(columnInfoDtoList);
            Stage columnStage = appConfigDto.getColumnStage();
            columnStage.close();
            appConfigDto.setColumnStage(null);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
        save.setDisable(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            GenerateCodeDto generateCodeDto = appConfigDto.getGenerateCodeDto();
            if (CollectionUtils.isNotEmpty(generateCodeDto.getColumn())) {
                columnInfoDtoList = generateCodeDto.getColumn();
                OutputUtils.infoList(columnInfo, columnInfoDtoList, false);
            } else {
                columnInfoDtoList = new ArrayList<>();
                Map<String, ColumnInfoDto> tableColumn = generateCodeDto.getColumnMap();
                Iterator<String> iterator = tableColumn.keySet().iterator();
                while (iterator.hasNext()) {
                    String columnCode = iterator.next();
                    ColumnInfoDto columnInfoDto = tableColumn.get(columnCode);
                    OutputUtils.info(columnInfo, columnInfoDto, false);
                    columnInfoDtoList.add(columnInfoDto);
                }
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }
}
