package com.hoomoomoo.im.service;

import com.hoomoomoo.im.model.base.ResultData;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hoomoomoo
 * @description 首页信息服务类
 * @package com.hoomoomoo.im.service
 * @date 2019/10/27
 */
public interface SysConsoleService {

    /**
     * 查询首页信息
     *
     * @return
     */
    ResultData selectConsoleData(HttpServletRequest httpServletRequest);

}
