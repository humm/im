package im.config;

import im.service.SysInterfaceService;
import im.service.SysSystemService;
import im.service.SysWeChatFlowService;
import im.util.SysLogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import static im.consts.BaseCueConst.*;
/**
 * @author hoomoomoo
 * @description 初始化配置
 * @package com.hoomoomoo.im.config.bean
 * @date 2019/08/04
 */

@Component
@Order(1)
public class InitConfig implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitConfig.class);

    @Autowired
    private SysSystemService sysSystemService;

    @Autowired
    private SysWeChatFlowService sysWeChatFlowService;

    @Autowired
    private SysInterfaceService sysInterfaceService;

    @Override
    public void run(String... args) {
        // 系统初始化
        sysSystemService.initSystem();

        // 系统升级
        sysSystemService.updateSystem();

        // 加载配置sql
        sysSystemService.getConfigSql();

        // 加载系统参数
        sysSystemService.initParameter();

        // 输出系统配置参数至控制台
        sysSystemService.outputConfigParameter();

        // 加载业务ID数据
        sysSystemService.loadBusinessId();

        // 加载查询数据字典
        sysSystemService.loadSysDictionaryCondition();

        // 加载微信流程处理步骤
        sysWeChatFlowService.getWeChatFlow(true);

        // 超级模式启动
        sysSystemService.startSuperMode();

        // 系统启动备份
        sysSystemService.startBackup();

        // 处理邮件申请数据
        sysInterfaceService.startMail();

        // 发送备份文件至邮箱
        sysSystemService.systemBackupToMail();

        SysLogUtils.load(logger, LOG_BUSINESS_TYPE_INIT);
    }
}
