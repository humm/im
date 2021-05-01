package com.hoomoomoo.im.dto;

import lombok.Data;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.dto
 * @date 2021/04/24
 */

@Data
public class AppConfigDto extends BaseDto {

    private String appName;

    private String showTab;

    private String svnUrl;

    private String svnUsername;

    private String svnPassword;

    private String svnMaxRevision;

    private String svnRecentTime;

    private String svnDeletePrefix;

    private Boolean enableLog;

}
