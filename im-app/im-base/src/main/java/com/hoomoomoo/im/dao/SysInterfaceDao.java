package com.hoomoomoo.im.dao;

import com.hoomoomoo.im.model.SysInterfaceModel;
import com.hoomoomoo.im.model.SysInterfaceQueryModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author hoomoomoo
 * @description 接口信息Dao
 * @package com.hoomoomoo.im.dao
 * @date 2020/02/09
 */
@Mapper
public interface SysInterfaceDao {

    /**
     * 查询接口信息
     *
     * @param sysInterfaceQueryModel
     * @return
     */
    SysInterfaceModel selectOne(SysInterfaceQueryModel sysInterfaceQueryModel);

    /**
     * 保存接口信息
     *
     * @param sysInterfaceModel
     * @return
     */
    void save(SysInterfaceModel sysInterfaceModel);

   /**
     * 修改接口信息
     *
     * @param sysInterfaceModel
     * @return
     */
    void update(SysInterfaceModel sysInterfaceModel);

}
