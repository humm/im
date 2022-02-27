package com.hoomoomoo.im.dto;

import lombok.Data;

import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.SYMBOL_EMPTY;

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

    private String scriptUpdateGenerateMode;

    private String scriptUpdateIgnoreSkip;

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

    private String copyCodePrefix;

    private Map<String, String> copyCodeVersion;

    private Map<String, String> svnUrl;

    private String svnRep;

    private String svnStartPrefix;

    private String jdCookie;

    private String jdAppraiseNum;

    private String jdIntervalTime;

    private String jdAppraiseDefault;

    private String jdAppraiseInfo;

    private String jdAppraiseWait;

    private String jdAppraiseWaitGoods;

    private String jdShowOrder;

    private String jdShowOrderExecute;

    private String jdShowOrderInfo;

    private String jdAppendAppraise;

    private String jdAppendAppraiseExecute;

    private String jdServiceAppraise;

    private String jdServiceAppraiseExecute;

    private Boolean jdInitQuery;

    private String jdUser;

    private String jdUserCode;

    private Map<String, String> cookieMap;

    private String executeType;

    private String jdServiceType;

    private String generateSqlDatabaseNum;

    private String generateSqlTableNum;

    private String generateSqlType;

    private String generateSqlDatabaseCode;

    private String generateSqlTableCode;

    public AppConfigDto() {
        this.appLogEnable = false;
        this.svnDefaultAppendBiz = false;
        this.scriptUpdateSkip = false;
        this.scriptUpdateGenerateFile = false;
        this.appLicenseShow = false;
        this.jdInitQuery = false;
        this.svnUpdatePath = new ArrayList<>(16);
        this.scriptUpdateTable = new ArrayList<>(16);
        this.svnStatUser = new LinkedHashMap<>(16);
        this.svnStatReset = true;
        this.svnStatInterval = 0;
        this.copyCodeVersion = new LinkedHashMap(16);
        this.svnUrl = new LinkedHashMap(16);
        this.cookieMap = new HashMap<>(16);
        this.executeType = SYMBOL_EMPTY;
    }
}
