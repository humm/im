package com.hoomoomoo.im.config.bean;

import com.hoomoomoo.im.consts.BaseConst;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import static com.hoomoomoo.im.consts.BaseConst.APPLICATION_PROPERTIES;
import static com.hoomoomoo.im.consts.BaseConst.SCHEDULE;

/**
 * @author hoomoomoo
 * @description 定时任务配置
 * @package com.hoomoomoo.im.config.dto
 * @date 2019/08/03
 */

@Component
@PropertySource({BaseConst.APPLICATION_PROPERTIES})
@ConfigurationProperties(prefix = BaseConst.SCHEDULE)
@Data
public class ScheduleConfigBean {

    /**
     * 定时任务线程大小
     */
    private Integer poolSize;

    /**
     * 定时任务线程名称前缀
     */
    private String threadNamePrefix;

    /**
     * 优雅停机：线程池中任务等待时间
     */
    private Integer awaitTerminationSeconds;

    /**
     * 优雅停机：是否等待所有线程执行完
     */
    private Boolean waitForTasksToCompleteOnShutdown;

    /**
     * 是否开启邮件读取定时任务
     */
    private Boolean mail;

    /**
     * 邮件读取定时任务执行规则
     */
    private String mailCron;

}
