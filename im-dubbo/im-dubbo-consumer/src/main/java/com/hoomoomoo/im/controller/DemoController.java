package im.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import im.api.DemoService;
import im.model.DemoModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author humm
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2020/08/22
 */

@Controller
@RequestMapping("/")
public class DemoController {

    @Reference
    private DemoService demoService;

    @RequestMapping(value = "getDemoData", method = RequestMethod.POST)
    @ResponseBody
    public List<DemoModel> getDemoData() {
        return demoService.getDemoData();
    }


}
