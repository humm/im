package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.ColumnInfoDto;
import com.hoomoomoo.im.dto.GenerateCodeDto;
import com.hoomoomoo.im.service.GenerateCommon;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.LazyMap;
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
public class ColumnSetController implements Initializable {

    @FXML
    private Button save;

    @FXML
    private TableView columnInfo;

    @FXML
    private Label tips;

    private List<ColumnInfoDto> columnInfoDtoList  = new ArrayList<>();

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
    void columnQueryStat(TableColumn.CellEditEvent event) {
        updateColumnInfo(columnInfo.getSelectionModel().getSelectedIndex(), STR_15, event.getNewValue().toString());
    }

    private void updateColumnInfo(int rowNum, String updateType, String value) {
        if (StringUtils.isNotBlank(value)) {
            value = value.trim();
        }
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
                    case STR_10:
                        columnInfoDto.setColumnQuery(value);
                        break;
                    case STR_11:
                        columnInfoDto.setColumnUpdate(value);
                        break;
                    case STR_12:
                        columnInfoDto.setColumnQueryOrder(value);
                        break;
                    case STR_13:
                        columnInfoDto.setColumnQueryOrderType(value);
                    case STR_14:
                        columnInfoDto.setColumnBatchUpdate(value);
                        break;
                    case STR_15:
                        columnInfoDto.setColumnQueryStat(value);
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
        try {
            OutputUtils.clearLog(tips);
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            List<String> errorInfo = new ArrayList();
            Map<String, String> queryOrder = new HashMap<>(16);
            for (ColumnInfoDto columnInfoDto : columnInfoDtoList) {
                String columnUnderline = columnInfoDto.getColumnUnderline();
                if (!CommonUtils.isNumber(columnInfoDto.getColumnWidth())) {
                    errorInfo.add(columnUnderline + " 显示宽度只能为整数");
                    break;
                }
                if (!CommonUtils.isNumber(columnInfoDto.getColumnPrecision())) {
                    errorInfo.add(columnUnderline + " 小数位数只能为整数");
                    break;
                }
                if (!CommonUtils.isNumber(columnInfoDto.getColumnOrder())) {
                    errorInfo.add(columnUnderline + " 显示顺序只能为整数");
                    break;
                }
                String columnQueryOrder = columnInfoDto.getColumnQueryOrder();
                if (!CommonUtils.isNumber(columnQueryOrder)) {
                    errorInfo.add(columnUnderline + " 查询排序号只能为整数");
                    break;
                }
                if (queryOrder.containsKey(columnQueryOrder)) {
                    errorInfo.add("查询排序号【" + columnQueryOrder + "】不能重复");
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
            columnInfoDtoList = generateCodeDto.getColumn();
            if (CollectionUtils.isEmpty(columnInfoDtoList)) {
                columnInfoDtoList = new ArrayList<>();
            }
            Map<String, ColumnInfoDto> tableColumn = generateCodeDto.getColumnMap();
            Iterator<String> iterator = tableColumn.keySet().iterator();
            while (iterator.hasNext()) {
                String columnCode = iterator.next();
                ColumnInfoDto columnInfoDto = tableColumn.get(columnCode);
                if (GenerateCommon.skipColumn(columnInfoDto, false)) {
                    continue;
                }
                OutputUtils.info(columnInfo, columnInfoDto, false);
                if (isAppend(columnCode)) {
                    columnInfoDtoList.add(columnInfoDto);
                }
            }
            Iterator<ColumnInfoDto> columnIterator = columnInfoDtoList.listIterator();
            while (columnIterator.hasNext()) {
                if (tableColumn.get(columnIterator.next().getColumnCode()) == null) {
                    columnIterator.remove();
                }
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    private boolean isAppend(String columnCode) {
        boolean append = true;
        for (ColumnInfoDto item : columnInfoDtoList) {
            if (columnCode.equals(item.getColumnCode())) {
                append = false;
                break;
            }
        }
        return append;
    }

}
