package com.hoomoomoo.im.config;

import com.hoomoomoo.im.service.SysSystemService;
import com.hoomoomoo.im.util.SysLogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.hoomoomoo.im.consts.BaseCueConst.*;
/**
 * @author hoomoomoo
 * @description 系统拦截器
 * @package com.hoomoomoo.im.config
 * @date 2019/09/01
 */

@Configuration
public class InterceptorConfig implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(InterceptorConfig.class);

    @Autowired
    private SysSystemService sysSystemService;

    @PostConstruct
    public void init(){
        SysLogUtils.load(logger, LOG_BUSINESS_TYPE_INTERCEPTOR_CONFIG);
    }

    /**
     * 请求完成后 执行前提preHandle方法返回true
     */
    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3) {

    }

    /**
     * controller处理后 执行前提preHandle方法返回true
     */
    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3) {

    }

    /**
     * controller处理前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) {
        // 加载系统参数
        sysSystemService.initParameter();
        return true;
    }
}
