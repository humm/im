package im;

import im.config.bean.ConfigBean;
import im.config.bean.ScheduleConfigBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author hoomoomoo
 * @description 配置类测试
 * @package im.test
 * @date 2019/08/03
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FamilyStarter.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ConfigBeanTest {

    private static final Logger logger = LoggerFactory.getLogger(ConfigBeanTest.class);


    @Autowired
    private ScheduleConfigBean scheduleConfigBean;

    @Autowired
    private ConfigBean applicationBean;

    @Autowired
    private Environment environment;


    @Test
    public void scheduleConfigBean(){
        logger.info(scheduleConfigBean.toString());
    }

    @Test
    public void environment(){
        environment.getProperty("---");
    }

    @Test
    public void applicationBean(){
        logger.info(applicationBean.toString());
    }

}
