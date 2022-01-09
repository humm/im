package im.service;

import com.alibaba.dubbo.config.annotation.Service;
import im.api.DemoService;
import im.model.DemoModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hoomoomoo
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
        demoModel.setCode("demo");
        demoModel.setMsg("恭喜你...");
        demoModelList.add(demoModel);
        return demoModelList;
    }
}
