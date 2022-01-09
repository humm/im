package im.service.impl;

import com.github.pagehelper.PageHelper;
import im.config.RunDataConfig;
import im.config.WebSocketServerConfig;
import im.config.bean.ConfigBean;
import im.config.bean.DatasourceConfigBean;
import im.dao.SysConfigDao;
import im.dao.SysDictionaryDao;
import im.dao.SysSystemDao;
import im.dao.SysUserDao;
import im.model.*;
import im.model.base.BaseModel;
import im.model.base.ResultData;
import im.model.base.SessionBean;
import im.model.base.ViewData;
import im.service.SysMailService;
import im.service.SysMenuService;
import im.service.SysParameterService;
import im.service.SysSystemService;
import im.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static im.config.RunDataConfig.*;
import static im.consts.BaseConst.*;
import static im.consts.BaseCueConst.*;
import static im.consts.DictionaryConst.*;
import static im.consts.ParameterConst.MIND_FILL;
import static im.consts.ParameterConst.*;

/**
 * @author hoomoomoo
 * @description 系统级别公用服务
 * @package im.service.common.imp
 * @date 2019/08/04
 */

@Service
@Transactional
public class SysSystemServiceImpl implements SysSystemService {

    private static final Logger logger = LoggerFactory.getLogger(SysSystemServiceImpl.class);

    @Autowired
    private ConfigBean configBean;

    @Autowired
    private DatasourceConfigBean datasourceConfigBean;

    @Autowired
    private Environment environment;

    @Autowired
    private SysSystemDao sysSystemDao;

    @Autowired
    private SysDictionaryDao sysDictionaryDao;

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private SysParameterService sysParameterService;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysMailService sysMailService;

    @Autowired
    private SysConfigDao sysConfigDao;

    /**
     * 控制台输出应用配置参数
     */
    @Override
    public void outputConfigParameter() {
        if (!sysParameterService.getParameterBoolean(START_CONSOLE_OUTPUT)) {
            return;
        }
        Properties properties = new OrderedProperties();
        SysLogUtils.configStart(logger, LOG_BUSINESS_TYPE_PARAMETER_CONFIG);
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(APPLICATION_PROPERTIES.split(COLON)[1]);
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_PARAMETER_CONFIG, e);
        } catch (IOException e) {
            SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_PARAMETER_CONFIG, e);
        }
        Iterator iterator = properties.stringPropertyNames().iterator();
        while (iterator.hasNext()) {
            StringBuffer singleProperty = new StringBuffer();
            String key = String.valueOf(iterator.next());
            singleProperty.append(key).append(EQUAL_SIGN).append(convertValue(key));
            boolean isIgnore = false;
            String ruleConfig = configBean.getIgnoreOutputKeyword();
            if (StringUtils.isNotBlank(ruleConfig)) {
                String[] rules = ruleConfig.split(COMMA);
                for (String rule : rules) {
                    if (key.startsWith(rule) || key.endsWith(rule)) {
                        isIgnore = true;
                        break;
                    }
                }
            }
            if (!isIgnore) {
                SysLogUtils.info(logger, singleProperty);
            }
        }
        SysLogUtils.configEnd(logger, LOG_BUSINESS_TYPE_PARAMETER_CONFIG);
    }

    /**
     * 加载业务ID
     *
     * @return
     */
    @Override
    public void loadBusinessId() {
        SysLogUtils.functionStart(logger, LOG_BUSINESS_TYPE_BUSINESS_SERIAL_NO_LOAD);
        BUSINESS_SERIAL_NO.clear();
        List<String> businessIdList = sysSystemDao.loadBusinessId();
        if (CollectionUtils.isNotEmpty(businessIdList)) {
            for (String businessId : businessIdList) {
                if (businessId.split(MINUS).length != 2) {
                    continue;
                }
                String businessKey = businessId.split(MINUS)[0];
                String businessValue = businessId.split(MINUS)[1];
                BUSINESS_SERIAL_NO.put(businessKey, businessValue);
            }
        }
        SysLogUtils.functionEnd(logger, LOG_BUSINESS_TYPE_BUSINESS_SERIAL_NO_LOAD);
    }

    /**
     * 根据业务类型获取业务ID
     *
     * @param businessType
     * @return
     */
    @Override
    public String getBusinessSerialNo(String businessType) {
        SysLogUtils.functionStart(logger, LOG_BUSINESS_TYPE_BUSINESS_SERIAL_NO_GET);
        if (StringUtils.isBlank(businessType)) {
            SysLogUtils.fail(logger, LOG_BUSINESS_TYPE_BUSINESS_SERIAL_NO_GET, BUSINESS_TYPE_NOT_EMPTY);
            SysLogUtils.functionEnd(logger, LOG_BUSINESS_TYPE_BUSINESS_SERIAL_NO_GET);
            return null;
        }
        String businessId = null;
        Lock lock = new ReentrantLock();
        lock.lock();
        try {
            businessId = BUSINESS_SERIAL_NO.get(businessType);
            // 业务ID不存在 设置默认值
            if (StringUtils.isBlank(businessId)) {
                businessId = SysDateUtils.yyyy() + BUSINESS_ID_DEFAULT;
            } else {
                String businessYear = businessId.substring(0, 4);
                String businessNo = businessId.substring(4);
                // 业务ID时间不是当前时间 设置默认值
                if (!SysDateUtils.yyyy().equals(businessYear)) {
                    businessId = SysDateUtils.yyyy() + BUSINESS_ID_DEFAULT;
                } else {
                    // 去除多去的0 获取序列号
                    while (businessNo.startsWith(STR_0)) {
                        businessNo = businessNo.substring(1);
                    }
                    // 序列号加1
                    businessNo = String.valueOf(Long.valueOf(businessNo) + 1);
                    // 序列号补0
                    while (businessNo.length() < 10) {
                        businessNo = STR_0 + businessNo;
                    }
                    businessId = businessYear + businessNo;
                }
            }
            // 更新内存数据序列号值
            BUSINESS_SERIAL_NO.put(businessType, businessId);
            SysLogUtils.success(logger, LOG_BUSINESS_TYPE_BUSINESS_SERIAL_NO_GET, businessType + MINUS + businessId);
        } catch (Exception e) {
            SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_BUSINESS_SERIAL_NO_GET, e);
        } finally {
            lock.unlock();
        }
        SysLogUtils.functionEnd(logger, LOG_BUSINESS_TYPE_BUSINESS_SERIAL_NO_GET);
        return businessId;
    }

    /**
     * 字典转义
     *
     * @param list
     * @param clazz
     * @return
     */
    @Override
    public List transferData(List list, Class clazz) {
        SysLogUtils.functionStart(logger, LOG_BUSINESS_TYPE_DICTIONARY_TRANSFER);
        List<BaseModel> baseModelList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            // 本次查询缓存数据
            Map dictionaryCache = new HashMap(16);
            for (Object obj : list) {
                baseModelList.add(transfer(dictionaryCache, SysBeanUtils.beanToMap(obj), clazz, true));
            }
        }
        SysLogUtils.functionEnd(logger, LOG_BUSINESS_TYPE_DICTIONARY_TRANSFER);
        return baseModelList;
    }

    /**
     * 字典转义
     *
     * @param list
     * @return
     */
    @Override
    public List<LinkedHashMap> transferData(List<LinkedHashMap> list) {
        SysLogUtils.functionStart(logger, LOG_BUSINESS_TYPE_DICTIONARY_TRANSFER);
        List<LinkedHashMap> baseModelList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            // 本次查询缓存数据
            Map dictionaryCache = new HashMap(16);
            for (LinkedHashMap single : list) {
                baseModelList.add(transfer(dictionaryCache, single, true));
            }
        }
        SysLogUtils.functionEnd(logger, LOG_BUSINESS_TYPE_DICTIONARY_TRANSFER);
        return null;
    }

    /**
     * 字典转义
     *
     * @param baseModel
     * @return
     */
    @Override
    public void transferData(BaseModel baseModel, Class clazz) {
        SysLogUtils.functionStart(logger, LOG_BUSINESS_TYPE_DICTIONARY_TRANSFER);
        if (baseModel != null) {
            BaseModel baseModelTransfer = transfer(new HashMap(16), SysBeanUtils.beanToMap(baseModel), clazz, true);
            BeanUtils.copyProperties(baseModelTransfer, baseModel);
        }
        SysLogUtils.functionEnd(logger, LOG_BUSINESS_TYPE_DICTIONARY_TRANSFER);
    }

    @Override
    public void transferData(BaseModel baseModel, Class clazz, Boolean transferAmout) {
        SysLogUtils.functionStart(logger, LOG_BUSINESS_TYPE_DICTIONARY_TRANSFER);
        if (baseModel != null) {
            BaseModel baseModelTransfer = transfer(new HashMap(16), SysBeanUtils.beanToMap(baseModel), clazz, transferAmout);
            BeanUtils.copyProperties(baseModelTransfer, baseModel);
        }
        SysLogUtils.functionEnd(logger, LOG_BUSINESS_TYPE_DICTIONARY_TRANSFER);
    }

    /**
     * 加载所有字典查询条件
     *
     * @return
     */
    @Override
    public void loadSysDictionaryCondition() {
        Map<String, Boolean> userDataAuthority = new HashMap(16);
        SysLogUtils.functionStart(logger, LOG_BUSINESS_TYPE_DICTIONARY_LOAD);
        DICTIONARY_CONDITION.clear();
        // 查询用户信息
        List<SysUserModel> sysUserList = sysUserDao.selectSysUser(null);
        // 查询字典信息
        List<SysDictionaryModel> sysDictionaryList = sysDictionaryDao.selectSysDictionaryCondition();
        // 拼装数据
        for (SysUserModel sysUserModel : sysUserList) {
            for (SysDictionaryModel sysDictionaryModel : sysDictionaryList) {
                String dictionaryCode = sysDictionaryModel.getDictionaryCode();
                // 用户不存在
                if (DICTIONARY_CONDITION.get(sysUserModel.getUserId()) == null) {
                    setDictionaryItem(sysUserModel, sysDictionaryModel, userDataAuthority, new ConcurrentHashMap(1), new ConcurrentHashMap(1));
                } else {
                    // 用户存在 字典不存在
                    if (DICTIONARY_CONDITION.get(sysUserModel.getUserId()).get(dictionaryCode) == null) {
                        setDictionaryItem(sysUserModel, sysDictionaryModel, userDataAuthority,
                                DICTIONARY_CONDITION.get(sysUserModel.getUserId()),
                                DICTIONARY_CONDITION.get(new StringBuffer(sysUserModel.getUserId()).append(BLANK).toString()));
                    } else {
                        // 用户存在 字典存在
                        if (isUserDictionary(sysUserModel, sysDictionaryModel, userDataAuthority)) {
                            DICTIONARY_CONDITION.get(sysUserModel.getUserId()).get(dictionaryCode).add(sysDictionaryModel);
                            DICTIONARY_CONDITION.get(new StringBuffer(sysUserModel.getUserId()).append(BLANK).toString()).get(dictionaryCode).add(sysDictionaryModel);
                        }
                    }
                }
            }
        }
        SysLogUtils.functionEnd(logger, LOG_BUSINESS_TYPE_DICTIONARY_LOAD);
    }

    /**
     * 获取用户ID
     *
     * @return
     */
    @Override
    public String getUserId() {
        SysLogUtils.functionStart(logger, LOG_BUSINESS_TYPE_USER_ID_SELECT);
        String userId = STR_EMPTY;
        SessionBean sessionBean = SysSessionUtils.getSession();
        if (sessionBean != null) {
            userId = sessionBean.getUserId();
        } else {
            SysUserQueryModel sysUserQueryModel = new SysUserQueryModel();
            sysUserQueryModel.setUserCode(ADMIN_CODE);
            List<SysUserModel> sysUserModelList = sysUserDao.selectSysUser(sysUserQueryModel);
            if (CollectionUtils.isNotEmpty(sysUserModelList)) {
                SysUserModel sysUserModel = sysUserModelList.get(0);
                if (sysUserModel != null) {
                    userId = sysUserModel.getUserId();
                }
            }
        }
        SysLogUtils.functionEnd(logger, LOG_BUSINESS_TYPE_USER_ID_SELECT);
        return userId;
    }

    /**
     * 设置查询条件
     *
     * @param viewData
     */
    @Override
    public void setCondition(ViewData viewData) {
        SysLogUtils.functionStart(logger, LOG_BUSINESS_TYPE_CONDITION_SET);
        // 智能填充
        viewData.setMindFill(RunDataConfig.MIND_FILL);
        // 设置登录用户信息
        viewData.setSessionBean(SysSessionUtils.getSession());
        String userId = getUserId();
        // 获取查询条件
        switch (viewData.getViewType()) {
            case BUSINESS_TYPE_INCOME:
                viewData.getCondition().put(SELECT_USER_ID,
                        DICTIONARY_CONDITION.get(new StringBuffer(userId).append(BLANK).toString()).get(D000));
                viewData.getCondition().put(SELECT_INCOME_COMPANY,
                        DICTIONARY_CONDITION.get(new StringBuffer(userId).append(BLANK).toString()).get(D005));
                viewData.getCondition().put(SELECT_INCOME_TYPE,
                        DICTIONARY_CONDITION.get(new StringBuffer(userId).append(BLANK).toString()).get(D003));
                break;
            case BUSINESS_TYPE_GIFT:
                viewData.getCondition().put(SELECT_GIFT_TYPE,
                        DICTIONARY_CONDITION.get(new StringBuffer(userId).append(BLANK).toString()).get(D004));
                viewData.getCondition().put(SELECT_GIFT_SENDER,
                        DICTIONARY_CONDITION.get(new StringBuffer(userId).append(BLANK).toString()).get(D009));
                viewData.getCondition().put(SELECT_GIFT_RECEIVER,
                        DICTIONARY_CONDITION.get(new StringBuffer(userId).append(BLANK).toString()).get(D009));
                break;
            case BUSINESS_TYPE_USER:
                viewData.getCondition().put(SELECT_USER_STATUS,
                        DICTIONARY_CONDITION.get(new StringBuffer(userId).append(BLANK).toString()).get(D001));
                break;
            case BUSINESS_TYPE_LOGIN_LOG:
                viewData.getCondition().put(SELECT_USER_ID,
                        DICTIONARY_CONDITION.get(new StringBuffer(userId).append(BLANK).toString()).get(D000));
                viewData.getCondition().put(SELECT_LOGIN_STATUS,
                        DICTIONARY_CONDITION.get(new StringBuffer(userId).append(BLANK).toString()).get(D002));
                break;
            case BUSINESS_TYPE_NOTICE:
                viewData.getCondition().put(SELECT_USER_ID,
                        DICTIONARY_CONDITION.get(new StringBuffer(userId).append(BLANK).toString()).get(D000));
                viewData.getCondition().put(SELECT_NOTICE_TYPE,
                        DICTIONARY_CONDITION.get(new StringBuffer(userId).append(BLANK).toString()).get(D008));
                viewData.getCondition().put(SELECT_NOTICE_STATUS,
                        DICTIONARY_CONDITION.get(new StringBuffer(userId).append(BLANK).toString()).get(D007));
                viewData.getCondition().put(SELECT_READ_STATUS,
                        DICTIONARY_CONDITION.get(new StringBuffer(userId).append(BLANK).toString()).get(D012));
                viewData.getCondition().put(SELECT_BUSINESS_TYPE,
                        DICTIONARY_CONDITION.get(new StringBuffer(userId).append(BLANK).toString()).get(D011));
                viewData.getCondition().put(SELECT_BUSINESS_SUB_TYPE,
                        DICTIONARY_CONDITION.get(new StringBuffer(userId).append(BLANK).toString()).get(D003));
                viewData.getCondition().get(SELECT_BUSINESS_SUB_TYPE).addAll(DICTIONARY_CONDITION.get(userId).get(D004));
                break;
            default:
                break;
        }
        SysLogUtils.functionEnd(logger, LOG_BUSINESS_TYPE_CONDITION_SET);
    }

    /**
     * 查询按钮权限
     *
     * @param menuId
     * @return
     */
    @Override
    public Boolean selectButtonAuthority(String menuId) {
        Boolean hasAuthority = false;
        SysLogUtils.functionStart(logger, LOG_BUSINESS_TYPE_BUTTON_AUTHORITY_SELECT);
        SessionBean sessionBean = SysSessionUtils.getSession();
        if (sessionBean != null) {
            if (ADMIN_CODE.equals(sessionBean.getUserCode())) {
                hasAuthority = true;
            } else {
                // 获取按钮权限
                SysSystemQueryModel sysSystemQueryModel = new SysSystemQueryModel();
                sysSystemQueryModel.setMenuId(menuId);
                sysSystemQueryModel.setUserId(sessionBean.getUserId());
                hasAuthority = sysSystemDao.selectButtonAuthority(sysSystemQueryModel);
            }
        }
        SysLogUtils.functionEnd(logger, LOG_BUSINESS_TYPE_BUTTON_AUTHORITY_SELECT);
        return hasAuthority;
    }

    /**
     * 获取用户密码
     *
     * @return
     */
    @Override
    public String selectUserPassword() {
        String password = STR_EMPTY;
        SessionBean sessionBean = SysSessionUtils.getSession();
        if (sessionBean != null) {
            SysUserQueryModel sysUserQueryModel = new SysUserQueryModel();
            sysUserQueryModel.setUserId(sessionBean.getUserId());
            List<SysUserModel> sysUserModelList = sysUserDao.selectSysUser(sysUserQueryModel);
            if (CollectionUtils.isNotEmpty(sysUserModelList)) {
                password = new StringBuffer(sysUserModelList.get(0).getUserPassword()).reverse().toString();
            }
        }
        return password;
    }

    /**
     * 系统初始化
     */
    @Override
    public void initSystem() {
        // 初始化模式  1:不初始化 2:强制初始化 3:弱校验初始化 4:强校验初始化
        SYSTEM_USED_STATUS = SYSTEM_STATUS_INIT;
        String initMode = configBean.getInitMode();
        SysTableQueryModel sysTableQueryModel = new SysTableQueryModel();
        sysTableQueryModel.setTableName(SYSTEM_TABLE.toLowerCase());
        switch (initMode) {
            case STR_2:
                init(LOG_BUSINESS_TYPE_INIT_SYSTEM, INIT_SYSTEM_PROCEDURE, INIT_SYSTEM_TABLE, INIT_SYSTEM_DATA);
                break;
            case STR_3:
                // 有表不存在则初始化
                int num = SYSTEM_TABLE.split(COMMA).length;
                int tableNum = sysSystemDao.selectTableNum(sysTableQueryModel);
                if (num > tableNum) {
                    init(LOG_BUSINESS_TYPE_INIT_SYSTEM, INIT_SYSTEM_PROCEDURE, INIT_SYSTEM_TABLE, INIT_SYSTEM_DATA);
                }
                break;
            case STR_4:
                // 所有表不存在则初始化
                tableNum = sysSystemDao.selectTableNum(sysTableQueryModel);
                if (tableNum == 0) {
                    init(LOG_BUSINESS_TYPE_INIT_SYSTEM, INIT_SYSTEM_PROCEDURE, INIT_SYSTEM_TABLE, INIT_SYSTEM_DATA);
                }
                break;
            default:
                break;
        }
        SYSTEM_USED_STATUS = null;
    }

    /**
     * 系统升级
     */
    @Override
    public void updateSystem() {
        String currentVersion = sysParameterService.getParameterString(VERSION);
        if (!SYSTEM_VERSION.equals(currentVersion)) {
            SYSTEM_USED_STATUS = SYSTEM_STATUS_UPDATE;
            SysLogUtils.functionStart(logger, LOG_BUSINESS_TYPE_UPDATE_SYSTEM);
            init(LOG_BUSINESS_TYPE_UPDATE_SYSTEM, UPDATE_SYSTEM_PROCEDURE, null, UPDATE_SYSTEM_DATA);
            // 刷新字典项 加业务ID
            loadSysDictionaryCondition();
            loadBusinessId();
            SYSTEM_USED_STATUS = null;
            SysLogUtils.functionEnd(logger, LOG_BUSINESS_TYPE_UPDATE_SYSTEM);
        }
    }

    /**
     * 参数初始化
     */
    @Override
    public void initParameter() {
        // 控制台输出请求标记
        RunDataConfig.LOG_REQUEST_TAG = sysParameterService.getParameterBoolean(CONSOLE_OUTPUT_LOG_REQUEST_TAG);
        // 控制台输出请求入参
        RunDataConfig.LOG_REQUEST_PARAMETER = sysParameterService.getParameterBoolean(CONSOLE_OUTPUT_LOG_REQUEST_PARAMETER);
        // 智能填充
        RunDataConfig.MIND_FILL = sysParameterService.getParameterBoolean(MIND_FILL);
        // 获取邮件配置参数
        RunDataConfig.MAIL_CONFIG = sysParameterService.getMailConfig();
    }

    /**
     * 加载配置sql
     */
    @Override
    public void getConfigSql() {
        CONFIG_SQL = SysCommonUtils.getConfigSql(SYSTEM_CONFIG_SQL);
    }

    /**
     * 系统备份文件
     *
     * @param fileName
     */
    @Override
    public ResultData systemBackupFile(String fileName) {
        SysLogUtils.functionStart(logger, LOG_BUSINESS_TYPE_BACKUP_SQL);
        String[] tables = SYSTEM_TABLE.split(COMMA);
        StringBuffer database = new StringBuffer(String.format(BACKUP_TIPS, LOG_BUSINESS_TYPE_BACKUP_LIST, LOG_OPERATE_TAG_START));
        StringBuffer databaseContent = new StringBuffer(String.format(BACKUP_TIPS, LOG_BUSINESS_TYPE_BACKUP, LOG_OPERATE_TAG_START));
        database.append(NEXT_LINE);
        databaseContent.append(NEXT_LINE);
        for (String tableName : tables) {
            // 去除表名称的单引号
            tableName = tableName.substring(1, tableName.length() - 1);
            database.append(EXPLAN).append(tableName).append(NEXT_LINE);
            StringBuffer tableInfo = new StringBuffer();
            tableInfo.append(EXPLAN).append(tableName).append(NEXT_LINE);
            // 历史数据数据
            tableInfo.append(TRUNCATE_LEFT).append(tableName).append(SEMICOLON).append(NEXT_LINE);
            tableInfo.append(COMMIT).append(NEXT_LINE).append(NEXT_LINE);

            // 拼装查询数据
            SysTableQueryModel sysTableQueryModel = new SysTableQueryModel();
            sysTableQueryModel.setTableName(tableName);
            // 查询数据表字段
            List<SysTableModel> sysTableColumns = sysSystemDao.selectTableColumn(sysTableQueryModel);
            if (CollectionUtils.isNotEmpty(sysTableColumns)) {
                LinkedHashMap<String, String> columnMap = getQueryCondition(sysTableQueryModel, tableName);
                // 查询数据表总数据大小
                SysTableModel sysTableModel = sysSystemDao.selectTableCount(sysTableQueryModel);
                if (sysTableModel == null || sysTableModel.getDataCount() == 0) {
                    // 空表直接返回
                    databaseContent.append(tableInfo);
                    continue;
                }
                // 查询数据表数据
                if (sysTableModel.getDataCount() <= 100) {
                    buildTableData(sysSystemDao.selectTableData(sysTableQueryModel), tableName, columnMap, tableInfo);
                } else {
                    // 分页数据查询
                    Long page = sysTableModel.getDataCount() / BACKUP_DATA_LIMIT;
                    if (sysTableModel.getDataCount() % BACKUP_DATA_LIMIT != 0) {
                        page++;
                    }
                    sysTableQueryModel.setLimit(BACKUP_DATA_LIMIT);
                    for (int i = 1; i <= page; i++) {
                        sysTableQueryModel.setPage(i);
                        PageHelper.startPage(sysTableQueryModel.getPage(), sysTableQueryModel.getLimit());
                        buildTableData(sysSystemDao.selectTableData(sysTableQueryModel), tableName, columnMap, tableInfo);
                    }
                }
            }
            databaseContent.append(tableInfo).append(COMMIT).append(NEXT_LINE);
        }
        databaseContent.append(String.format(BACKUP_TIPS, LOG_BUSINESS_TYPE_BACKUP, LOG_OPERATE_TAG_END));
        database.append(String.format(BACKUP_TIPS, LOG_BUSINESS_TYPE_BACKUP_LIST, LOG_OPERATE_TAG_END));
        database.append(NEXT_LINE).append(NEXT_LINE).append(databaseContent);
        try {
            // 写文件
            String backupLocation = sysParameterService.getParameterString(BACKUP_LOCATION);
            File saveFile = new File(backupLocation + SLASH + fileName);
            FileUtils.writeStringToFile(saveFile, database.toString(), UTF8);
            SysLogUtils.success(logger, LOG_BUSINESS_TYPE_BACKUP_SQL);
        } catch (IOException e) {
            SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_BACKUP_SQL, e);
            return new ResultData(false, e.getMessage(), null);
        }
        SysLogUtils.functionEnd(logger, LOG_BUSINESS_TYPE_BACKUP_SQL);
        return new ResultData(true, BACKUP_SUCCESS, null);
    }

    /**
     * 组装数据表数据
     *
     * @param tableData
     * @param tableName
     * @param columnMap
     * @param tableInfo
     */
    private void buildTableData(List<LinkedHashMap> tableData, String tableName, LinkedHashMap<String, String> columnMap, StringBuffer tableInfo) {
        if (CollectionUtils.isNotEmpty(tableData)) {
            // 拼装数据
            for (Map singleData : tableData) {
                StringBuffer sql = new StringBuffer();
                StringBuffer insert = new StringBuffer();
                StringBuffer values = new StringBuffer();
                insert.append(INSERT_LEFT).append(tableName).append(STR_SPACE).append(BRACKET_LEFT);
                values.append(VALUES_LEFT);
                Iterator<Map.Entry<String, String>> iterator = columnMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> columnData = iterator.next();
                    String columnCode = columnData.getKey().toLowerCase();
                    String columnType = columnData.getValue();
                    insert.append(columnCode).append(COMMA).append(STR_SPACE);
                    values.append(getColumnValue(columnType, singleData.get(columnCode.toUpperCase()))).append(COMMA).append(STR_SPACE);
                }
                if (insert.toString().endsWith(new StringBuffer(COMMA).append(STR_SPACE).toString())) {
                    insert = new StringBuffer(insert.substring(0, insert.length() - 2));
                }
                if (values.toString().endsWith(new StringBuffer(COMMA).append(STR_SPACE).toString())) {
                    values = new StringBuffer(values.substring(0, values.length() - 2));
                }
                sql.append(insert).append(BRACKET_RIGHT).append(NEXT_LINE)
                        .append(values).append(BRACKET_RIGHT).append(SEMICOLON)
                        .append(NEXT_LINE).append(NEXT_LINE);
                tableInfo.append(sql);
            }
        }
    }

    /**
     * 系统备份dmp
     *
     * @param fileName
     */
    @Override
    public ResultData systemBackupDmp(String fileName) {
        SysLogUtils.functionStart(logger, LOG_BUSINESS_TYPE_BACKUP_DMP);
        ResultData resultData = null;
        // 拼装执行命令
        String username = datasourceConfigBean.getUsername();
        String password = datasourceConfigBean.getPassword();
        String sid = STR_EMPTY;
        String url = datasourceConfigBean.getUrl();
        if (StringUtils.isNotBlank(url)) {
            String[] connect = url.split(AT)[1].split(COLON);
            sid = connect[2];
        }
        String command = String.format(BACKUP_COMMAND, username, password, sid, fileName);
        String commandTip = String.format(BACKUP_COMMAND, username, ASTERISK_SIX, sid, fileName);
        SysLogUtils.info(logger, TIP_BACKUP_COMMAND + commandTip);
        try {
            resultData = SysCommandUtils.execute(command);
        } catch (Exception e) {
            SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_BACKUP_DMP, e);
            return new ResultData(false, e.getMessage(), null);
        }
        if (STR_0.equals(resultData.getCode())) {
            // 删除备份记录文件
            File log = new File(sysParameterService.getParameterString(BACKUP_LOCATION) + SLASH + BACKUP_LOG);
            if (log.exists()) {
                log.delete();
            }
            File backupFile = new File(sysParameterService.getParameterString(BACKUP_LOCATION) + SLASH + fileName.toUpperCase());
            backupFile.renameTo(new File(sysParameterService.getParameterString(BACKUP_LOCATION) + SLASH + fileName));
            SysLogUtils.success(logger, LOG_BUSINESS_TYPE_BACKUP_DMP);
        } else {
            SysLogUtils.fail(logger, LOG_BUSINESS_TYPE_BACKUP_DMP, resultData.getData());
        }
        SysLogUtils.functionEnd(logger, LOG_BUSINESS_TYPE_BACKUP_DMP);
        return resultData;
    }

    /**
     * 系统备份dmp
     *
     * @param fileName
     */
    @Override
    public ResultData systemBackupExcel(String fileName) {
        SysLogUtils.functionStart(logger, LOG_BUSINESS_TYPE_BACKUP_EXCEL);
        String[] tables = SYSTEM_TABLE.split(COMMA);
        LinkedHashMap<String, List<LinkedHashMap>> backupData = new LinkedHashMap<>(16);
        for (String tableName : tables) {
            // 去除表名称的单引号
            tableName = tableName.substring(1, tableName.length() - 1);
            if (!isBackupTable(tableName)) {
                // 不是配置表直接返回
                continue;
            }
            SysTableQueryModel sysTableQueryModel = new SysTableQueryModel();
            getQueryCondition(sysTableQueryModel, tableName);
            // 查询数据表总数据大小
            SysTableModel sysTableModel = sysSystemDao.selectTableCount(sysTableQueryModel);
            if (sysTableModel == null || sysTableModel.getDataCount() == 0) {
                // 空表直接返回
                continue;
            }
            // 查询数据表数据
            if (sysTableModel.getDataCount() <= 100) {
                backupData.put(tableName, sysSystemDao.selectTableData(sysTableQueryModel));
            } else {
                // 分页数据查询
                Long page = sysTableModel.getDataCount() / BACKUP_DATA_LIMIT;
                if (sysTableModel.getDataCount() % BACKUP_DATA_LIMIT != 0) {
                    page++;
                }
                sysTableQueryModel.setLimit(BACKUP_DATA_LIMIT);
                for (int i = 1; i <= page; i++) {
                    sysTableQueryModel.setPage(i);
                    PageHelper.startPage(sysTableQueryModel.getPage(), sysTableQueryModel.getLimit());
                    List<LinkedHashMap> singleQuery = sysSystemDao.selectTableData(sysTableQueryModel);
                    if (backupData.containsKey(tableName)) {
                        backupData.get(tableName).addAll(singleQuery);
                    } else {
                        backupData.put(tableName, singleQuery);
                    }
                }
            }
            List<LinkedHashMap> tableData = backupData.get(tableName);
            transferData(tableData);
            // 设置Excel标题
            if (CollectionUtils.isNotEmpty(tableData)) {
                List<LinkedHashMap> all = new ArrayList<>();
                LinkedHashMap title = new LinkedHashMap(16);
                all.add(title);
                all.addAll(tableData);
                List<SysTableModel> titleList = sysSystemDao.selectTableColumnComments(sysTableQueryModel);
                LinkedHashMap<String, Object> single = tableData.get(0);
                Iterator<String> iterator = single.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    title.put(key, getColumnComments(titleList, key));
                }
                backupData.put(tableName, all);
            }
        }
        // 数据准备完成 写Excel文件
        buildExcel(backupData, fileName);
        SysLogUtils.functionEnd(logger, LOG_BUSINESS_TYPE_BACKUP_EXCEL);
        return null;
    }

    /**
     * 获取Excel标题
     *
     * @param titleList
     * @param columnKey
     * @return
     */
    private String getColumnComments(List<SysTableModel> titleList, String columnKey) {
        if (CollectionUtils.isNotEmpty(titleList)) {
            for (SysTableModel sysTableModel : titleList) {
                if (sysTableModel.getColumnCode().toLowerCase().equals(columnKey.toLowerCase())) {
                    return sysTableModel.getColumnComments();
                }
            }
        }
        return STR_EMPTY;
    }

    /**
     * Excel数据处理
     *
     * @param backupData
     * @param fileName
     */
    private void buildExcel(LinkedHashMap<String, List<LinkedHashMap>> backupData, String fileName) {
        String backupLocation = sysParameterService.getParameterString(BACKUP_LOCATION);
        OutputStream outputStream = null;
        try {
            String filePath = backupLocation + SLASH + fileName;
            // 创建Excel文件
            createExcel(backupData, filePath);
            File file = new File(filePath);
            FileInputStream in = new FileInputStream(file);
            Workbook workbook = WorkbookFactory.create(in);
            if (backupData != null && !backupData.isEmpty()) {
                int sheetIndex = 0;
                Iterator sheetIterator = backupData.keySet().iterator();
                while (sheetIterator.hasNext()) {
                    String tableName = String.valueOf(sheetIterator.next());
                    // 取第一个Sheet工作表
                    Sheet sheet = workbook.getSheetAt(sheetIndex);
                    // 设置单元格属性
                    CellStyle cellStyle = workbook.createCellStyle();
                    // 水平居中
                    cellStyle.setAlignment(HorizontalAlignment.CENTER);
                    // 垂直居中
                    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                    // 文字属性
                    CellStyle boldStyle = workbook.createCellStyle();
                    Font font = workbook.createFont();
                    font.setColor(IndexedColors.BLACK.getIndex());
                    boldStyle.setFont(font);
                    boldStyle.setAlignment(HorizontalAlignment.LEFT);
                    boldStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                    // 加粗样式
                    CellStyle titleStyle = workbook.createCellStyle();
                    font = workbook.createFont();
                    font.setBold(true);
                    font.setColor(IndexedColors.RED.getIndex());
                    titleStyle.setFont(font);
                    titleStyle.setAlignment(HorizontalAlignment.CENTER);
                    titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                    // 写入数据
                    List<LinkedHashMap> rowList = backupData.get(tableName);
                    if (CollectionUtils.isNotEmpty(rowList)) {
                        for (int i = 0; i < rowList.size(); i++) {
                            // 创建行
                            Row row = sheet.createRow(i);
                            // 设置列宽
                            LinkedHashMap rowData = rowList.get(i);
                            Iterator iterator = rowData.keySet().iterator();
                            int index = 0;
                            while (iterator.hasNext()) {
                                String key = String.valueOf(iterator.next());
                                Cell cell = row.createCell(index);
                                if (rowData.get(key) == null) {
                                    cell.setCellValue(STR_EMPTY);
                                } else {
                                    cell.setCellValue(String.valueOf(rowData.get(key)));
                                }
                                cell.setCellStyle(cellStyle);
                                if (i == 0) {
                                    cell.setCellStyle(titleStyle);
                                } else {
                                    cell.setCellStyle(boldStyle);
                                }
                                sheet.setColumnWidth(index, (int) ((BACKUP_EXCEL_CELL_WIDTH + 0.72) * 256));
                                index++;
                            }
                        }
                    }
                    sheetIndex++;
                }
            }
            outputStream = new FileOutputStream(filePath);
            workbook.write(outputStream);
        } catch (Exception e) {
            SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_BACKUP_EXCEL, e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException e) {
                SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_BACKUP_EXCEL, e);
            }
        }
    }

    /**
     * 创建excel文件
     *
     * @param backupData
     * @param filePath
     */
    private void createExcel(LinkedHashMap<String, List<LinkedHashMap>> backupData, String filePath) {
        FileOutputStream fileOutputStream = null;
        try {
            // 创建工作薄
            Workbook workbook = new XSSFWorkbook();
            fileOutputStream = new FileOutputStream(filePath);
            // 设置工作表名
            if (backupData != null && !backupData.isEmpty()) {
                Iterator iterator = backupData.keySet().iterator();
                int index = 0;
                while (iterator.hasNext()) {
                    String tableName = String.valueOf(iterator.next());
                    workbook.createSheet();
                    workbook.setSheetName(index, getExcelInfo(tableName));
                    index++;
                }
            }
            workbook.write(fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_BACKUP_EXCEL, e);
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_BACKUP_EXCEL, e);
            }
        }
    }

    /**
     * 应用启动备份
     *
     * @return
     */
    @Override
    public void startBackup() {
        boolean startBackup = sysParameterService.getParameterBoolean(START_BACKUP);
        if (startBackup) {
            backup(BACKUP_MODE_START);
        }
    }

    /**
     * 应用数据备份
     *
     * @return
     */
    @Override
    public void systemBackup() {
        // 备份文件
        backup(BACKUP_MODE_SCHEDULE);
        // 发送至邮件
        systemBackupToMail();
    }

    /**
     * 系统备份
     *
     * @param backupType
     */
    private void backup(String backupType) {
        String backupLocation = sysParameterService.getParameterString(BACKUP_LOCATION);
        if (WELL.equals(backupLocation)) {
            SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_BACKUP, BACKUP_LOCATION_IS_EMPTY);
            return;
        }
        if (StringUtils.isBlank(backupType)) {
            backupType = BACKUP_MODE_START;
        }
        SYSTEM_USED_STATUS = SYSTEM_STATUS_BACKUP;
        try {
            String backupMode = sysParameterService.getParameterString(BACKUP_MODE);
            String fileNameSuffix = SysDateUtils.yyyyMMddHHmmss();
            if (backupMode.contains(BACKUP_SQL_SUFFIX.substring(1))) {
                systemBackupFile(new StringBuffer(fileNameSuffix).append(MINUS).append(backupType).append(BACKUP_SQL_SUFFIX).toString());
            }
            if (backupMode.contains(BACKUP_EXCEL_SUFFIX.substring(1))) {
                systemBackupExcel(new StringBuffer(fileNameSuffix).append(MINUS).append(backupType).append(BACKUP_EXCEL_SUFFIX).toString());
            }
            if (backupMode.contains(BACKUP_DMP_SUFFIX.substring(1))) {
                systemBackupDmp(new StringBuffer(fileNameSuffix).append(MINUS).append(backupType).append(BACKUP_DMP_SUFFIX).toString());
            }
        } catch (Exception e) {
            SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_BACKUP, e);
        }
        SYSTEM_USED_STATUS = null;
    }

    /**
     * 超级模式启动
     */
    @Override
    public void startSuperMode() {
        SysMenuModel sysMenuModel = new SysMenuModel();
        if (STR_1.equals(configBean.getSuperMode())) {
            sysMenuModel.setIsEnable(STR_1);
        } else {
            sysMenuModel.setIsEnable(STR_0);
        }
        sysMenuModel.setMenuId(MENU_ID_SUPER_MODE);
        sysMenuService.updateMenu(sysMenuModel);
    }

    /**
     * 邮件保存应用备份文件
     */
    @Override
    public void systemBackupToMail() {
        SysLogUtils.functionStart(logger, LOG_BUSINESS_TYPE_BACKUP_TO_MAIL);
        Boolean backupToMail = sysParameterService.getParameterBoolean(BACKUP_TO_MAIL);
        String backupMode = sysParameterService.getParameterString(BACKUP_MODE);
        if (backupToMail && StringUtils.isNotBlank(backupMode)) {
            String backupLocation = sysParameterService.getParameterString(BACKUP_LOCATION);
            if (StringUtils.isNotBlank(backupLocation)) {
                File[] backupFile = new File(backupLocation).listFiles();
                if (backupFile != null) {
                    Arrays.sort(backupFile, (o1, o2) -> {
                        long sort = o2.lastModified() - o1.lastModified();
                        if (sort > 0) {
                            return 1;
                        } else if (sort == 0) {
                            return 0;
                        } else {
                            return -1;
                        }
                    });
                    List<String> filePath = new ArrayList<>();
                    Map<String, Boolean> pathFlag = new HashMap<>(3);
                    for (File file : backupFile) {
                        String fileName = file.getName();
                        String[] fileNameSub = fileName.split(MINUS);
                        if (fileNameSub != null && fileNameSub.length == 2 && fileNameSub[0].length() > 8) {
                            String fileNameDate = fileNameSub[0].substring(0, 8);
                            String suffix = fileNameSub[1].split(BACKSLASH_POINT)[1].toLowerCase();
                            if (SysDateUtils.yyyyMMdd().equals(fileNameDate)) {
                                if (backupMode.contains(suffix) && !pathFlag.containsKey(suffix)) {
                                    pathFlag.put(suffix, true);
                                    filePath.add(file.getAbsolutePath());
                                }
                            }
                        }
                    }
                    if (CollectionUtils.isNotEmpty(filePath)) {
                        SysMailModel mailModel = new SysMailModel();
                        mailModel.setTo(RunDataConfig.MAIL_CONFIG.getMailFrom());
                        mailModel.setFrom(RunDataConfig.MAIL_CONFIG.getMailFrom());
                        mailModel.setSubject(MAIL_SUBJECT_BACKUP);
                        mailModel.setContent(MAIL_BACKUP_FILE);
                        mailModel.setFilePath(filePath);
                        sysMailService.sendMail(mailModel);
                    }
                }
            }
        }
        SysLogUtils.functionEnd(logger, LOG_BUSINESS_TYPE_BACKUP_TO_MAIL);
    }

    /**
     * 获取查询条件
     *
     * @param tableName
     * @return
     */
    private LinkedHashMap<String, String> getQueryCondition(SysTableQueryModel sysTableQueryModel, String tableName) {
        sysTableQueryModel.setTableName(tableName);
        // 查询数据表字段
        List<SysTableModel> sysTableColumns = sysSystemDao.selectTableColumn(sysTableQueryModel);
        // 拼装查询表数据查询字段
        LinkedHashMap<String, String> columnMap = new LinkedHashMap(16);
        StringBuffer queryColumn = new StringBuffer();
        for (SysTableModel sysTableModel : sysTableColumns) {
            queryColumn.append(sysTableModel.getColumnCode()).append(COMMA);
            columnMap.put(sysTableModel.getColumnCode(), sysTableModel.getColumnType());
        }
        String column = queryColumn.toString();
        sysTableQueryModel.setTableColumn(column.substring(0, column.length() - 1));
        sysTableQueryModel.setTableOrder(sysTableColumns.get(0).getColumnCode());
        // 查询表主键用于排序
        List<SysTableModel> sysTableKey = sysSystemDao.selectTablePrimaryKey(sysTableQueryModel);
        if (CollectionUtils.isNotEmpty(sysTableKey)) {
            StringBuffer tableKey = new StringBuffer();
            for (SysTableModel key : sysTableKey) {
                tableKey.append(key.getColumnCode()).append(COMMA);
            }
            if (tableKey.toString().endsWith(COMMA)) {
                tableKey = new StringBuffer(tableKey.toString().substring(0, tableKey.toString().lastIndexOf(COMMA)));
            }
            sysTableQueryModel.setTableOrder(tableKey.toString());
        }
        return columnMap;
    }

    /**
     * 获取字段值
     *
     * @param columnType
     * @param columnValue
     * @return
     */
    private String getColumnValue(String columnType, Object columnValue) {
        if (columnValue == null) {
            return null;
        }
        if (COLUMN_NUMBER.contains(columnType)) {
            return String.valueOf(columnValue);
        } else if (COLUMN_DATE.equals(columnType)) {
            return new StringBuffer(TO_DATE_LEFT).append(String.valueOf(columnValue).substring(0, 10)).append(TO_DATE_RIGHT).toString();
        } else if (columnType.contains(COLUMN_TIMESTAMP)) {
            return new StringBuffer(TO_TIMESTAMP_LEFT).append(String.valueOf(columnValue).substring(0, 19)).append(TO_TIMESTAMP_RIGHT).toString();
        } else {
            if (String.valueOf(columnValue).contains(AMPERSAND)) {
                return new StringBuffer(CHR_38).append(String.valueOf(columnValue).replace(AMPERSAND, STR_EMPTY)).append(SINGLE_QUOTES).toString();
            } else {
                return new StringBuffer(SINGLE_QUOTES).append(columnValue).append(SINGLE_QUOTES).toString();
            }
        }
    }

    /**
     * 初始化数据
     */
    private void init(String logType, String procedureType, String tableType, String dataType) {
        SysLogUtils.functionStart(logger, logType);
        try {
            Connection connection = getConnection();
            if (connection != null) {
                // 初始化存储过程
                InputStream file = new ClassPathResource(procedureType).getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file));
                StringBuffer content = new StringBuffer();
                String temp;
                while ((temp = bufferedReader.readLine()) != null) {
                    content.append(temp).append(NEXT_LINE);
                }
                if (StringUtils.isNotBlank(content.toString())) {
                    Statement statement = connection.createStatement();
                    String[] procedure = content.toString().trim().split(SYSTEM_PROCEDURE_SPLIT);
                    for (int i = 0; i < procedure.length; i++) {
                        if (i == 0) {
                            // 文件描述内容不执行
                            continue;
                        }
                        statement.execute(procedure[i]);
                    }
                }
                // 初始化数据
                ScriptRunner runner = new ScriptRunner(connection);
                Resources.setCharset(Charset.forName(UTF8));
                runner.setLogWriter(null);
                // 初始化表结构
                Reader reader = null;
                if (StringUtils.isNotBlank(tableType)) {
                    reader = Resources.getResourceAsReader(tableType);
                    runner.runScript(reader);
                }

                // 初始化基础数据
                reader = Resources.getResourceAsReader(dataType);
                runner.runScript(reader);

                // 关闭资源连接
                bufferedReader.close();
                reader.close();
                runner.closeConnection();
                connection.close();
            }
        } catch (IOException e) {
            SysLogUtils.exception(logger, logType, e);
        } catch (SQLException e) {
            SysLogUtils.exception(logger, logType, e);
        }
        SysLogUtils.functionEnd(logger, logType);
    }

    /**
     * 获取数据库连接驱动
     *
     * @return
     */
    private Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(datasourceConfigBean.getUrl(),
                    datasourceConfigBean.getUsername(), datasourceConfigBean.getPassword());
        } catch (SQLException e) {
            SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_INIT_SYSTEM, e);
        }
        return connection;
    }

    /**
     * 设置字典项
     *
     * @param sysUserModel
     * @param sysDictionaryModel
     * @param codeMap
     * @param codeMapBlank
     */
    private void setDictionaryItem(SysUserModel sysUserModel,
                                   SysDictionaryModel sysDictionaryModel,
                                   Map<String, Boolean> userDataAuthority,
                                   ConcurrentHashMap<String, List<SysDictionaryModel>> codeMap,
                                   ConcurrentHashMap<String, List<SysDictionaryModel>> codeMapBlank) {
        // 字典不存在
        if (codeMap.get(sysDictionaryModel.getDictionaryCode()) == null) {
            List<SysDictionaryModel> item = new ArrayList<>();
            List<SysDictionaryModel> itemBlank = new ArrayList<>();
            // 设置请选择选项
            SysDictionaryModel select = new SysDictionaryModel();
            BeanUtils.copyProperties(sysDictionaryModel, select);
            select.setDictionaryItem(STR_EMPTY);
            select.setDictionaryCaption(SELECT);
            select.setItemOrder(STR_0);
            itemBlank.add(select);
            if (isUserDictionary(sysUserModel, sysDictionaryModel, userDataAuthority)) {
                item.add(sysDictionaryModel);
                itemBlank.add(sysDictionaryModel);
            }
            codeMap.put(sysDictionaryModel.getDictionaryCode(), item);
            codeMapBlank.put(sysDictionaryModel.getDictionaryCode(), itemBlank);
        } else {
            // 字典存在
            if (isUserDictionary(sysUserModel, sysDictionaryModel, userDataAuthority)) {
                codeMap.get(sysDictionaryModel.getDictionaryCode()).add(sysDictionaryModel);
                codeMapBlank.get(sysDictionaryModel.getDictionaryCode()).add(sysDictionaryModel);
            }
        }
        DICTIONARY_CONDITION.put(sysUserModel.getUserId(), codeMap);
        DICTIONARY_CONDITION.put(new StringBuffer(sysUserModel.getUserId()).append(BLANK).toString(), codeMapBlank);
    }

    /**
     * 是否用户字典信息
     *
     * @param sysUserModel
     * @param sysDictionaryModel
     * @return
     */
    private Boolean isUserDictionary(SysUserModel sysUserModel,
                                     SysDictionaryModel sysDictionaryModel,
                                     Map<String, Boolean> userDataAuthority) {
        String dictionaryCode = sysDictionaryModel.getDictionaryCode();
        boolean flag = D000.equals(dictionaryCode) || D005.equals(dictionaryCode) || D009.equals(dictionaryCode);
        if (!selectDataAuthority(sysUserModel, userDataAuthority) && flag) {
            return sysUserModel.getUserId().equals(sysDictionaryModel.getUserId());
        }
        return true;
    }

    /**
     * 是否具有数据权限
     *
     * @param sysUserModel
     * @param userDataAuthority
     * @return
     */
    private Boolean selectDataAuthority(SysUserModel sysUserModel,
                                        Map<String, Boolean> userDataAuthority) {
        if (userDataAuthority.containsKey(sysUserModel.getUserId())) {
            return userDataAuthority.get(sysUserModel.getUserId());
        }
        if (ADMIN_CODE.equals(sysUserModel.getUserCode())) {
            userDataAuthority.put(sysUserModel.getUserId(), true);
            return true;
        } else {
            SysSystemQueryModel sysSystemQueryModel = new SysSystemQueryModel();
            sysSystemQueryModel.setUserId(sysUserModel.getUserId());
            Boolean hasDataAuthority = sysSystemDao.selectDataAuthority(sysSystemQueryModel);
            userDataAuthority.put(sysUserModel.getUserId(), hasDataAuthority);
            return hasDataAuthority;
        }
    }

    /**
     * 转义
     *
     * @param dictionaryCache
     * @param ele
     * @return
     */
    private LinkedHashMap transfer(Map dictionaryCache, LinkedHashMap ele, boolean transferAmout) {
        transferElement(dictionaryCache, ele, null, true, transferAmout);
        return ele;
    }

    /**
     * 转义具体值
     *
     * @param dictionaryCache
     * @param ele
     * @param clazz
     */
    private LinkedHashMap transferElement(Map dictionaryCache, LinkedHashMap ele, Class clazz, boolean filter, boolean transferAmout) {
        Iterator<String> iterator = ele.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (ele.get(key) == null) {
                continue;
            }
            String value = String.valueOf(ele.get(key));
            // 字典项转义
            if (StringUtils.isNotBlank(value) && value.split(MINUS).length == 2) {
                if (dictionaryCache.get(value) != null) {
                    ele.put(key, dictionaryCache.get(value));
                } else {
                    String dictionaryCode = value.split(MINUS)[0];
                    String dictionaryItem = value.split(MINUS)[1];
                    SysDictionaryQueryModel sysDictionaryQueryModel = new SysDictionaryQueryModel();
                    sysDictionaryQueryModel.setDictionaryCode(dictionaryCode);
                    sysDictionaryQueryModel.setDictionaryItem(dictionaryItem);
                    List<SysDictionaryModel> dictionaryList = sysDictionaryDao.selectSysDictionary(sysDictionaryQueryModel);
                    if (CollectionUtils.isNotEmpty(dictionaryList)) {
                        ele.put(key, dictionaryList.get(0).getDictionaryCaption());
                        dictionaryCache.put(value, dictionaryList.get(0).getDictionaryCaption());
                    }
                }
                continue;
            }
            // 配置key转义
            String[] keys = TRANSFER_KEY.split(COMMA);
            for (int i = 0; i < keys.length; i++) {
                if (keys[i].equalsIgnoreCase(key)) {
                    if (dictionaryCache.get(value) != null) {
                        ele.put(key, dictionaryCache.get(value));
                    } else {
                        transfer(dictionaryCache, ele, clazz, key, value);
                    }
                    break;
                }
            }
            // 金额格式化
            if (transferAmout && key.toLowerCase().contains(AMOUNT)) {
                ele.put(key, SysCommonUtils.formatValue(value));
            }
            if (filter) {
                // 字段过滤
                if (BACKUP_EXCEL_FILTER_COLUMN.contains(key.toLowerCase())) {
                    iterator.remove();
                }
                // 日期格式化
                if (key.toLowerCase().contains(DATE)) {
                    if (value.toLowerCase().contains(BACKUP_EXCEL_DATE_FORMAT)) {
                        String[] values = value.split(STR_SPACE);
                        if (values.length == 2) {
                            ele.put(key, values[0]);
                        }
                    } else {
                        if (value.length() > 19) {
                            ele.put(key, value.substring(0, 19));
                        }
                    }
                }
            }
        }
        return ele;
    }

    /**
     * 转义
     *
     * @param dictionaryCache
     * @param ele
     */
    private BaseModel transfer(Map dictionaryCache, Map ele, Class clazz, Boolean transferAmout) {
        return SysBeanUtils.mapToBean(clazz, SysBeanUtils.linkedHashMapToMap(transferElement(dictionaryCache,
                SysBeanUtils.mapToLinkedHashMap(ele), clazz, false, transferAmout)));
    }

    /**
     * 配置key转义
     *
     * @param dictionaryCache
     * @param ele
     * @param clazz
     * @param key
     * @param value
     */
    private void transfer(Map dictionaryCache, Map ele, Class clazz, String key, String value) {
        // 用户信息userId不转义
        if (!SysUserModel.class.equals(clazz)) {
            // 转义 userId
            SysUserQueryModel sysUserQueryModel = new SysUserQueryModel();
            sysUserQueryModel.setUserId(value);
            List<SysUserModel> sysUserList = sysUserDao
                    .selectSysUser(sysUserQueryModel);
            if (CollectionUtils.isNotEmpty(sysUserList)) {
                ele.put(key, sysUserList.get(0).getUserName());
                dictionaryCache.put(value, sysUserList.get(0).getUserName());
            }
        }
    }

    /**
     * 自定义排序
     */
    private class OrderedProperties extends Properties {

        private static final long serialVersionUID = -4627607243846121965L;

        private final LinkedHashSet<Object> keys = new LinkedHashSet<>();

        @Override
        public Enumeration<Object> keys() {
            return Collections.<Object>enumeration(keys);
        }

        @Override
        public Object put(Object key, Object value) {
            keys.add(key);
            return super.put(key, value);
        }

        @Override
        public Set<Object> keySet() {
            return keys;
        }

        @Override
        public Set<String> stringPropertyNames() {
            Set<String> set = new LinkedHashSet<>();
            for (Object key : this.keys) {
                set.add((String) key);
            }
            return set;
        }
    }

    /**
     * 配置参数过滤转换
     *
     * @param key
     * @return
     */
    private String convertValue(String key) {
        if (StringUtils.isNotBlank(configBean.getConvertOutputKeyword())
                && StringUtils.isNotBlank(key)) {
            String[] keywords = configBean.getConvertOutputKeyword().split(COMMA);
            for (String word : keywords) {
                if (key.contains(word)) {
                    return ASTERISK_SIX;
                }
            }
        }
        return environment.getProperty(key);
    }

    /**
     * 获取Excel配置表tab描述
     *
     * @param tableName 表名称
     * @return
     */
    private String getExcelInfo(String tableName) {
        String[] tables = BACKUP_EXCEL.split(SEMICOLON);
        if (tables != null) {
            for (String table : tables) {
                String[] tableInfo = table.split(COMMA);
                if (tableInfo[0].equals(tableName)) {
                    return tableInfo[1];
                }
            }
        }
        return STR_EMPTY;
    }

    /**
     * 是否需要备份Excel数据表
     *
     * @param tableName
     * @return
     */
    private boolean isBackupTable(String tableName) {
        return StringUtils.isNotBlank(getExcelInfo(tableName));
    }

    /**
     * 查询配置模块信息
     */
    @Override
    public SysModuleModel selectConfigModule() {
        SessionBean sessionBean = SysSessionUtils.getSession();
        if (sessionBean != null) {
            return selectConfigModule(sessionBean.getUserId());
        }
        return new SysModuleModel();
    }

    /**
     * 查询配置模块信息
     */
    @Override
    public SysModuleModel selectConfigModule(String userId) {
        SysModuleModel sysModuleModel = new SysModuleModel();
        SysConfigQueryModel sysConfigQueryModel = new SysConfigQueryModel();
        sysConfigQueryModel.setUserId(userId);
        sysConfigQueryModel.setModuleGroupCode(MODULE_CONSOLE);
        List<SysConfigModel> sysConfigModelList = sysConfigDao.selectModule(sysConfigQueryModel);
        if (CollectionUtils.isNotEmpty(sysConfigModelList)) {
            Map module = new HashMap(16);
            for (SysConfigModel sysConfigModel : sysConfigModelList) {
                module.put(sysConfigModel.getModuleCode(), sysConfigModel.getModuleStatus());
            }
            sysModuleModel = (SysModuleModel) SysBeanUtils.mapToBean(SysModuleModel.class, module);
        }
        return sysModuleModel;
    }

    /**
     * 保存模块信息
     *
     * @param sysModuleModel
     * @return
     */
    @Override
    public ResultData save(SysModuleModel sysModuleModel) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_CONSOLE, LOG_OPERATE_TYPE_UPDATE);
        SessionBean sessionBean = SysSessionUtils.getSession();
        if (sessionBean != null) {
            Map<String, Object> module = SysBeanUtils.beanToMap(sysModuleModel);
            SysConfigModel sysConfigModel = new SysConfigModel();
            sysConfigModel.setUserId(sessionBean.getUserId());
            Iterator<String> iterator = module.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                sysConfigModel.setModuleGroupCode(MODULE_CONSOLE);
                sysConfigModel.setModuleCode(key);
                String status = SWITCH_ON.equals(String.valueOf(module.get(key))) ? STR_1 : STR_0;
                sysConfigModel.setModuleStatus(status);
                sysConfigDao.save(sysConfigModel);
            }
            WebSocketServerConfig.sendMessageInfo(WEBSOCKET_TOPIC_NAME_CONSOLE, LOG_BUSINESS_TYPE_CONSOLE);
        }
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_CONSOLE, LOG_OPERATE_TYPE_UPDATE);
        return new ResultData(true, UPDATE_SUCCESS, null);

    }

    /**
     * 删除模块信息
     *
     * @param sysConfigModelList
     * @return
     */
    @Override
    public ResultData delete(List<SysConfigModel> sysConfigModelList) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_CONSOLE, LOG_OPERATE_TYPE_DELETE);
        sysConfigDao.delete(sysConfigModelList);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_CONSOLE, LOG_OPERATE_TYPE_DELETE);
        return new ResultData(true, LOG_OPERATE_TYPE_DELETE, null);
    }
}
