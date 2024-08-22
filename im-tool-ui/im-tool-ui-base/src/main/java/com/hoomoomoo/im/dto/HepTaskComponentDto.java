package com.hoomoomoo.im.dto;

import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import lombok.Data;

import java.util.List;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.dto
 * @date 2023-07-30
 */

@Data
public class HepTaskComponentDto extends BaseDto{

    private List<String> logs;

    private Label dayTodo;

    private Label weekTodo;

    private Label waitHandleTaskNum;

    private Label dayPublish;

    private Label weekPublish;

    private Label dayClose;

    private Label weekClose;

    private Label todoNum;

    private TableView taskList;

    private TextArea notice;

}
