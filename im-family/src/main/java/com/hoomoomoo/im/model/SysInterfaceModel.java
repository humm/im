package com.hoomoomoo.im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hoomoomoo
 * @description 接口信息实体类
 * @package com.hoomoomoo.im.model
 * @date 2020/02/09
 */

@Data
public class SysInterfaceModel extends BaseModel {

    @ApiModelProperty(value="接口ID", required = false)
    private String interfaceId;

    @ApiModelProperty(value="请求ID", required = false)
    private String requestId;

    @ApiModelProperty(value="请求数据", required = false)
    private String requestData;

    @ApiModelProperty(value="请求处理结果", required = false)
    private String requestResult;

    @ApiModelProperty(value="请求处理消息", required = false)
    private String requestMessage;

    @ApiModelProperty(value="请求反馈状态", required = false)
    private String feedbackStatus;

    public SysInterfaceModel() {
    }

    public SysInterfaceModel(String param) {
        this.requestData = param;
        this.requestResult = param;
        this.requestMessage = param;
        this.feedbackStatus = param;
    }
}
