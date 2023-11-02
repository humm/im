package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.ColumnDto;
import com.hoomoomoo.im.utils.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.GENERATE_CODE;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/10/15
 */
public class GenerateCodeController extends BaseController implements Initializable {

    @FXML
    private AnchorPane generateCode;

    @FXML
    private TextArea log;

    @FXML
    private ComboBox firstMenuBox;

    @FXML
    private ComboBox secondMenuBox;

    @FXML
    private TextField thirdMenuCode;

    @FXML
    private TextField thirdMenuName;

    @FXML
    private TextField author;

    @FXML
    private TextField menuOrder;

    @FXML
    private TextField entityCode;

    @FXML
    private TextField tableCode;

    @FXML
    private TextField asyTableCode;

    @FXML
    private ComboBox menuTypeBox;

    @FXML
    private ComboBox menuFunctionBox;

    @FXML
    private ComboBox menuDataSourceBox;

    @FXML
    private ComboBox multipleTableBox;

    @FXML
    private ComboBox hisRecordBox;

    @FXML
    private Button executeBtn;

    @FXML
    private Button showTableCodeBtn;

    @FXML
    private Button showAsyTableCodeBtn;

    @FXML
    private Button refreshMenuBtn;

    private HashMap<String, String> MENU_TYPE_CACHE = new HashMap();
    private HashMap<String, String> MENU_FUNCTION_CACHE = new HashMap();
    private HashMap<String, String> MENU_DATASOURCE_CACHE = new HashMap();
    private HashMap<String, String> MENU_MULTIPLE_TABLE_CACHE = new HashMap();
    private LinkedHashMap<String, Map<String, Map<String, Map<String, String>>>> MENU_CACHE = new LinkedHashMap<>();

    {
        MENU_TYPE_CACHE.put(MENU_TYPE_FUND_CODE, MENU_TYPE_FUND_NAME);
        MENU_TYPE_CACHE.put(MENU_TYPE_ACCOUNT_CODE, MENU_TYPE_ACCOUNT_NAME);
        MENU_TYPE_CACHE.put(MENU_TYPE_FUND_NAME, MENU_TYPE_FUND_CODE);
        MENU_TYPE_CACHE.put(MENU_TYPE_ACCOUNT_NAME, MENU_TYPE_FUND_CODE);

        MENU_FUNCTION_CACHE.put(MENU_FUNCTION_SET_CODE, MENU_FUNCTION_SET_NAME);
        MENU_FUNCTION_CACHE.put(MENU_FUNCTION_QUERY_CODE, MENU_FUNCTION_QUERY_NAME);
        MENU_FUNCTION_CACHE.put(MENU_FUNCTION_SET_NAME, MENU_FUNCTION_SET_CODE);
        MENU_FUNCTION_CACHE.put(MENU_FUNCTION_QUERY_NAME, MENU_FUNCTION_QUERY_CODE);

        MENU_DATASOURCE_CACHE.put(MENU_DATASOURCE_PUB_CODE, MENU_DATASOURCE_PUB_NAME);
        MENU_DATASOURCE_CACHE.put(MENU_DATASOURCE_QUERY_CODE, MENU_DATASOURCE_QUERY_NAME);
        MENU_DATASOURCE_CACHE.put(MENU_DATASOURCE_SHARDING_CODE, MENU_DATASOURCE_SHARDING_NAME);
        MENU_DATASOURCE_CACHE.put(MENU_DATASOURCE_PUB_NAME, MENU_DATASOURCE_PUB_CODE);
        MENU_DATASOURCE_CACHE.put(MENU_DATASOURCE_QUERY_NAME, MENU_DATASOURCE_QUERY_CODE);
        MENU_DATASOURCE_CACHE.put(MENU_DATASOURCE_SHARDING_NAME, MENU_DATASOURCE_SHARDING_CODE);

        MENU_MULTIPLE_TABLE_CACHE.put(MENU_MULTIPLE_TABLE_YES_CODE, MENU_MULTIPLE_TABLE_YES_NAME);
        MENU_MULTIPLE_TABLE_CACHE.put(MENU_MULTIPLE_TABLE_NO_CODE, MENU_MULTIPLE_TABLE_NO_NAME);
        MENU_MULTIPLE_TABLE_CACHE.put(MENU_MULTIPLE_TABLE_YES_NAME, MENU_MULTIPLE_TABLE_YES_CODE);
        MENU_MULTIPLE_TABLE_CACHE.put(MENU_MULTIPLE_TABLE_NO_NAME, MENU_MULTIPLE_TABLE_NO_CODE);

    }

    @FXML
    void selectFirstMenu(ActionEvent event) {
        String menuName = InputUtils.getComboBoxValue(event);
        if (MapUtils.isEmpty(MENU_CACHE)) {
            return;
        }
        Map<String, String> subMenu = MENU_CACHE.get(KEY_MENU_NAME).get(menuName).get(KEY_MENU_SUB);
        Iterator<String> iterator = subMenu.keySet().iterator();
        ObservableList secondMenuBoxItems = secondMenuBox.getItems();
        secondMenuBoxItems.clear();
        while (iterator.hasNext()) {
            secondMenuBoxItems.add(iterator.next());
        }
    }

    @FXML
    void selectSecondMenu(ActionEvent event) {

    }

    @FXML
    void refreshMenu(ActionEvent event) throws Exception {
        initMenuCache();
    }

    @FXML
    void selectMenuType(ActionEvent event) throws Exception {

    }

    @FXML
    void selectMenuFunction(ActionEvent event) throws Exception {

    }

    @FXML
    void selectMenuDataSource(ActionEvent event) throws Exception {

    }

    @FXML
    void selectMultipleTable(ActionEvent event) throws Exception {

    }

    @FXML
    void showTableCode(ActionEvent event) {
        showTableCodeBtn.setDisable(true);
        try {
            String tableName = InputUtils.getComponentValue(tableCode);
            if (StringUtils.isBlank(tableName)) {
                OutputUtils.clearLog(log);
                OutputUtils.info(log, String.format(MSG_GENERATE_CODE_TIPS, "正表代码"));
                return;
            }
            List<ColumnDto> columnInfo = getConfigTableColumnInfo(tableName);
            String pkName = getConfigTablePkInfo(tableName);
            showTableInfo(tableName, pkName, columnInfo);
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        } finally {
            showTableCodeBtn.setDisable(false);
        }
    }

    @FXML
    void showAsyTableCode(ActionEvent event) {
        showAsyTableCodeBtn.setDisable(true);
        try {
            String tableName = InputUtils.getComponentValue(asyTableCode);
            if (StringUtils.isBlank(tableName)) {
                OutputUtils.clearLog(log);
                OutputUtils.info(log, String.format(MSG_GENERATE_CODE_TIPS, "复核表代码"));
                return;
            }
            List<ColumnDto> columnInfo = getConfigTableColumnInfo(tableName);
            String pkName = getConfigTablePkInfo(tableName);
            showTableInfo(tableName, pkName, columnInfo);
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        } finally {
            showAsyTableCodeBtn.setDisable(false);
        }
    }

    @FXML
    void configColumn(ActionEvent event) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            Stage stage = appConfigDto.getChildStage();
            if (stage != null) {
                stage.close();
                appConfigDto.setChildStage(null);
                stage = null;
            }

            if (stage == null) {
                Parent root = new FXMLLoader().load(new FileInputStream(FileUtils.getFilePath(PATH_COLUMN_SET_FXML)));
                Scene scene = new Scene(root);
                scene.getStylesheets().add(FileUtils.getFileUrl(PATH_STARTER_CSS).toExternalForm());
                stage = new Stage();
                stage.getIcons().add(new javafx.scene.image.Image(PATH_ICON));
                stage.setScene(scene);
                stage.setTitle(NAME_CONFIG_COLUMN);
                stage.setResizable(false);
                stage.show();
                appConfigDto.setChildStage(stage);
                stage.setOnCloseRequest(columnEvent -> {
                    appConfigDto.getChildStage().close();
                    appConfigDto.setChildStage(null);
                });
            } else {
                stage.toFront();
            }

        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        }
    }

    @FXML
    void selectHisRecord(ActionEvent event) {

    }

    @FXML
    void execute(ActionEvent event) {
        try {
            OutputUtils.clearLog(log);
            LoggerUtils.info(String.format(BaseConst.MSG_USE, GENERATE_CODE.getName()));
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            buildInputInfo();
            if (!TaCommonUtils.checkConfigGenerateCode(log, appConfigDto)) {
                return;
            }
            setProgress(0);
            updateProgress();
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initMenuCache();
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();

            ObservableList menuTypeItems = menuTypeBox.getItems();
            menuTypeItems.add(MENU_TYPE_FUND_NAME);
            menuTypeItems.add(MENU_TYPE_ACCOUNT_NAME);

            ObservableList menuFunctionItems = menuFunctionBox.getItems();
            menuFunctionItems.add(MENU_FUNCTION_SET_NAME);
            menuFunctionItems.add(MENU_FUNCTION_QUERY_NAME);

            ObservableList menuDataSourceItems = menuDataSourceBox.getItems();
            menuDataSourceItems.add(MENU_DATASOURCE_PUB_NAME);
            menuDataSourceItems.add(MENU_DATASOURCE_QUERY_NAME);
            menuDataSourceItems.add(MENU_DATASOURCE_SHARDING_NAME);

            ObservableList multipleTableItems = multipleTableBox.getItems();
            multipleTableItems.add(MENU_MULTIPLE_TABLE_YES_NAME);
            multipleTableItems.add(MENU_MULTIPLE_TABLE_NO_NAME);

            String menuType = appConfigDto.getGenerateCodeMenuType();
            if (StringUtils.isNotBlank(menuType)) {
                OutputUtils.info(menuTypeBox, MENU_TYPE_CACHE.get(menuType));
            }

            String menuFunction = appConfigDto.getGenerateCodeMenuFunction();
            if (StringUtils.isNotBlank(menuFunction)) {
                OutputUtils.info(menuFunctionBox, MENU_FUNCTION_CACHE.get(menuFunction));
            }

            String menuDataSource = appConfigDto.getGenerateCodeMenuDataSource();
            if (StringUtils.isNotBlank(menuDataSource)) {
                OutputUtils.info(menuDataSourceBox, MENU_DATASOURCE_CACHE.get(menuDataSource));
            }

            String menuMultipleTable = appConfigDto.getGenerateCodeMenuMultipleTable();
            if (StringUtils.isNotBlank(menuMultipleTable)) {
                OutputUtils.info(multipleTableBox, MENU_MULTIPLE_TABLE_CACHE.get(menuMultipleTable));
            }

            String menuAuthor = appConfigDto.getGenerateCodeMenuAuthor();
            if (StringUtils.isNotBlank(menuAuthor)) {
                OutputUtils.info(author, menuAuthor);
            }

        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        }
    }

    private void buildInputInfo() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String firstMenuName = InputUtils.getComponentValue(firstMenuBox);
        String secondMenuName = InputUtils.getComponentValue(secondMenuBox);
        appConfigDto.setFirstMenuCode(firstMenuName);
        appConfigDto.setFirstMenuName(firstMenuName);
        appConfigDto.setSecondMenuCode(secondMenuName);
        appConfigDto.setSecondMenuName(secondMenuName);
        appConfigDto.setThirdMenuCode(InputUtils.getComponentValue(thirdMenuCode));
        appConfigDto.setThirdMenuName(InputUtils.getComponentValue(thirdMenuName));
        appConfigDto.setGenerateCodeMenuType(MENU_TYPE_CACHE.get(InputUtils.getComponentValue(menuTypeBox)));
        appConfigDto.setGenerateCodeMenuFunction(MENU_FUNCTION_CACHE.get(InputUtils.getComponentValue(menuFunctionBox)));
        appConfigDto.setGenerateCodeMenuDataSource(MENU_DATASOURCE_CACHE.get(InputUtils.getComponentValue(menuDataSourceBox)));
        appConfigDto.setMenuOrder(InputUtils.getComponentValue(menuOrder));
        appConfigDto.setEntityCode(InputUtils.getComponentValue(entityCode));
        appConfigDto.setGenerateCodeMenuAuthor(InputUtils.getComponentValue(author));
        appConfigDto.setTableCode(InputUtils.getComponentValue(tableCode));
        appConfigDto.setAsyTableCode(InputUtils.getComponentValue(asyTableCode));
        appConfigDto.setGenerateCodeMenuMultipleTable(MENU_MULTIPLE_TABLE_CACHE.get(InputUtils.getComponentValue(multipleTableBox)));
    }

    private void initMenuCache() {
        MENU_CACHE.clear();
        firstMenuBox.getItems().clear();
        secondMenuBox.getItems().clear();
        refreshMenuBtn.setDisable(true);
        String sql = "select * from tsys_menu where trans_code = 'menu' and (parent_code = 'console-fund-ta-vue' or parent_code in ('query' , ' param' , 'busin')) order by order_no";
        try {
            List<Map<String, String>> firstMenu = DatabaseUtils.executeQuery(sql);
            if (CollectionUtils.isNotEmpty(firstMenu)) {
                MENU_CACHE.put(KEY_MENU_CODE, new LinkedHashMap<>());
                MENU_CACHE.put(KEY_MENU_NAME, new LinkedHashMap<>());

                for (Map<String, String> item : firstMenu) {
                    String menuCode = item.get(KEY_MENU_CODE);
                    String menuName = item.get(KEY_MENU_NAME);

                    Map<String, Map<String, Map<String, String>>> menuCodeMap = MENU_CACHE.get(KEY_MENU_CODE);
                    Map<String, Map<String, String>> menuCodeItem = new LinkedHashMap<>();
                    menuCodeMap.put(menuCode, menuCodeItem);
                    Map<String, String> selfCode = new LinkedHashMap<>();
                    Map<String, String> subCode = new LinkedHashMap<>();
                    selfCode.put(menuCode, menuName);
                    menuCodeItem.put(KEY_MENU_CODE, selfCode);
                    menuCodeItem.put(KEY_MENU_SUB, subCode);

                    Map<String, Map<String, Map<String, String>>> menuNameMap = MENU_CACHE.get(KEY_MENU_NAME);
                    Map<String, Map<String, String>> menuNameItem = new LinkedHashMap<>();
                    menuNameMap.put(menuName, menuNameItem);
                    Map<String, String> selfName = new LinkedHashMap<>();
                    Map<String, String> subName = new LinkedHashMap<>();
                    selfName.put(menuName, menuCode);
                    menuNameItem.put(KEY_MENU_CODE, selfName);
                    menuNameItem.put(KEY_MENU_SUB, subName);

                    initSubMenuCache(menuCode, menuName);
                }
            }
            ObservableList firstMenuItems = firstMenuBox.getItems();
            Iterator<String> iterator = MENU_CACHE.get(KEY_MENU_NAME).keySet().iterator();
            while (iterator.hasNext()) {
                firstMenuItems.add(iterator.next());
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        } finally {
            refreshMenuBtn.setDisable(false);
        }
    }

    private void initSubMenuCache(String parentCode, String parentName) throws Exception {
        String sql = "select * from tsys_menu where parent_code = '" + parentCode + "'";
        List<Map<String, String>> secondMenu = DatabaseUtils.executeQuery(sql);
        if (CollectionUtils.isNotEmpty(secondMenu)) {
            for (Map<String, String> item : secondMenu) {
                String menuCode = item.get(KEY_MENU_CODE);
                String menuName = item.get(KEY_MENU_NAME);
                Map<String, Map<String, Map<String, String>>> menuCodeItem = MENU_CACHE.get(KEY_MENU_CODE);
                if (MapUtils.isNotEmpty(menuCodeItem)) {
                    Map<String, Map<String, String>> menu = menuCodeItem.get(parentCode);
                    Map<String, String> subCode = menu.get(KEY_MENU_SUB);
                    subCode.put(menuCode, menuName);
                }
                Map<String, Map<String, Map<String, String>>> menuNameItem = MENU_CACHE.get(KEY_MENU_NAME);
                if (MapUtils.isNotEmpty(menuNameItem)) {
                    Map<String, Map<String, String>> menu = menuNameItem.get(parentName);
                    Map<String, String> subCode = menu.get(KEY_MENU_SUB);
                    subCode.put(menuName, menuCode);
                }
            }
        }
    }

    private List<ColumnDto> getConfigTableColumnInfo(String tableName) throws Exception {
        String sql = "select * from user_tab_columns where table_name = upper('" + tableName + "') order by column_id";
        List<ColumnDto> result = new ArrayList<>();
        List<Map<String, String>> columnInfo = DatabaseUtils.executeQuery(sql);
        if (CollectionUtils.isNotEmpty(columnInfo)) {
            for (Map<String, String> item : columnInfo) {
                ColumnDto columnDto = new ColumnDto();
                columnDto.setColumnCode(item.get(KEY_COLUMN_NAME).toLowerCase());
                columnDto.setColumnDataType(item.get(KEY_DATA_TYPE).toLowerCase());
                String dataPrecision = item.get(KEY_DATA_PRECISION);
                if (StringUtils.isBlank(dataPrecision)) {
                    dataPrecision = item.get(KEY_DATA_LENGTH);
                }
                columnDto.setColumnDataPrecision(dataPrecision);
                String dataScale = item.get(KEY_DATA_SCALE);
                if (StringUtils.isNotBlank(dataScale)) {
                    columnDto.setColumnDataScale(dataScale);
                } else {
                    columnDto.setColumnDataScale(STR_BLANK);
                }
                String dataDefault = item.get(KEY_DATA_DEFAULT);
                if (StringUtils.isNotBlank(dataDefault)) {
                    columnDto.setColumnDataDefault(dataDefault.toLowerCase().trim());
                } else {
                    columnDto.setColumnDataDefault(STR_BLANK);
                }
                result.add(columnDto);
            }
        }
        return result;
    }

    private String getConfigTablePkInfo(String tableName) {
        String sql = "select lower(uc.constraint_name) pk_name, lower(uic.column_name) column_name from user_ind_columns uic, user_constraints uc " +
                "where uc.constraint_type = 'P' " +
                "and uc.table_name = uic.table_name " +
                "and uc.table_name = upper('" + tableName + "') ";
        String pk = STR_BLANK;
        try {
            List<Map<String, String>> columnInfo = DatabaseUtils.executeQuery(sql);
            if (CollectionUtils.isNotEmpty(columnInfo)) {
                for (int i=0; i<columnInfo.size(); i++) {
                    Map<String, String> item = columnInfo.get(i);
                    if (i == 0) {
                        pk += "constraint " + item.get(KEY_PK_NAME) + " primary key (";
                    }
                    pk += item.get(KEY_COLUMN_NAME);
                    if (i != columnInfo.size() - 1) {
                        pk += STR_COMMA + STR_SPACE;
                    } else {
                        pk += ")";
                    }
                }
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.getMessage());
        }
        return pk;
    }

    private void showTableInfo(String tableName, String pkName, List<ColumnDto> columnInfo) {
        if (CollectionUtils.isEmpty(columnInfo)) {
            OutputUtils.clearLog(log);
            OutputUtils.info(log, String.format("数据表" + MSG_TIPS + "不存在，请先建表", tableName));
            return;
        }
        String table = "create table " + tableName + "(" + STR_NEXT_LINE;
        boolean appendComma = true;
        for (int i=0; i<columnInfo.size(); i++) {
            ColumnDto columnDto = columnInfo.get(i);
            if (StringUtils.isBlank(pkName)) {
                if (i == columnInfo.size() - 1) {
                    appendComma = false;
                }
            }
            table += buildColumn(columnDto, appendComma);
        }
        if (StringUtils.isNotBlank(pkName)) {
            table += STR_SPACE_4 + pkName + STR_NEXT_LINE;
        }
        table += ")" + STR_NEXT_LINE;
        OutputUtils.clearLog(log);
        OutputUtils.info(log, table);
    }

    private String buildColumn(ColumnDto column, boolean appendComma) {
        String item = STR_SPACE_4;
        String columnCode = column.getColumnCode();
        item += columnCode;
        item += appendSpace(30 - STR_SPACE_4.length() - columnCode.length());
        String dataType = column.getColumnDataType();
        switch (dataType) {
            case COLUMN_TYPE_VARCHAR2:
                dataType += "(" + column.getColumnDataPrecision() + ")";
                break;
            case COLUMN_TYPE_NUMBER:
                if (STR_0.equals(column.getColumnDataScale())) {
                    dataType = COLUMN_TYPE_INTEGER;
                } else {
                    dataType += "(" + column.getColumnDataPrecision() + "," + column.getColumnDataScale() + ")";
                }
                break;
        }
        item += dataType;
        item += appendSpace(20 - dataType.length());
        item += "default " + column.getColumnDataDefault() + " not null";
        if (appendComma) {
            item += STR_COMMA;
        }
        return item + STR_NEXT_LINE;
    }

    private String appendSpace(int len) {
        String space = STR_BLANK;
        for (int i=0; i<len; i++) {
            space += STR_SPACE;
        }
        return space;
    }
}
