package com.hoomoomoo.im.model;

import lombok.Data;


/**
 * @author hoomoomoo
 * @description 邮件配置实体类
 * @package com.hoomoomoo.im.config.bean
 * @date 2019/08/04
 */

@Data
public class MailConfigModel extends BaseModel {

    /**
     * 邮件发送是否启用debug模式
     */
    private String mailDebug;

    /**
     * 邮件发送host
     */
    private String mailHost;

    /**
     * 邮件发送用户名
     */
    private String mailUsername;

    /**
     * 邮件发送用户密码
     */
    private String mailPassword;

    /**
     * 邮件发送协议
     */
    private String mailProtocol;

    /**
     * 邮件收件人
     */
    private String mailTo;

    /**
     * 邮件发送人
     */
    private String mailFrom;

    /**
     * 邮件发送编码规则
     */
    private String mailEncoding;

    /**
     * 邮件读取主题
     */
    private String mailSubject;

    /**
     * 邮件读取host
     */
    private String mailReceiveHost;

    /**
     * 邮件读取用户名
     */
    private String mailReceiveUsername;

    /**
     * 邮件读取密码
     */
    private String mailReceivePassword;

    /**
     * 邮件读取协议
     */
    private String mailReceiveProtocol;

    /**
     * 邮件读取类型
     */
    private String mailReceiveFolder;
}
