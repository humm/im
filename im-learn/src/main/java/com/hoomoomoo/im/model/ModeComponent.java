package com.hoomoomoo.im.model;

import lombok.Data;

import javax.swing.*;
import java.awt.*;

/**
 * @author
 * @description TODO
 * @package com.hoomoomoo.im.model
 * @date 2020/12/03
 */

@Data
public class ModeComponent {

    private String mode;

    private JPanel version;

    private JTextField sourcePath;

    private JTextField targetPath;

    private TextArea schedule;

    private JLabel result;

    private JLabel readNum;

    private JLabel successNum;

    private JLabel failNum;
}
