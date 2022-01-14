package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.dto.GoodsDto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.util.List;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2022/1/13
 */
public class ShoppingBaseController extends BaseController{

    @FXML
    public Label orderNum;

    @FXML
    public Label userName;

    @FXML
    public TableView<?> orderGoodsList;

    @FXML
    public TableView<?> log;

    @FXML
    public Button execute;

    @FXML
    public Button query;

    public List<GoodsDto> goodsDtoList;

    public int orderNumValue;
}
