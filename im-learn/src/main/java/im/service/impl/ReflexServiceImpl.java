package im.service.impl;

import im.model.Box;
import im.model.Msg;
import im.service.ReflexService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author
 * @description TODO
 * @package im.service.impl
 * @date 2020/11/20
 */

@Service
public class ReflexServiceImpl implements ReflexService {
    @Override
    public Map function01() {
        System.out.println("function01");
        return null;
    }

    @Override
    public Map function02(String msg) {
        System.out.println(msg);
        return null;
    }

    @Override
    public Map function03(Map msg) {
        System.out.println(msg);
        return null;
    }

    @Override
    public Map function04(Box box) {
        System.out.println(box.getId());
        return null;
    }

    @Override
    public Map function05(Box<Msg> box) {
        System.out.println(box.getData().getCode());
        return null;
    }
}
