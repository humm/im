package com.hoomoomoo.im.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author hoomoomoo
 * @description 微信消息服务类
 * @package com.hoomoomoo.im.service
 * @date 2020/02/27
 */

public interface SysWeChatService {

    /**
     * 微信消息处理
     *
     * @param request
     * @return
     */
    String message(HttpServletRequest request, HttpServletResponse response);

    /**
     * 更新微信操作信息
     *
     */
    void updateOperateFlow();

}
