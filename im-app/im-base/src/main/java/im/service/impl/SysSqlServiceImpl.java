package im.service.impl;

import im.model.SysSqlMode;
import im.service.SysSqlService;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static im.consts.BaseConst.*;

/**
 * @author hoomoomoo
 * @description 配置sql服务实现类
 * @package com.hoomoomoo.im.service.impl
 * @date 2019/11/23
 */

@Service
@Transactional
public class SysSqlServiceImpl<T> extends SqlSessionDaoSupport implements SysSqlService<T> {

    /**
     * 配置sql执行服务
     *
     * @param sysSqlMode
     * @param parameter
     * @return
     */
    @Override
    public List<T> execute(SysSqlMode sysSqlMode, Object parameter) {
        String mode = sysSqlMode.getMode();
        String value = sysSqlMode.getValue();
        if (SQL_MODEL_ADD.equals(mode)) {
            getSqlSession().insert(value, parameter);
        } else if (SQL_MODEL_DELETE.equals(mode)) {
            getSqlSession().delete(value, parameter);
        } else if (SQL_MODEL_UPDATE.equals(mode)) {
            getSqlSession().update(value, parameter);
        } else if (SQL_MODEL_SELECT.equals(mode)) {
            return getSqlSession().selectList(value, parameter);
        }
        return null;
    }

    @Autowired(required = false)
    @Qualifier("sqlSessionFactory")
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }
}
