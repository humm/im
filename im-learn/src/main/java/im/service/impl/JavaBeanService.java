package im.service.impl;

import im.service.ExecuteService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author
 * @description TODO
 * @package im.service.impl
 * @date 2020/10/24
 */
@Service("javaBean")
public class JavaBeanService implements ExecuteService {

    @Override
    public String executeService(Map param) {
        return "JavaBeanService";
    }
}
