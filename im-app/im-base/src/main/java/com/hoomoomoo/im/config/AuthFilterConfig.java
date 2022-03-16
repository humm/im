package com.hoomoomoo.im.config;

import com.hoomoomoo.im.consts.WeChatConst;
import com.hoomoomoo.im.model.SysMenuQueryModel;
import com.hoomoomoo.im.model.base.SessionBean;
import com.hoomoomoo.im.service.SysMenuService;
import com.hoomoomoo.im.util.SysLogUtils;
import com.hoomoomoo.im.util.SysSessionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.BaseCueConst.*;

/**
 * @author hoomoomoo
 * @description 权限过滤器
 * @package com.hoomoomoo.im.config
 * @date 2020/03/21
 */

public class AuthFilterConfig implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AuthFilterConfig.class);

    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public void init(FilterConfig filterConfig) {
        if (sysMenuService == null) {
            ServletContext servletContext = filterConfig.getServletContext();
            ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            sysMenuService = ctx.getBean("sysMenuServiceImpl", SysMenuService.class);
        }
        SysLogUtils.load(logger, LOG_BUSINESS_TYPE_AUTH_FILTER);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String servletPath = request.getServletPath();
        if (WeChatConst.WECHAT_REQUEST.contains(servletPath) || servletPath.contains(SWAGGER_REQUEST) || servletPath.contains(SWAGGER_API_DOCS_REQUEST)) {
            LoginFilterConfig.isSwagger(request, servletPath);
            filterChain.doFilter(request, response);
            return;
        }
        String menuId = request.getParameter(STR_KEY_MEND_ID);
        if (StringUtils.isNotBlank(menuId)) {
            SessionBean sessionBean = SysSessionUtils.getSession();
            if (sessionBean != null) {
                if (!STR_KEY_MEND_ID_SKIP.equals(menuId) && !ADMIN_CODE.equals(sessionBean.getUserCode()) && !servletPath.startsWith(PAGE_ROLE)) {
                    SysMenuQueryModel sysMenuQueryModel = new SysMenuQueryModel();
                    sysMenuQueryModel.setUserId(sessionBean.getUserId());
                    sysMenuQueryModel.setMenuId(menuId);
                    if (!sysMenuService.selectMenuAuthority(sysMenuQueryModel)) {
                        // 用户无权限返回错误页面
                        request.setAttribute(MESSAGE, TIPS_ERROR_AUTH);
                        request.getRequestDispatcher(PAGE_ERROR).forward(request, response);
                        return;
                    }
                }
            }
        } else {
            SessionBean sessionBean = SysSessionUtils.getSession();
            if (sessionBean != null && sessionBean.getIsSwagger()) {
                filterChain.doFilter(request, response);
                return;
            }
            int index = servletPath.lastIndexOf(SLASH);
            String requestSuffix = servletPath.substring(index + 1);
            String ignoreRequest = new StringBuffer(IGNORE_REQUEST).append(COMMA).append(PAGE_INDEX).append(COMMA).append(PAGE_CONSOLE).toString();
            if (!LoginFilterConfig.isIgoreSuffix(requestSuffix) && ignoreRequest.indexOf(servletPath) == -1 && !servletPath.startsWith(PAGE_WEBSOCKET)) {
                if (LoginFilterConfig.isAjaxRequest(request)) {
                    response.setHeader(STATUS, SYSTEM_STATUS_MENU_ID);
                    response.setHeader(MESSAGE, URLEncoder.encode(TIPS_ERROR_MENU, UTF8));
                } else {
                    request.setAttribute(MESSAGE, TIPS_ERROR_MENU);
                    request.getRequestDispatcher(PAGE_ERROR).forward(request, response);
                }
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
