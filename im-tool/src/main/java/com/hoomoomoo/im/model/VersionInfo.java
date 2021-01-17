package com.hoomoomoo.im.model;

import lombok.Data;

/**
 * @author
 * @description 版本号实体类
 * @package com.hoomoomoo.im.model
 * @date 2020/12/14
 */

@Data
public class VersionInfo {

    private String mode;

    private String versionName;

    private String statusFile;

    private String sourcePath;

    private String targetPath;
}
