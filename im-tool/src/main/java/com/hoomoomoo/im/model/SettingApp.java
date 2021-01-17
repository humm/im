package com.hoomoomoo.im.model;

import lombok.Data;

/**
 * @author
 * @description TODO
 * @package com.hoomoomoo.im.model
 * @date 2020/12/18
 */

@Data
public class SettingApp {

    private String svnUpdate;

    private String svnUsername;

    private String svnPassword;

    private String svnFileName;

    private String fileSuffix;

    private String updateDeleteSuccess;

    private String lineContentOffset;

    private String lineSpecialContentOffset;

    private String mergerFileName;

    private String fileEncoding;
}
