package com.hoomoomoo.im.config;

import com.hoomoomoo.im.model.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.hoomoomoo.im.consts.BusinessConst.STR_EMPTY;

/**
 * @author hoomoomoo
 * @description 运行内存数据
 * @package com.hoomoomoo.im.config
 * @date 2019/08/08
 */

public class RunDataConfig extends BaseRunDataConfig{

    /**
     * 业务序列号
     */
    public static ConcurrentHashMap<String, String> BUSINESS_SERIAL_NO      = new ConcurrentHashMap<>(16);

    /**
     * 配置sql
     */
    public static ConcurrentHashMap<String, SysSqlMode> CONFIG_SQL          = new ConcurrentHashMap<>(16);

    /**
     * 字典项查询条件
     */
    public static ConcurrentHashMap<String, ConcurrentHashMap<String, List<SysDictionaryModel>>> DICTIONARY_CONDITION = new ConcurrentHashMap<>(16);

    /**
     * 微信操作节点
     */
    public static ConcurrentHashMap<String, SysWeChatOperateModel> WECHAT_FLOW_OPERATE = new ConcurrentHashMap<>(16);

    /**
     * 微信流程步骤
     */
    public static LinkedHashMap<String, SysWeChatFlowModel> WECHAT_FLOW_LIST = new LinkedHashMap<>(16);

    /**
     * 微信流程步骤：功能序号与步骤代码对应关系
     */
    public static ConcurrentHashMap<String, String> WECHAT_FLOW_NUM_TO_CODE = new ConcurrentHashMap<>(16);

    /**
     * 邮件配置信息类
     */
    public static MailConfigModel MAIL_CONFIG                               = null;

    /**
     * 微信操作主菜单
     */
    public static String WECHAT_MAIN_FLOW_LIST                              = null;

    /**
     * 微信操作更多菜单
     */
    public static String WECHAT_MAIN_FLOW_MORE_LIST                         = null;

    /**
     * 智能填充
     */
    public static Boolean MIND_FILL                                         = true;

    /**
     * 邮件数据处理标识
     */
    public static Boolean MAIL_HANDLE_FLAG                                  = false;

    /**
     * 系统使用状态
     */
    public static String SYSTEM_USED_STATUS                                 = null;
}
