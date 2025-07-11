package com.hoomoomoo.im.dto;

import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import lombok.Data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.hoomoomoo.im.consts.BaseConst.STR_BLANK;

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

    private String svnTaskMaxRevision;

    private String svnRecentTime;

    private String svnDeletePrefix;

    private Boolean appLogEnable;

    private int appLogSaveDay;

    private String appLogLevel;

    private String appMode;

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

    private String scriptUpdateGenerateType;

    private String scriptUpdateGenerateUed;

    private String scriptUpdateIgnoreSkip;

    private String scriptUpdateGeneratePath;

    private Integer svnStatInterval;

    private Boolean svnStatReset;

    private LinkedHashMap<String, String> svnStatUser;

    private String svnStatHistoryOrderType;

    private String appUser;

    private String copyCodeDefaultSource;

    private String copyCodeDefaultTarget;

    private String copyCodePrefix;

    private String copyCodeOnlyClass;

    private Map<String, String> copyCodeVersion;

    private Map<String, String> svnUrl;

    private Map<String, String> replaceSourceUrl;

    private Map<String, String> replaceTargetUrl;

    private String copyCodeLocationReplaceSkipAccountVersion;

    private String copyCodeLocationReplaceSkipVersion;

    private String copyCodeLocationReplaceVersion;

    private String svnRep;

    private String svnStartPrefix;

    private Map<String, String> cookieMap;

    private String executeType;

    private String generateSqlDatabaseNum;

    private String generateSqlTableNum;

    private String generateSqlType;

    private String generateSqlDatabaseCode;

    private String generateSqlTableCode;

    private Stage childStage;

    private Stage errorLogStage;

    private Stage checkResultStage;

    private String pageType;

    private String databaseScriptUrl;

    private String databaseScriptUsername;

    private String databaseScriptPassword;

    private String databaseScriptLocation;

    private Map<String, String> fieldTranslateMap;

    private int fileSyncTimer;

    private String fileSyncAuthVersion;

    private Map<String, String> fileSyncVersionMap;

    private HepTaskDto hepTaskDto;

    private HepTaskComponentDto hepTaskComponentDto;

    private String hepTaskTodoCostTime;

    private String hepTaskUser;

    private String hepTaskUserName;

    private String hepTaskUserExtend;

    private String hepTaskCustomerPath;

    private String hepTaskSameOne;

    private String activateFunction;

    private String activatePrevFunction;

    private String systemToolShakeMouseAuto;

    private String hepTaskTodoDetailSymbol;

    private String systemToolShakeMouseTimer;

    private String systemToolShakeMouseStep;

    private String systemToolShakeMouseStopTime;

    private TabPane tabPane;

    private String generateCodeJavaPath;

    private String generateCodeSqlPath;

    private String generateCodeVuePath;

    private String generateCodeRoutePath;

    private String generateCodeCheckPath;

    private String generateCodeMenuType;

    private String generateCodeMenuFunction;

    private String generateCodeMenuDataSource;

    private String generateCodeMenuAuthor;

    private String generateCodeMenuMultipleTable;

    private String firstMenuCode;

    private String firstMenuName;

    private String secondMenuCode;

    private String secondMenuName;

    private String thirdMenuCode;

    private String thirdMenuName;

    private String menuOrder;

    private String entityCode;

    private String tableCode;

    private String asyTableCode;

    private List<ColumnDto> configColumnList;

    private List<ColumnDto> tableColumnList;

    private ColumnDto columnDto;

    private List<VersionDto> versionDtoList;

    private List<String> dayPublishVersion;

    private List<String> weekPublishVersion;

    private String systemToolCheckMenuResultPath;

    private String systemToolCheckMenuFundBasePath;

    private String systemToolCheckMenuFundBaseRouterPath;

    private String systemToolCheckMenuPubBasePath;

    private String systemToolCheckMenuFundExtPath;

    private String systemToolCheckMenuEndFlag;

    private String systemToolCheckMenuSkipRouter;

    private int systemToolScriptRepairBatchNum;

    private String systemToolScriptChangeMenuPath;

    private int systemToolGarbageCollectionTimer;

    private int systemToolLogScanTimer;

    private String systemToolSyncCodeSource;

    private String systemToolSyncCodeTarget;

    private Boolean execute;

    private String repairSchedule;

    private String hepTaskRestPlan;

    private String hepTaskRestPlanDate;

    private String hepTaskRestPlanTime;

    private String errorLogDetail;

    private ConcurrentHashMap<String, Timer> timerMap;

    private Boolean initScanLog;

    private LinkedHashMap<String, String> scanLogTipsIndex;

    private Tooltip tooltip;

    public AppConfigDto() {
        this.execute = false;
        this.appLogEnable = false;
        this.svnDefaultAppendBiz = false;
        this.scriptUpdateSkip = false;
        this.scriptUpdateGenerateFile = false;
        this.svnUpdatePath = new ArrayList<>(16);
        this.scriptUpdateTable = new ArrayList<>(16);
        this.svnStatUser = new LinkedHashMap<>(16);
        this.svnStatReset = true;
        this.svnStatInterval = 0;
        this.copyCodeVersion = new LinkedHashMap(16);
        this.svnUrl = new LinkedHashMap(16);
        this.replaceSourceUrl = new LinkedHashMap(16);
        this.replaceTargetUrl = new LinkedHashMap(16);
        this.cookieMap = new LinkedHashMap<>(16);
        this.fieldTranslateMap = new LinkedHashMap<>(16);
        this.fileSyncVersionMap = new LinkedHashMap(16);
        this.executeType = STR_BLANK;
        this.appUser = STR_BLANK;
        this.configColumnList = new ArrayList<>(16);
        this.tableColumnList = new ArrayList<>(16);
        this.dayPublishVersion = new ArrayList<>(16);
        this.weekPublishVersion = new ArrayList<>(16);
        this.timerMap = new ConcurrentHashMap<>();
        this.initScanLog = true;
        this.scanLogTipsIndex = new LinkedHashMap<>();
        tooltip = new Tooltip();
    }
}
