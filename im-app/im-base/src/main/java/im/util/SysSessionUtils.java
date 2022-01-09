package im.util;

import im.model.base.SessionBean;

/**
 * @author hoomoomoo
 * @description Session 工具类
 * @package im.util
 * @date 2019/08/08
 */

public class SysSessionUtils {

    private static ThreadLocal<SessionBean> threadLocal = new ThreadLocal<>();

    /**
     * 设置Session
     *
     * @param sessionBean
     */
    public static void setSession(SessionBean sessionBean){
        threadLocal.set(sessionBean);
    }

    /**
     * 获取Session
     *
     * @return
     */
    public static SessionBean getSession(){
        return threadLocal.get();
    }

    /**
     * 清除Session
     *
     */
    public static void clearSession(){
        threadLocal.remove();
    }
}
