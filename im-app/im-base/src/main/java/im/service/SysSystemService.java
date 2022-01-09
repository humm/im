package im.service;

import im.model.SysConfigModel;
import im.model.SysModuleModel;
import im.model.base.BaseModel;
import im.model.base.ResultData;
import im.model.base.ViewData;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author hoomoomoo
 * @description 系统级别公用服务
 * @package im.service.common
 * @date 2019/08/04
 */

public interface SysSystemService {

    /**
     * 控制台输出应用配置参数
     */
    void outputConfigParameter();

    /**
     * 加载业务ID
     *
     * @return
     */
    void loadBusinessId();

    /**
     * 根据业务类型获取业务ID
     *
     * @param businessType
     * @return
     */
    String getBusinessSerialNo(String businessType);

    /**
     * 字典转义
     *
     * @param list
     * @param clazz
     * @return
     */
    List transferData(List list, Class clazz);

    /**
     * 字典转义
     *
     * @param list
     * @return
     */
    List<LinkedHashMap> transferData(List<LinkedHashMap> list);

    /**
     * 字典转义
     *
     * @param baseModel
     * @param clazz
     * @return
     */
    void transferData(BaseModel baseModel, Class clazz);

    /**
     * 字典转义
     *
     * @param baseModel
     * @param clazz
     * @param transferAmout
     */
    void transferData(BaseModel baseModel, Class clazz, Boolean transferAmout);

    /**
     * 加载所有字典查询条件
     *
     * @return
     */
    void loadSysDictionaryCondition();

    /**
     * 获取用户ID
     *
     * @return
     */
    String getUserId();

    /**
     * 设置查询条件
     *
     * @param viewData
     */
    void setCondition(ViewData viewData);

    /**
     * 查询按钮权限
     *
     * @param menuId
     * @return
     */
    Boolean selectButtonAuthority(String menuId);

    /**
     * 获取用户密码
     *
     * @return
     */
    String selectUserPassword();

    /**
     * 系统初始化
     */
    void initSystem();

    /**
     * 系统升级
     */
    void updateSystem();

    /**
     * 参数初始化
     */
    void initParameter();

    /**
     * 加载配置sql
     */
    void getConfigSql();

    /**
     * 系统备份文件
     *
     * @param fileName
     */
    ResultData systemBackupFile(String fileName);

    /**
     * 系统备份dmp
     *
     * @param fileName
     */
    ResultData systemBackupDmp(String fileName);

    /**
     * 系统备份dmp
     *
     * @param fileName
     */
    ResultData systemBackupExcel(String fileName);

    /**
     * 应用启动备份
     * @return
     */
    void startBackup();

    /**
     * 应用数据备份
     * @return
     */
    void systemBackup();

    /**
     * 超级模式启动
     */
    void startSuperMode();

    /**
     * 邮件保存应用备份文件
     */
    void systemBackupToMail();

    /**
     * 查询模块配置信息
     *
     * @return
     */
    SysModuleModel selectConfigModule();

    /**
     * 查询配置模块信息
     *
     */
    SysModuleModel selectConfigModule(String userId);

    /**
     * 保存模块信息
     *
     * @param sysModuleModel
     * @return
     */
    ResultData save(SysModuleModel sysModuleModel);

    /**
     * 删除模块信息
     *
     * @param sysConfigModelList
     * @return
     */
    ResultData delete(List<SysConfigModel> sysConfigModelList);

}
