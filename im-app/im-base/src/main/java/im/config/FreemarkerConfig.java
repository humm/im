package im.config;

import im.config.bean.SystemConfigBean;
import im.consts.BaseConst;
import im.consts.BaseCueConst;
import im.util.SysLogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hoomoomoo
 * @description freeMarker配置类
 * @package im.config
 * @date 2019/08/10
 */

@Configuration
public class FreemarkerConfig {

    private static final Logger logger = LoggerFactory.getLogger(FreemarkerConfig.class);

    @Autowired
    private SystemConfigBean systemConfigBean;

    @Bean
    public FreeMarkerConfigurer freemarkerViewConfig() {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setTemplateLoaderPath(systemConfigBean.getFreemarkerTemplateLoaderPath());
        configurer.setDefaultEncoding(systemConfigBean.getFreemarkerCharset());
        configurer.setPreferFileSystemAccess(false);
        Map<String, Object> freemarkerVariables = new HashMap<>(1);
        freemarkerVariables.put(BaseConst.APP_NAME, systemConfigBean.getAppName());
        freemarkerVariables.put(BaseConst.APP_DESCRIBE, systemConfigBean.getAppDescribe());
        configurer.setFreemarkerVariables(freemarkerVariables);
        SysLogUtils.load(logger, BaseCueConst.LOG_BUSINESS_TYPE_FREEMARKER);
        return configurer;
    }
}
