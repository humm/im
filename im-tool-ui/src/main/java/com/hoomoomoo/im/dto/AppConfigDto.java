package com.hoomoomoo.im.dto;

import com.sun.imageio.plugins.common.LZWCompressor;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    private Boolean scriptUpdateAnnotationSkip;

    private Boolean scriptUpdateGenerateFile;

    private String scriptGenerateMode;

    private String scriptUpdateGeneratePath;

    private Boolean appLicenseShow;

    private Integer svnStatInterval;

    private Boolean svnStatReset;

    private LinkedHashMap<String, String> svnStatUser;

    private String svnStatHistoryOrderType;

    private String appUser;

    private String generateCodeJavaPath;

    private String generateCodeSqlPath;

    private String generateCodeVuePath;

    private String generateCodeRoutePath;

    private String generateCodeAuthor;

    private String generateCodeDbType;

    private String generateCodePageType;

    private String copyCodeDefaultSource;

    private String copyCodeDefaultTarget;

    private Map<String, String> copyCodeVersion;

    public AppConfigDto() {
        this.appLogEnable = false;
        this.svnDefaultAppendBiz = false;
        this.scriptUpdateSkip = false;
        this.scriptUpdateGenerateFile = false;
        this.scriptUpdateAnnotationSkip = false;
        this.appLicenseShow = false;
        this.svnUpdatePath = new ArrayList<>(16);
        this.scriptUpdateTable = new ArrayList<>(16);
        this.svnStatUser = new LinkedHashMap<>(16);
        this.svnStatReset = true;
        this.svnStatInterval = 0;
        this.copyCodeVersion = new LinkedHashMap(16);
    }
}
