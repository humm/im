package com.hoomoomoo.im.dao;

import com.hoomoomoo.im.model.SysWeChatFlowModel;
import com.hoomoomoo.im.model.SysWeChatFlowQueryModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hoomoomoo
 * @description 微信流程步骤服dao
 * @package com.hoomoomoo.im.dao
 * @date 2020/02/29
 */

@Mapper
public interface SysWeChatFlowDao {

    /**
     * 查询流程步骤
     *
     * @param sysWeChatFlowQueryModel
     * @return
     */
    List<SysWeChatFlowModel> selectFlowList(SysWeChatFlowQueryModel sysWeChatFlowQueryModel);

}
