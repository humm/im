package com.hoomoomoo.im;

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
public class ServiceStarter {

    public static void main(String[] args) {
        SpringApplication.run(ServiceStarter.class, args);
    }

}
