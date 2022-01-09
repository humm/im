package im.service.impl;

import im.dao.SysVersionDao;
import im.model.SysVersionModel;
import im.model.base.ResultData;
import im.service.SysVersionService;
import im.util.SysLogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static im.consts.BaseCueConst.*;

/**
 * @author hoomoomoo
 * @description 修订信息服务实现类
 * @package im.service.impl
 * @date 2019/11/23
 */

@Service
@Transactional
public class SysVersionServiceImpl implements SysVersionService {

    private static final Logger logger = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private SysVersionDao sysVersionDao;

    /**
     * 查询修订信息
     *
     * @return
     */
    @Override
    public ResultData selectList() {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_VERSION, LOG_OPERATE_TYPE_SELECT);
        List<SysVersionModel> sysVersionModelList = sysVersionDao.selectList();
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_VERSION, LOG_OPERATE_TYPE_SELECT);
        return new ResultData(true, SELECT_SUCCESS, sysVersionModelList);
    }
}
