package im.dao;

import im.model.SysVersionModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hoomoomoo
 * @description 修订信息dao
 * @package im.dao
 * @date 2019/11/23
 */

@Mapper
public interface SysVersionDao {

    List<SysVersionModel> selectList();
}
