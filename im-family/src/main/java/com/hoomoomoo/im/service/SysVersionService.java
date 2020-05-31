package com.hoomoomoo.im.service;

import com.hoomoomoo.im.model.ResultData;

/**
 * @author hoomoomoo
 * @description 修订信息服务类
 * @package com.hoomoomoo.im.service
 * @date 2019/11/23
 */

public interface SysVersionService {

    /**
     * 查询修订信息
     *
     * @return
     */
    ResultData selectList();

}
