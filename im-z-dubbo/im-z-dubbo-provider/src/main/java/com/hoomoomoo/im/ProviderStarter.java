package com.hoomoomoo.im;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;


/**
 * @author hoomoomoo
 * @description TODO
 * @package com.hoomoomoo.im
 * @date 2019/08/03
 */
@SpringBootApplication
@ServletComponentScan
public class ProviderStarter {

    private static final Logger logger = LoggerFactory.getLogger(ProviderStarter.class);

    public static void main(String[] args) {
        SpringApplication.run(ProviderStarter.class, args);
        logger.info("提供者...启动成功");
    }

}
