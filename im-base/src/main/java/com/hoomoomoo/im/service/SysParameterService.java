package com.hoomoomoo.im.service;

import com.hoomoomoo.im.model.MailConfigModel;
import com.hoomoomoo.im.model.SysParameterModel;
import com.hoomoomoo.im.model.SysParameterQueryModel;
import com.hoomoomoo.im.model.base.ResultData;

/**
 * @author hoomoomoo
 * @description 参数信息服务类
 * @package com.hoomoomoo.im.service
 * @date 2019/10/20
 */
public interface SysParameterService {

    /**
     * 查询参数信息
     *
     * @param sysParameterQueryModel
     * @return
     */
    ResultData selectList(SysParameterQueryModel sysParameterQueryModel);

    /**
     * 保存参数信息
     *
     * @param sysParameterModel
     * @return
     */
    ResultData save(SysParameterModel sysParameterModel);

    /**
     * 获取系统参数
     *
     * @param parameterCode
     * @return
     */
    Boolean getParameterBoolean(String parameterCode);

    /**
     * 获取系统参数
     *
     * @param parameterCode
     * @return
     */
    String getParameterString(String parameterCode);

    /**
     * 获取系统参数
     *
     * @param parameterCode
     * @return
     */
    Integer getParameterInteger(String parameterCode);

    /**
     * 获取邮件配置参数
     *
     * @return
     */
    MailConfigModel getMailConfig();
}
