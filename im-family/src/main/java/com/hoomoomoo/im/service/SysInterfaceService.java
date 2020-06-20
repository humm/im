package com.hoomoomoo.im.service;

import com.hoomoomoo.im.model.SysInterfaceResponseModel;
import com.hoomoomoo.im.model.SysMailModel;
import com.hoomoomoo.im.model.base.BaseModel;

import java.util.List;

/**
 * @author hoomoomoo
 * @description 接口信息服务类
 * @package com.hoomoomoo.im.service
 * @date 2020/02/09
 */

public interface SysInterfaceService {

    /**
     * 处理邮件请求
     */
    void handleMailRequest();


    /**
     * 处理业务请求数据
     *
     * @param baseModelList
     * @param sysMailModel
     * @return
     */
    SysInterfaceResponseModel handleRequestData(List<BaseModel> baseModelList, SysMailModel sysMailModel);

    /**
     * 系统启动读取邮件
     */
    void startMail();
}
