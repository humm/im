package im.dao;

import im.model.SysConfigModel;
import im.model.SysConfigQueryModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hoomoomoo
 * @description 配置信息dao
 * @package im.dao
 * @date 2019/11/26
 */

@Mapper
public interface SysConfigDao {

    /**
     * 查询配置模块信息
     *
     * @return
     */
    List<SysConfigModel> selectModule(SysConfigQueryModel sysConfigQueryModel);

    /**
     * 保存模块信息
     *
     * @return
     */
    void save(SysConfigModel sysConfigModel);

    /**
     * 删除模块配置信息
     *
     * @param sysConfigModelList
     */
    void delete(List<SysConfigModel> sysConfigModelList);
}
