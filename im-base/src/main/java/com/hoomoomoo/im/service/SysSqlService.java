package com.hoomoomoo.im.service;

import com.hoomoomoo.im.model.SysSqlMode;

import java.util.List;

/**
 * @author hoomoomoo
 * @description 配置sql服务类
 * @package com.hoomoomoo.im.service
 * @date 2019/11/23
 */

public interface SysSqlService<T> {

    /**
     * 配置sql执行服务
     *
     * @param sysSqlMode
     * @param parameter
     * @return
     */
    List<T> execute(SysSqlMode sysSqlMode, T parameter);
}
