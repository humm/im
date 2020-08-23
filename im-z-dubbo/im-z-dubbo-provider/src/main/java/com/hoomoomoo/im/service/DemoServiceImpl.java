package com.hoomoomoo.im.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.hoomoomoo.im.api.DemoService;
import com.hoomoomoo.im.model.DemoModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.service
 * @date 2020/08/22
 */

@Service
public class DemoServiceImpl implements DemoService {

    @Override
    public List<DemoModel> getDemoData() {
        List<DemoModel> demoModelList = new ArrayList<>();
        DemoModel demoModel = new DemoModel();
        demoModel.setUserId("20200822");
        demoModel.setUserCode("demo");
        demoModel.setUserName("演示");
        demoModelList.add(demoModel);
        return demoModelList;
    }
}
