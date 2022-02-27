package im.schedule;

import im.service.SysInterfaceService;
import im.util.SysLogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;

import static im.consts.BaseConst.COLON;
import static im.consts.BaseCueConst.*;

/**
 * @author hoomoomoo
 * @description 邮件数据处理定时任务
 * @package com.hoomoomoo.im.schedule
 * @date 2020/02/10
 */

@Configuration
@EnableScheduling
@ConditionalOnExpression("${family.schedule.mail:true}")
public class SysMailSchedule implements SchedulingConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(SysMailSchedule.class);

    @Value("${family.schedule.mailCron:*/30 * * * * ?}")
    private String cron;

    @Autowired
    private SysInterfaceService sysInterfaceService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.addTriggerTask(() ->{
            try {
                SysLogUtils.info(logger, String.format(BUSINESS_SCHEDULE_MAIL, LOG_OPERATE_TAG_START));
                sysInterfaceService.handleMailRequest();
                SysLogUtils.info(logger, String.format(BUSINESS_SCHEDULE_MAIL, LOG_OPERATE_TAG_END));
            } catch (Exception e) {
                SysLogUtils.exception(logger, BUSINESS_SCHEDULE_MAIL.split(COLON)[0], e);
            }
        }, (triggerContext) ->{
            CronTrigger trigger = new CronTrigger(cron);
            Date nextExecDate = trigger.nextExecutionTime(triggerContext);
            return nextExecDate;
        });
    }
}
