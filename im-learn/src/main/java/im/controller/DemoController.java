package im.controller;

import im.service.ExecuteService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author
 * @description TODO
 * @package im.controller
 * @date 2020/10/24
 */

@RequestMapping("/demo")
@RestController
public class DemoController {

    @Autowired
    private Map<String, ExecuteService> serviceMap = new HashMap();

    @Autowired
    private List<ExecuteService> serviceList = new ArrayList<>();

    @ApiOperation("获取服务名称")
    @RequestMapping(value = "/service", method = RequestMethod.POST)
    public String getService(@ApiParam(value = "服务类型", required = true)
                             @RequestParam String serviceType) {
        if (serviceMap.get(serviceType) == null) {
            return "服务类型未实现";
        }
        return serviceMap.get(serviceType).executeService(null);
    }
}
