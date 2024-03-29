package com.hoomoomoo.im;

import com.hoomoomoo.im.util.SysLogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import static com.hoomoomoo.im.consts.BaseCueConst.LOG_BUSINESS_TYPE_APP_START;


/**
 * @author hoomoomoo
 * @description 应用启动类
 * @package im
 * @date 2019/08/03
 */
@SpringBootApplication
@ServletComponentScan
public class FimsStarter {

    private static final Logger logger = LoggerFactory.getLogger(FimsStarter.class);

    public static void main(String[] args) {
        SpringApplication.run(FimsStarter.class, args);
        SysLogUtils.configSuccess(logger, LOG_BUSINESS_TYPE_APP_START);
    }

}
