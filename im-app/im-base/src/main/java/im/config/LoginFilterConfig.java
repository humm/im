package im.config;


import im.model.base.SessionBean;
import im.service.SysMenuService;
import im.util.SysLogUtils;
import im.util.SysSessionUtils;
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
import java.io.PrintWriter;
import java.net.URLEncoder;

import static im.config.RunDataConfig.SYSTEM_USED_STATUS;
import static im.consts.BaseConst.*;
import static im.consts.BaseCueConst.*;
import static im.consts.WeChatConst.WECHAT_REQUEST;

/**
 * @author hoomoomoo
 * @description 登录过滤器
 * @package im.config
 * @date 2019/08/08
 */

public class LoginFilterConfig implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LoginFilterConfig.class);

    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public void init(FilterConfig filterConfig) {
        if (sysMenuService == null) {
            ServletContext servletContext = filterConfig.getServletContext();
            ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            sysMenuService = ctx.getBean("sysMenuServiceImpl", SysMenuService.class);
        }
        SysLogUtils.load(logger, LOG_BUSINESS_TYPE_LOGIN_FILTER);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setCharacterEncoding(UTF8);
        String servletPath = request.getServletPath();
        if (WECHAT_REQUEST.contains(servletPath) || servletPath.contains(SWAGGER_REQUEST) || servletPath.contains(SWAGGER_API_DOCS_REQUEST)) {
            isSwagger(request, servletPath);
            filterChain.doFilter(request, response);
            return;
        }
        int index = servletPath.lastIndexOf(SLASH);
        String requestSuffix = servletPath.substring(index + 1);
        SessionBean sessionBean = (SessionBean) request.getSession().getAttribute(SESSION_BEAN);
        if (StringUtils.isNotBlank(SYSTEM_USED_STATUS)) {
            if (isAjaxRequest(request)) {
                response.setHeader(STATUS, SYSTEM_USED_STATUS);
                String message = STR_EMPTY;
                if (SYSTEM_USED_STATUS.equals(SYSTEM_STATUS_INIT)) {
                    message = LOG_BUSINESS_TYPE_INIT_SYSTEM;
                } else if (SYSTEM_USED_STATUS.equals(SYSTEM_STATUS_UPDATE)) {
                    message = LOG_BUSINESS_TYPE_UPDATE_SYSTEM;
                } else if (SYSTEM_USED_STATUS.equals(SYSTEM_STATUS_BACKUP)) {
                    message = LOG_BUSINESS_TYPE_BACKUP;
                }
                message = URLEncoder.encode(new StringBuffer(message).append(COMMA_CHINESE).append(BUSINESS_OPERATE_WAIT).toString(), UTF8);
                response.setHeader(MESSAGE, message);
            } else {
                toLogin(request, response);
            }
            return;
        }
        if (sessionBean != null) {
            // 查询用户数据权限 避免数据权限更新后session刷新不计时
            if (ADMIN_CODE.equals(sessionBean.getUserCode())) {
                sessionBean.setIsAdminData(true);
            } else {
                sessionBean.setIsAdminData(sysMenuService.selectDataAuthorityByUserId(sessionBean.getUserId()));
            }
            SysSessionUtils.setSession(sessionBean);
            if (PAGE_LOGIN.equals(servletPath)) {
                response.sendRedirect(request.getContextPath() + PAGE_INDEX);
            }
        } else {
            if (!isIgoreSuffix(requestSuffix) && IGNORE_REQUEST.indexOf(servletPath) == -1) {
                if (isAjaxRequest(request)) {
                    response.setHeader(STATUS, SYSTEM_STATUS_TIMEOUT);
                    response.setHeader(MESSAGE, URLEncoder.encode(BUSINESS_OPERATE_TIMEOUT, UTF8));
                } else {
                    toLogin(request, response);
                }
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    /**
     * 跳转登入页面
     *
     * @param request
     * @param response
     */
    private void toLogin(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter printWriter = null;
        try {
            printWriter = response.getWriter();
            printWriter.println("<html><script>");
            printWriter.println("window.open ('" + request.getContextPath() + PAGE_LOGIN + "','_top')");
            printWriter.println("</script></html>");
        } catch (IOException e) {
            SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_LOGIN_FILTER, e);
        } finally {
            printWriter.close();
        }
    }

    /**
     * 是否忽略后缀
     *
     * @param requestSuffix
     * @return
     */
    public static boolean isIgoreSuffix(String requestSuffix) {
        if (!requestSuffix.contains(POINT)) {
            return false;
        }
        String[] suffixs = IGNORE_SUFFIX.split(COMMA);
        for (String suffix : suffixs) {
            if (requestSuffix.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    public static void isSwagger(HttpServletRequest request, String servletPath) {
        if (servletPath.contains(SWAGGER_REQUEST) || servletPath.contains(SWAGGER_API_DOCS_REQUEST)) {
            SessionBean sessionBean = (SessionBean) request.getSession().getAttribute(SESSION_BEAN);
            if (sessionBean != null) {
                sessionBean.setIsSwagger(true);
            }
        }
    }

    /**
     * 是否ajax请求
     *
     * @param request
     * @return
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        if (request.getHeader(X_REQUESTED_WITH) != null && request.getHeader(X_REQUESTED_WITH).equalsIgnoreCase(XML_HTTPREQUEST)) {
            return true;
        } else {
            return false;
        }
    }
}
