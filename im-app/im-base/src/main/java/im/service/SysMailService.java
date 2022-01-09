package im.service;

import im.model.SysMailModel;

import java.util.List;

/**
 * @author hoomoomoo
 * @description 邮件服务类
 * @package im.service
 * @date 2019/09/20
 */
public interface SysMailService {

    /**
     * 发送邮件
     *
     * @param mailModel
     * @return
     */
    Boolean sendMail(SysMailModel mailModel);

    /**
     * 接收指定主题邮件
     *
     * @param mailModel
     * @return
     */
    List<SysMailModel> receiveMail(SysMailModel mailModel);

    /**
     * 邮件参数配置信息校验
     *
     * @return
     */
    Boolean checkMailConfig();

}
