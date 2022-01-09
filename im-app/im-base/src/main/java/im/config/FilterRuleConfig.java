package im.config;


import im.util.SysLogUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;

import static im.consts.BaseConst.FILTER_URL_PATTERNS;
import static im.consts.BaseCueConst.LOG_BUSINESS_TYPE_FILTER;

/**
 * @author hoomoomoo
 * @description 系统拦截器配置
 * @package im.config
 * @date 2019/09/01
 */

@Configuration
public class FilterRuleConfig {

    private static final Logger logger = LoggerFactory.getLogger(FilterRuleConfig.class);

    @PostConstruct
    public void init() {
        SysLogUtils.load(logger, LOG_BUSINESS_TYPE_FILTER);
    }

    @Bean
    public FilterRegistrationBean LoginFilterConfig() {
        return configFilter(new LoginFilterConfig(), LoginFilterConfig.class.getName(), FILTER_URL_PATTERNS, null, null, 1);
    }

    @Bean
    public FilterRegistrationBean AuthFilterConfig() {
        return configFilter(new AuthFilterConfig(), AuthFilterConfig.class.getName(), FILTER_URL_PATTERNS, null, null, 2);

    }

    /**
     * 配置过滤器
     *
     * @param filter
     * @param filterName
     * @param urlPatterns
     * @param initParameterKey
     * @param initParameterValue
     * @param order
     * @return
     */
    private FilterRegistrationBean configFilter(Filter filter, String filterName, String urlPatterns, String initParameterKey, String initParameterValue, int order) {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(filter);
        //设置过滤器名称
        filterRegistrationBean.setName(filterName);
        //配置过滤规则
        filterRegistrationBean.addUrlPatterns(urlPatterns);
        //设置init参数
        if (StringUtils.isNotBlank(initParameterKey)) {
            filterRegistrationBean.addInitParameter(initParameterKey, initParameterValue);
        }
        //执行次序
        filterRegistrationBean.setOrder(order);
        return filterRegistrationBean;
    }

}
