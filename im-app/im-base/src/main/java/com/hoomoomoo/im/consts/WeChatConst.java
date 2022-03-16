package com.hoomoomoo.im.consts;

/**
 * @author hoomoomoo
 * @description 微信常量类
 * @package com.hoomoomoo.im.consts
 * @date 2020/02/28
 */

public class WeChatConst {

    /**
     * 微信请求
     */
    public static final String WECHAT_REQUEST = "/wechat/request.do";

    /**
     * 请求类型: GET
     */
    public static final String REQUEST_TYPE_GET = "GET";

    /**
     * token
     */
    public static final String TOKEN = "family";

    /**
     * 请求相关字段
     */
    public static final String PARAMETER_SIGNATURE = "signature";
    public static final String PARAMETER_TIMESTAMP = "timestamp";
    public static final String PARAMETER_NONCE = "nonce";
    public static final String PARAMETER_ECHOSTR = "echostr";
    public static final String PARAMETER_FROMUSERNAME = "FromUserName";
    public static final String PARAMETER_TOUSERNAME = "ToUserName";
    public static final String PARAMETER_MSGTYPE = "MsgType";
    public static final String PARAMETER_CONTENT = "Content";
    public static final String PARAMETER_CREATETIME = "CreateTime";
    public static final String PARAMETER_MSGID = "MsgId";
    public static final String PARAMETER_EVENTKEY = "EventKey";
    public static final String PARAMETER_EVENT = "Event";

    /**
     * 算法 SHA1
     */
    public static final String ALGORITHM_SHA1 = "SHA1";

    /**
     * 消息类型：文本
     */
    public static final String MESSAGE_TYPE_TEXT = "text";

    /**
     * 消息类型：事件
     */
    public static final String MESSAGE_TYPE_EVENT = "event";

    /**
     * 消息类型：订阅
     */
    public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";

    /**
     * 消息类型：退订
     */
    public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";

    /**
     * 流程步骤：身份认证
     */
    public static final String FLOW_CODE_AUTH = "auth";

    /**
     * 流程步骤：用户信息绑定
     */
    public static final String FLOW_CODE_BIND = "bind";

    /**
     * 流程步骤：服务选择
     */
    public static final String FLOW_CODE_SELECT = "select";

    /**
     * 流程步骤：收入查询 - 月度
     */
    public static final String FLOW_CODE_INCOME_MONTH = "income-month";

    /**
     * 流程步骤：收入查询 - 年度
     */
    public static final String FLOW_CODE_INCOME_YEAR = "income-year";

    /**
     * 流程步骤：收入查询 - 本年度
     */
    public static final String FLOW_CODE_INCOME_YEAR_CURRENT = "income-year-current";

    /**
     * 流程步骤：收入查询 - 总收入
     */
    public static final String FLOW_CODE_INCOME_ALL = "income-all";

    /**
     * 流程步骤：收入新增
     */
    public static final String FLOW_CODE_INCOME_ADD = "income-add";

    /**
     * 流程步骤：收入新增同上一笔
     */
    public static final String FLOW_CODE_INCOME_ADD_SAME = "income-add-same";

    /**
     * 流程步骤：收入修改
     */
    public static final String FLOW_CODE_INCOME_UPDATE = "income-update";

    /**
     * 流程步骤：收入删除
     */
    public static final String FLOW_CODE_INCOME_DELETE = "income-delete";

    /**
     * 流程步骤：收入新增操作
     */
    public static final String FLOW_CODE_INCOME_ADD$ = "income-add$";

    /**
     * 流程步骤：收入新增操作
     */
    public static final String FLOW_CODE_INCOME_ADD_SAME$ = "income-add-same$";

    /**
     * 流程步骤：收入删除操作
     */
    public static final String FLOW_CODE_INCOME_DELETE$ = "income-delete$";

    /**
     * 流程步骤：更多服务
     */
    public static final String FLOW_CODE_MORE = "more";

    /**
     * 流程步骤：随礼查询 - 自由查询
     */
    public static final String FLOW_CODE_GIFT_FREE = "gift-free";

    /**
     * 流程步骤：随礼查询 - 自由查询
     */
    public static final String FLOW_CODE_GIFT_FREE$ = "gift-free$";

    /**
     * 流程步骤：随礼查询 - 最近一次
     */
    public static final String FLOW_CODE_GIFT_LAST = "gift-last";

    /**
     * 流程步骤：随礼查询 - 年度
     */
    public static final String FLOW_CODE_GIFT_YEAR = "gift-year";

    /**
     * 流程步骤：随礼查询 - 本年度
     */
    public static final String FLOW_CODE_GIFT_YEAR_CURRENT = "gift-year-current";

    /**
     * 流程步骤：随礼查询 - 总随礼
     */
    public static final String FLOW_CODE_GIFT_ALL = "gift-all";

    /**
     * 流程步骤：随礼新增
     */
    public static final String FLOW_CODE_GIFT_ADD = "git-add";

    /**
     * 流程步骤：随礼修改
     */
    public static final String FLOW_CODE_GIFT_UPDATE = "git-update";

    /**
     * 流程步骤：随礼删除
     */
    public static final String FLOW_CODE_GIFT_DELETE = "gift-delete";

    /**
     * 流程步骤：随礼新增操作
     */
    public static final String FLOW_CODE_GIFT_ADD$ = "git-add$";

    /**
     * 流程步骤：随礼删除操作
     */
    public static final String FLOW_CODE_GIFT_DELETE$ = "gift-delete$";

    /**
     * 流程步骤：返回主菜单
     */
    public static final String FLOW_CODE_MAIN = "main";


}
