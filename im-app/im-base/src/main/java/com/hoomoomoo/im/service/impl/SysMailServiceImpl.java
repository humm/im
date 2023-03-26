package com.hoomoomoo.im.service.impl;

import com.hoomoomoo.im.config.RunDataConfig;
import com.hoomoomoo.im.model.SysMailModel;
import com.hoomoomoo.im.util.SysLogUtils;
import com.hoomoomoo.im.service.SysMailService;
import com.sun.mail.imap.IMAPFolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.BaseCueConst.*;

/**
 * @author hoomoomoo
 * @description 邮件服务实现类
 * @package com.hoomoomoo.im.service.impl
 * @date 2019/09/20
 */

@Service
@Transactional
public class SysMailServiceImpl implements SysMailService {

    private static final Logger logger = LoggerFactory.getLogger(SysMailServiceImpl.class);

    private JavaMailSenderImpl javaMailSender;

    /**
     * 发送邮件
     *
     * @param mailModel
     * @return
     */
    @Override
    public Boolean sendMail(SysMailModel mailModel) {
        if (checkSendConfig(mailModel)) {
            SysLogUtils.error(logger, MAIL_NOT_SET);
            return false;
        }
        SysLogUtils.functionStart(logger, LOG_BUSINESS_TYPE_MAIL_SEND);
        setMailSender();
        boolean isSend = true;
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            if (CollectionUtils.isNotEmpty(mailModel.getFilePath())) {
                mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
                for (String filePath : mailModel.getFilePath()) {
                    mimeMessageHelper.addAttachment(new File(filePath).getName(), new File(filePath));
                }
            }
            mimeMessageHelper.setTo(mailModel.getTo());
            mimeMessageHelper.setFrom(mailModel.getFrom());
            mimeMessageHelper.setSubject(mailModel.getSubject());
            mimeMessageHelper.setText(mailModel.getContent(), true);
            javaMailSender.send(mimeMessage);
            SysLogUtils.success(logger, LOG_BUSINESS_TYPE_MAIL_SEND);
        } catch (Exception e) {
            isSend = false;
            SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_MAIL_SEND, e);
        }
        SysLogUtils.functionEnd(logger, LOG_BUSINESS_TYPE_MAIL_SEND);
        return isSend;
    }

    /**
     * 接收指定主题邮件
     *
     * @param mailModel
     * @return
     */
    @Override
    public List<SysMailModel> receiveMail(SysMailModel mailModel) {
        SysLogUtils.functionStart(logger, LOG_BUSINESS_TYPE_MAIL_RECEIVE);
        List<SysMailModel> mailModelList = new ArrayList<>();
        Properties properties = new Properties();
        Session session = Session.getDefaultInstance(properties);
        session.setDebug(STR_1.equals(RunDataConfig.MAIL_CONFIG.getMailDebug()));
        try {
            Store store = session.getStore(RunDataConfig.MAIL_CONFIG.getMailReceiveProtocol());
            store.connect(RunDataConfig.MAIL_CONFIG.getMailReceiveHost(), RunDataConfig.MAIL_CONFIG.getMailReceiveUsername(), RunDataConfig.MAIL_CONFIG.getMailReceivePassword());
            Folder folder = store.getFolder(RunDataConfig.MAIL_CONFIG.getMailReceiveFolder());
            // 设置对邮件帐户的访问权限可以读写
            folder.open(Folder.READ_WRITE);
            Message[] messages = folder.getMessages();
            for (Message message : messages) {
                try {
                    if (message.getFlags().contains(Flags.Flag.SEEN)) {
                        // 已读邮件不处理
                        continue;
                    }
                    if (ASTERISK.equals(mailModel.getSubject()) || mailModel.getSubject().equals(message.getSubject())) {
                        // 邮件状态置为已读
                        message.setFlag(Flags.Flag.SEEN, true);
                        // 处理邮件内容
                        Long uuid = ((IMAPFolder) folder).getUID(message);
                        if (StringUtils.isNotBlank(mailModel.getMailId())) {
                            String[] mailIds = mailModel.getMailId().split(MINUS);
                            if (mailIds != null && mailIds.length == 3) {
                                Long id = Long.parseLong(mailIds[2]);
                                String mailHost = mailIds[0];
                                String mailUsername = mailIds[1];
                                if (RunDataConfig.MAIL_CONFIG.getMailReceiveHost().equals(mailHost) && RunDataConfig.MAIL_CONFIG.getMailReceiveUsername().equals(mailUsername)) {
                                    if (id >= uuid) {
                                        continue;
                                    }
                                }
                                String mailId =
                                        new StringBuffer(RunDataConfig.MAIL_CONFIG.getMailReceiveHost()).append(MINUS)
                                                .append(RunDataConfig.MAIL_CONFIG.getMailReceiveUsername()).append(MINUS).append(uuid).toString();
                                mailModelList.add(handleMailData(mailId, message));
                            }
                        }
                    }
                } catch (FolderClosedException e) {
                    SysLogUtils.exception(logger, String.format(EXCEPTION_NOT_HANDLE, e.getMessage()));
                } catch (MessagingException e) {
                    SysLogUtils.exception(logger, String.format(EXCEPTION_NOT_HANDLE, e.getMessage()));
                }
            }
        } catch (Exception e) {
            SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_MAIL_RECEIVE, e);
        }
        SysLogUtils.functionEnd(logger, LOG_BUSINESS_TYPE_MAIL_RECEIVE);
        return mailModelList;
    }

    /**
     * 处理邮件内容
     *
     * @param mailId
     * @param message
     */
    private SysMailModel handleMailData(String mailId, Message message) {
        SysLogUtils.functionStart(logger, LOG_BUSINESS_TYPE_MAIL_HANDLE);
        MimeMessage mimeMessage = (MimeMessage) message;
        SysMailModel mailModel = new SysMailModel();
        try {
            // 获取发件人地址
            String address = mimeMessage.getSender().toString();
            if (StringUtils.isNotBlank(address)) {
                int indexStart = address.lastIndexOf(LESS_THAN);
                int indexEnd = address.lastIndexOf(GREATER_THAN);
                if (indexEnd >= indexStart) {
                    mailModel.setTo(address.substring(indexStart + 1, indexEnd));
                }
            }
            // 获取邮件内容
            Object content = mimeMessage.getContent();
            if (content instanceof String) {
                mailModel.setSubject(mimeMessage.getSubject());
                mailModel.setContent(String.valueOf(content));
                mailModel.setMailId(mailId);
            } else if (content instanceof MimeMultipart) {
                MimeMultipart mimeMultipart = (MimeMultipart) mimeMessage.getContent();
                inner:
                for (int i = 0; i < mimeMultipart.getCount(); i++) {
                    BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                    if (bodyPart.isMimeType(TEXT_PLAIN)) {
                        // 文本内容
                        mailModel.setSubject(mimeMessage.getSubject());
                        mailModel.setContent(String.valueOf(bodyPart.getContent()));
                        mailModel.setMailId(mailId);
                        break inner;
                    } else if (bodyPart.isMimeType(TEXT_HTML)) {
                        // 超文本内容 暂不处理
                    } else if (bodyPart.isMimeType(MULTIPART)) {
                        // 附件 暂不处理
                    }
                }
            } else {
                SysLogUtils.fail(logger, LOG_BUSINESS_TYPE_MAIL_HANDLE, MAIL_CONTENT_NOT_SUPPORT);
            }
        } catch (Exception e) {
            SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_MAIL_HANDLE, e);
        }
        SysLogUtils.functionEnd(logger, LOG_BUSINESS_TYPE_MAIL_HANDLE);
        return mailModel;
    }

    /**
     * 设置邮件发送基础信息
     */
    private void setMailSender() {
        if (javaMailSender == null) {
            javaMailSender = new JavaMailSenderImpl();
        }
        javaMailSender.setHost(RunDataConfig.MAIL_CONFIG.getMailHost());
        javaMailSender.setProtocol(RunDataConfig.MAIL_CONFIG.getMailProtocol());
        javaMailSender.setDefaultEncoding(RunDataConfig.MAIL_CONFIG.getMailEncoding());
        javaMailSender.setUsername(RunDataConfig.MAIL_CONFIG.getMailUsername());
        javaMailSender.setPassword(RunDataConfig.MAIL_CONFIG.getMailPassword());
    }

    /**
     * 校验邮件发送参数
     *
     * @return
     */
    private Boolean checkSendConfig(SysMailModel mailModel) {
        return StringUtils.isBlank(mailModel.getFrom()) || StringUtils.isBlank(mailModel.getTo())
                || StringUtils.isBlank(mailModel.getSubject()) || StringUtils.isBlank(mailModel.getContent());
    }

    /**
     * 校验邮件发送参数
     *
     * @return
     */
    private Boolean checkSendConfig() {
        if (RunDataConfig.MAIL_CONFIG == null) {
            return true;
        }
        return StringUtils.isBlank(RunDataConfig.MAIL_CONFIG.getMailFrom()) || StringUtils.isBlank(RunDataConfig.MAIL_CONFIG.getMailUsername())
                || StringUtils.isBlank(RunDataConfig.MAIL_CONFIG.getMailPassword()) || StringUtils.isBlank(RunDataConfig.MAIL_CONFIG.getMailHost())
                || StringUtils.isBlank(RunDataConfig.MAIL_CONFIG.getMailProtocol()) || StringUtils.isBlank(RunDataConfig.MAIL_CONFIG.getMailDebug().toString())
                || StringUtils.isBlank(RunDataConfig.MAIL_CONFIG.getMailEncoding());
    }


    /**
     * 校验邮件读取参数
     *
     * @return
     */
    private Boolean checkReceiveConfig() {
        if (RunDataConfig.MAIL_CONFIG == null) {
            return true;
        }
        return StringUtils.isBlank(RunDataConfig.MAIL_CONFIG.getMailSubject()) || StringUtils.isBlank(RunDataConfig.MAIL_CONFIG.getMailReceiveFolder())
                || StringUtils.isBlank(RunDataConfig.MAIL_CONFIG.getMailReceiveHost()) || StringUtils.isBlank(RunDataConfig.MAIL_CONFIG.getMailReceiveUsername())
                || StringUtils.isBlank(RunDataConfig.MAIL_CONFIG.getMailReceivePassword()) || StringUtils.isBlank(RunDataConfig.MAIL_CONFIG.getMailReceiveProtocol());
    }

    /**
     * 邮件参数配置信息校验
     *
     * @return
     */
    @Override
    public Boolean checkMailConfig() {
        return checkSendConfig() || checkReceiveConfig();
    }

}
