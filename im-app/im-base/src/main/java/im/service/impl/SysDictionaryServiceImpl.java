package im.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import im.dao.SysDictionaryDao;
import im.dao.SysUserDao;
import im.model.SysDictionaryModel;
import im.model.SysDictionaryQueryModel;
import im.model.SysUserModel;
import im.model.base.PageModel;
import im.model.base.ResultData;
import im.service.SysDictionaryService;
import im.service.SysSystemService;
import im.util.SysLogUtils;
import im.util.SysSessionUtils;
import im.util.SysCommonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static im.consts.BaseConst.*;
import static im.consts.BaseCueConst.*;

/**
 * @author hoomoomoo
 * @description 字典信息服务实现类
 * @package im.service.impl
 * @date 2019/08/11
 */

@Service
@Transactional
public class SysDictionaryServiceImpl implements SysDictionaryService {

    private static final Logger logger = LoggerFactory.getLogger(SysDictionaryServiceImpl.class);

    @Autowired
    private SysDictionaryDao sysDictionaryDao;

    @Autowired
    private SysSystemService sysSystemService;

    @Autowired
    private SysUserDao sysUserDao;

    /**
     * 查询页面初始化相关数据
     *
     * @return
     */
    @Override
    public ResultData selectInitData() {
        return null;
    }

    /**
     * 分页查询字典信息
     *
     * @param sysDictionaryQueryModel
     * @return
     */
    @Override
    public PageModel<SysDictionaryModel> selectPage(SysDictionaryQueryModel sysDictionaryQueryModel) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_DICTIONARY, LOG_OPERATE_TYPE_SELECT_PAGE);
        SysLogUtils.parameter(logger, sysDictionaryQueryModel);
        PageHelper.startPage(sysDictionaryQueryModel.getPage(), sysDictionaryQueryModel.getLimit());
        // 创建PageInfo对象前 不能处理数据否则getTotal数据不正确
        List<SysDictionaryModel> sysDictionaryModelList = sysDictionaryDao.selectPage(sysDictionaryQueryModel);
        PageInfo<SysDictionaryModel> pageInfo = new PageInfo<>(sysDictionaryModelList);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_DICTIONARY, LOG_OPERATE_TYPE_SELECT_PAGE);
        return new PageModel(pageInfo.getTotal(), sysSystemService.transferData(pageInfo.getList(), SysDictionaryModel.class));
    }

    /**
     * 查询字典信息
     *
     * @param sysDictionaryQueryModel
     * @return
     */
    @Override
    public List<SysDictionaryModel> selectSysDictionary(SysDictionaryQueryModel sysDictionaryQueryModel) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_DICTIONARY, LOG_OPERATE_TYPE_SELECT);
        SysCommonUtils.setSessionInfo(sysDictionaryQueryModel);
        SysLogUtils.parameter(logger, sysDictionaryQueryModel);
        List<SysDictionaryModel> sysDictionaryList = sysDictionaryDao.selectSysDictionary(sysDictionaryQueryModel);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_DICTIONARY, LOG_OPERATE_TYPE_SELECT);
        return sysDictionaryList;
    }

    /**
     * 查询字典信息
     *
     * @param dictionaryCode
     * @param isTranslate
     * @return
     */
    @Override
    public ResultData selectOne(String dictionaryCode, Boolean isTranslate) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_DICTIONARY, LOG_OPERATE_TYPE_SELECT);
        SysDictionaryQueryModel sysDictionaryQueryModel = new SysDictionaryQueryModel();
        sysDictionaryQueryModel.setDictionaryCode(dictionaryCode);
        SysLogUtils.parameter(logger, sysDictionaryQueryModel);
        List<SysDictionaryModel> sysDictionaryModelList = sysDictionaryDao.selectSysDictionary(sysDictionaryQueryModel);
        Iterator<SysDictionaryModel> iterator = sysDictionaryModelList.iterator();
        while (iterator.hasNext()) {
            SysDictionaryModel sysDictionaryModel = iterator.next();
            if (WELL.equals(sysDictionaryModel.getDictionaryItem())) {
                iterator.remove();
                break;
            }
        }
        if (isTranslate) {
            sysDictionaryModelList = sysSystemService.transferData(sysDictionaryModelList, SysDictionaryModel.class);
        }
        List<SysUserModel> sysUserModelList = sysUserDao.selectSysUser(null);
        Map data = new HashMap<>();
        data.put(BUSINESS_TYPE_USER, sysUserModelList);
        data.put(BUSINESS_TYPE_DICTIONARY, sysDictionaryModelList);
        data.put(SESSION_BEAN, SysSessionUtils.getSession());
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_DICTIONARY, LOG_OPERATE_TYPE_SELECT);
        return new ResultData(true, SELECT_SUCCESS, data);
    }

    /**
     * 保存字典信息
     *
     * @param sysDictionaryModelList
     * @return
     */
    @Override
    public ResultData save(List<SysDictionaryModel> sysDictionaryModelList) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_DICTIONARY, LOG_OPERATE_TYPE_UPDATE);
        SysLogUtils.parameter(logger, sysDictionaryModelList);
        // 判断是否开放状态 开放状态先删除后插入 未开放状态更新
        if (CollectionUtils.isNotEmpty(sysDictionaryModelList)) {
            SysDictionaryModel sysDictionaryModel = sysDictionaryModelList.get(0);
            if (StringUtils.isNotBlank(sysDictionaryModel.getDictionaryCode()) && StringUtils.isBlank(sysDictionaryModel.getDictionaryItem())) {
                // 删除操作
                sysDictionaryDao.delete(sysDictionaryModel);
            } else {
                if (STR_1.equals(sysDictionaryModel.getIsOpen())) {
                    // 开放状态先删除后插入
                    SysDictionaryModel dictionary = new SysDictionaryModel();
                    dictionary.setDictionaryCode(sysDictionaryModelList.get(0).getDictionaryCode());
                    sysDictionaryDao.delete(dictionary);
                    for(SysDictionaryModel sysDictionary : sysDictionaryModelList){
                        sysDictionaryDao.save(sysDictionary);
                    }
                } else {
                    // 未开放状态更新
                    for(SysDictionaryModel sysDictionary : sysDictionaryModelList){
                        sysDictionaryDao.update(sysDictionary);
                    }
                }
            }
        }
        // 加载字典项
        sysSystemService.loadSysDictionaryCondition();
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_DICTIONARY, LOG_OPERATE_TYPE_UPDATE);
        return new ResultData(true, UPDATE_SUCCESS, null);
    }

    /**
     * 刷新字典信息
     *
     * @return
     */
    @Override
    public ResultData refresh() {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_DICTIONARY, LOG_OPERATE_TYPE_REFRESH);
        sysSystemService.loadSysDictionaryCondition();
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_DICTIONARY, LOG_OPERATE_TYPE_REFRESH);
        return new ResultData(true, REFRESH_SUCCESS, null);
    }

}
