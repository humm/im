package com.hoomoomoo.im;

import com.hoomoomoo.im.utils.*;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description TODO
 * @package im
 * @date 2021/05/09
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InitConfigTest {

   /**
    * 生成授权证书
    *
    * @param
    * @author: humm23693
    * @date: 2022-09-24
    * @return: void
    */
    @Test
    public void build_01() throws Exception {
        InitConfigUtils.buildLicense(APP_CODE_TA);
    }

    /**
     * 生成配置文件信息
     *
     * @param
     * @author: humm23693
     * @date: 2022-09-24
     * @return: void
     */
    @Test
    public void build_02() throws Exception {
        InitConfigUtils.buildConfig(APP_CODE_TA);
    }

   /**
    * 修改配置文件信息
    *
    * @param
    * @author: humm23693
    * @date: 2022-09-24
    * @return: void
    */
    @Test
    public void build_03() throws Exception {
        InitConfigUtils.updateConfig(APP_CODE_TA);
    }

    /**
     * 更新版本信息
     *
     * @param
     * @author: humm23693
     * @date: 2022-09-24
     * @return: void
     */
    @Test
    public void build_04() {
        InitConfigUtils.updateVersion(APP_CODE_TA);
    }

    /**
     * 复制基类配置信息
     *
     * @param
     * @author: humm23693
     * @date: 2022-09-24
     * @return: void
     */
    @Test
    public void build_05() {
        InitConfigUtils.copyBaseConfig(APP_CODE_TA);
    }

    /**
     * 删除无用配置文件
     *
     * @param
     * @author: humm23693
     * @date: 2022-10-11
     * @return: void
     */
    @Test
    public void build_06() {
        InitConfigUtils.cleanFile(APP_CODE_TA);
    }

    /**
     * 生成文件修改时间清单
     */
    @Test
    public void build_07() {
        InitConfigUtils.buildFileTime(APP_CODE_TA);
    }

}
