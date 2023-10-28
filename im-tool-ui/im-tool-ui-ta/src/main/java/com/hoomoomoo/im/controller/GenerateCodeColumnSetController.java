package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.StringConverter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.*;

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
    private TableColumn columnMulti;

    @FXML
    private TableColumn columnMultiSingle;


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

    @FXML
    void columnQueryEdit(TableColumn.CellEditEvent event) {
        updateColumnInfo(columnInfo.getSelectionModel().getSelectedIndex(), STR_10, event.getNewValue().toString());
    }

    @FXML
    void columnUpdateEdit(TableColumn.CellEditEvent event) {
        updateColumnInfo(columnInfo.getSelectionModel().getSelectedIndex(), STR_11, event.getNewValue().toString());
    }

    @FXML
    void columnQueryOrderEdit(TableColumn.CellEditEvent event) {
        updateColumnInfo(columnInfo.getSelectionModel().getSelectedIndex(), STR_12, event.getNewValue().toString());
    }

    @FXML
    void columnQueryOrderTypeEdit(TableColumn.CellEditEvent event) {
        updateColumnInfo(columnInfo.getSelectionModel().getSelectedIndex(), STR_13, event.getNewValue().toString());
    }

    @FXML
    void columnBatchUpdateEdit(TableColumn.CellEditEvent event) {
        updateColumnInfo(columnInfo.getSelectionModel().getSelectedIndex(), STR_14, event.getNewValue().toString());
    }

    @FXML
    void columnQueryStatEdit(TableColumn.CellEditEvent event) {
        updateColumnInfo(columnInfo.getSelectionModel().getSelectedIndex(), STR_15, event.getNewValue().toString());
    }

    @FXML
    void columnMultiSingleEdit(TableColumn.CellEditEvent event) {
        updateColumnInfo(columnInfo.getSelectionModel().getSelectedIndex(), STR_16, event.getNewValue().toString());
    }

    private void updateColumnInfo(int rowNum, String updateType, String value) {
        if (StringUtils.isNotBlank(value)) {
            value = value.trim();
        }
        /*for (int i = 0; i< generateColumnInfoDtoList.size(); i++) {
            GenerateColumnInfoDto generateColumnInfoDto = generateColumnInfoDtoList.get(i);
            if (i == rowNum) {
                switch (updateType) {
                    case STR_1:
                        generateColumnInfoDto.setColumnName(value);
                        break;
                    case STR_2:
                        generateColumnInfoDto.setColumnDict(value);
                        break;
                    case STR_3:
                        generateColumnInfoDto.setColumnMulti(value);
                        break;
                    case STR_4:
                        generateColumnInfoDto.setColumnDate(value);
                        break;
                    case STR_5:
                        generateColumnInfoDto.setColumnPrecision(value);
                        break;
                    case STR_6:
                        generateColumnInfoDto.setColumnRequired(value);
                        break;
                    case STR_7:
                        generateColumnInfoDto.setColumnDefault(value);
                        break;
                    case STR_8:
                        generateColumnInfoDto.setColumnOrder(value);
                        break;
                    case STR_9:
                        generateColumnInfoDto.setColumnWidth(value);
                        break;
                    case STR_10:
                        generateColumnInfoDto.setColumnQuery(value);
                        break;
                    case STR_11:
                        generateColumnInfoDto.setColumnUpdate(value);
                        break;
                    case STR_12:
                        generateColumnInfoDto.setColumnQueryOrder(value);
                        break;
                    case STR_13:
                        generateColumnInfoDto.setColumnQueryOrderType(value);
                    case STR_14:
                        generateColumnInfoDto.setColumnBatchUpdate(value);
                        break;
                    case STR_15:
                        generateColumnInfoDto.setColumnQueryStat(value);
                        break;
                    case STR_16:
                        generateColumnInfoDto.setColumnMultiSingle(value);
                        break;
                    default:
                        break;
                }
                break;
            }
        }
        if (STR_8.equals(updateType)) {
            Collections.sort(generateColumnInfoDtoList);
        }
        OutputUtils.clearLog(columnInfo);
        OutputUtils.infoList(columnInfo, generateColumnInfoDtoList, false);*/
    }

    @FXML
    void onSave(ActionEvent event) throws Exception {
        /*try {
            OutputUtils.clearLog(tips);
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            List<String> errorInfo = new ArrayList();
            Map<String, String> queryOrder = new HashMap<>(16);
            for (GenerateColumnInfoDto generateColumnInfoDto : generateColumnInfoDtoList) {
                String columnUnderline = generateColumnInfoDto.getColumnUnderline();
                if (StringUtils.isBlank(generateColumnInfoDto.getColumnName())) {
                    errorInfo.add(columnUnderline + " 名称不能为空");
                    break;
                }
                if (!CommonUtils.isNumber(generateColumnInfoDto.getColumnWidth())) {
                    errorInfo.add(columnUnderline + " 显示宽度只能为整数");
                    break;
                }
                if (!CommonUtils.isNumber(generateColumnInfoDto.getColumnPrecision())) {
                    errorInfo.add(columnUnderline + " 小数位数只能为整数");
                    break;
                }
                if (!CommonUtils.isNumber(generateColumnInfoDto.getColumnOrder())) {
                    errorInfo.add(columnUnderline + " 显示顺序只能为整数");
                    break;
                }
                String columnQueryOrder = generateColumnInfoDto.getColumnQueryOrder();
                if (!CommonUtils.isNumber(columnQueryOrder)) {
                    errorInfo.add(columnUnderline + " 查询排序只能为整数");
                    break;
                }
                if (queryOrder.containsKey(columnQueryOrder)) {
                    errorInfo.add("查询排序【" + columnQueryOrder + "】不能重复");
                    break;
                }
                if (StringUtils.isNotBlank(columnQueryOrder)) {
                    queryOrder.put(columnQueryOrder, columnQueryOrder);
                }
            }
            if (CollectionUtils.isNotEmpty(errorInfo)) {
                OutputUtils.info(tips, errorInfo.get(0));
                return;
            }
            save.setDisable(true);
            appConfigDto.getGenerateCodeDto().setColumn(generateColumnInfoDtoList);

        } catch (Exception e) {
            LoggerUtils.info(e);
        }
        save.setDisable(false);*/
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       /* try {
            ObservableList<String> options = FXCollections.observableArrayList(
                    "1",
                    "2",
                    "3"
            );
            final ComboBox comboBox = new ComboBox(options);
            columnMulti.setCellFactory(t -> {
                ComboBoxTableCell comboCell = new ComboBoxTableCell(comboBox);
                return comboCell;
            });


            columnMulti.setCellFactory(ComboBoxTableCell.forTableColumn(new StringConverter<String>()
            {
                @Override
                public String fromString(String string)
                {
                    return string;
                }

                @Override
                public String toString(String str)
                {
                    return str;
                }
            },"选项1","选项2","选项3"));

            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            GenerateCodeDto generateCodeDto = appConfigDto.getGenerateCodeDto();
            generateColumnInfoDtoList = generateCodeDto.getColumn();
            if (CollectionUtils.isEmpty(generateColumnInfoDtoList)) {
                generateColumnInfoDtoList = new ArrayList<>();
            }
            Map<String, GenerateColumnInfoDto> tableColumn = generateCodeDto.getColumnMap();
            Iterator<String> iterator = tableColumn.keySet().iterator();
            while (iterator.hasNext()) {
                String columnCode = iterator.next();
                GenerateColumnInfoDto generateColumnInfoDto = tableColumn.get(columnCode);
                OutputUtils.info(columnInfo, generateColumnInfoDto, false);
                if (isAppend(columnCode)) {
                    generateColumnInfoDtoList.add(generateColumnInfoDto);
                }
            }
            Iterator<GenerateColumnInfoDto> columnIterator = generateColumnInfoDtoList.listIterator();
            while (columnIterator.hasNext()) {
                if (tableColumn.get(columnIterator.next().getColumnCode()) == null) {
                    columnIterator.remove();
                }
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
        }*/
    }

    /*private boolean isAppend(String columnCode) {
        boolean append = true;
        for (GenerateColumnInfoDto item : generateColumnInfoDtoList) {
            if (columnCode.equals(item.getColumnCode())) {
                append = false;
                break;
            }
        }
        return append;
    }*/

}
