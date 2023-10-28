package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.*;

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

    private HashMap<String, String> MENU_TYPE_CACHE = new HashMap();
    private HashMap<String, String> MENU_FUNCTION_CACHE = new HashMap();
    private HashMap<String, String> MENU_DATASOURCE_CACHE = new HashMap();
    private HashMap<String, String> MENU_MULTIPLE_TABLE_CACHE = new HashMap();
    private HashMap<String, String> FIRST_MENU_CACHE = new HashMap();
    private HashMap<String, String> SECOND_MENU_CACHE = new HashMap();

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

    }

    @FXML
    void selectSecondMenu(ActionEvent event) {

    }

    @FXML
    void refreshMenu(ActionEvent event) {

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

    }

    @FXML
    void showAsyTableCode(ActionEvent event) {

    }

    @FXML
    void configColumn(ActionEvent event) {

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
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            ObservableList firstMenuItems = firstMenuBox.getItems();
            firstMenuItems.add("信息维护");
            firstMenuItems.add("查询库查询");
            firstMenuItems.add("查询分析");
            firstMenuItems.add("参数电子化");

            ObservableList secondMenuItems = secondMenuBox.getItems();
            secondMenuItems.add("分客户类型尾随佣金设置");
            secondMenuItems.add("尾随佣金指定客户类型设置");
            secondMenuItems.add("自有资产占比监控设置");
            secondMenuItems.add("特殊投资者不受个户交易限制");

            ObservableList menuTypeItems = menuTypeBox.getItems();
            menuTypeItems.add("自建业务");
            menuTypeItems.add("账户中心");

            ObservableList menuFunctionItems = menuFunctionBox.getItems();
            menuFunctionItems.add("方案设置");
            menuFunctionItems.add("数据查询");

            ObservableList menuDataSourceItems = menuDataSourceBox.getItems();
            menuDataSourceItems.add("主库");
            menuDataSourceItems.add("查询库");
            menuDataSourceItems.add("sharding");

            ObservableList multipleTableItems = multipleTableBox.getItems();
            multipleTableItems.add("是");
            multipleTableItems.add("否");

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

}
