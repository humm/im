package im.config.bean;

import im.consts.BaseConst;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


/**
 * @author hoomoomoo
 * @description 数据库连接配置类
 * @package com.hoomoomoo.im.config.bean
 * @date 2019/11/23
 */

@Component
@PropertySource({BaseConst.APPLICATION_PROPERTIES})
@ConfigurationProperties(prefix = BaseConst.SPRING_DATASOURCE)
@Data
public class DatasourceConfigBean {

    /**
     * 连接驱动
     */
    private String driver;

    /**
     * 连接url
     */
    private String url;

    /**
     * 连接用户名
     */
    private String username;

    /**
     * 连接密码
     */
    private String password;

}
