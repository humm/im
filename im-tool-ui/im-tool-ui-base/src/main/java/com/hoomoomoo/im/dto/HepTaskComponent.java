package com.hoomoomoo.im.dto;

import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import lombok.Data;

import java.util.List;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.dto
 * @date 2023-07-30
 */

@Data
public class HepTaskComponent extends BaseDto{

    private List<String> logs;

    private Label waitHandleTaskNum;

    private Label todoNum;

    private TableView taskList;

}
