package com.hoomoomoo.im.dto;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.dto
 * @date 2021/04/24
 */

@Data
public class AppConfigDto extends BaseDto {

    private String appName;

    private String appTabShow;

    private String svnUrl;

    private String svnUsername;

    private String svnPassword;

    private String svnMaxRevision;

    private String svnRecentTime;

    private String svnDeletePrefix;

    private Boolean appLogEnable;

    private String fundExcelPath;

    private String fundGenerateMode;

    private String fundGeneratePath;

    private String processExcelPath;

    private String processGeneratePathTrans;

    private String processGeneratePathSchedule;

    private String processTaCode;

    private Boolean svnDefaultAppendBiz;

    private String svnDefaultAppendPath;

    private List<LinkedHashMap<String, String>> svnUpdatePath;

    private List<LinkedHashMap<String, List<String>>> scriptUpdateTable;

    private LicenseDto license;

    private Boolean scriptUpdateSkip;

    private Boolean scriptUpdateGenerateFile;

    private String scriptGenerateMode;

    private String scriptUpdateGeneratePath;

    private Boolean appLicenseShow;

    public AppConfigDto() {
        this.appLogEnable = false;
        this.svnDefaultAppendBiz = false;
        this.scriptUpdateSkip = false;
        this.scriptUpdateGenerateFile = false;
        this.appLicenseShow = false;
    }
}
