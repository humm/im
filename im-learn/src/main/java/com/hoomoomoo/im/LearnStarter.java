package im;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;


/**
 * @author hoomoomoo
 * @description 应用启动类
 * @package im
 * @date 2020/10/24
 */
@SpringBootApplication
@ServletComponentScan
public class LearnStarter {

    public static void main(String[] args) {
        SpringApplication.run(LearnStarter.class, args);
    }

}
