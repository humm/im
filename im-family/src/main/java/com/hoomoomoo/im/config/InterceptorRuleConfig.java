package com.hoomoomoo.im.config;

import com.hoomoomoo.im.util.SysLogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import static com.hoomoomoo.im.consts.BusinessCueConst.*;

/**
 * @author hoomoomoo
 * @description 拦截器配置
 * @package com.hoomoomoo.im.config
 * @date 2019/09/01
 */

@Configuration
public class InterceptorRuleConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(InterceptorRuleConfig.class);

    @Autowired
    private InterceptorConfig interceptorConfig;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 自定义拦截器 添加拦截路径和排除拦截路径
        registry.addInterceptor(interceptorConfig).addPathPatterns("/**");

        // .addPathPatterns("/**"); 所有请求
        // .addPathPatterns("/interface/**"); 指定请求
        // .excludePathPatterns("/static"); 不拦截请求
        SysLogUtils.load(logger, LOG_BUSINESS_TYPE_INTERCEPTOR);
    }

}
