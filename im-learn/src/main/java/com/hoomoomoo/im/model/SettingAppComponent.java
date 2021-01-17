package com.hoomoomoo.im.model;

import lombok.Data;

import javax.swing.*;

/**
 * @author
 * @description TODO
 * @package com.hoomoomoo.im.model
 * @date 2020/12/18
 */

@Data
public class SettingAppComponent {

    private JComboBox svnUpdate;

    private JTextField svnUsername;

    private JPasswordField svnPassword;

    private JTextField svnFileName;

    private JTextField fileSuffix;

    private JComboBox updateDeleteSuccess;

    private JTextField lineContentOffset;

    private JTextField lineSpecialContentOffset;

    private JTextField mergerFileName;

    private JComboBox fileEncoding;
}
