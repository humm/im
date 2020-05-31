package com.hoomoomoo.im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import static com.hoomoomoo.im.consts.BaseConst.STATUS_SUCCESS;
import static com.hoomoomoo.im.consts.BaseTipsConst.OPERATE_SUCCESS;

/**
 * @author hoomoomoo
 * @description 返回结果集
 * @package com.hoomoomoo.im.model.common
 * @date 2019/08/05
 */

@Data
public class ResultData<T> {

    @ApiModelProperty(value="状态码：请求返回码 0正常返回 -1异常返回", required = false)
    private String code;

    @ApiModelProperty(value="业务处理是否正常", required = false)
    private Boolean bizResult;

    @ApiModelProperty(value="提示消息", required = false)
    private String msg;

    @ApiModelProperty(value="结果集", required = false)
    private T data;

    /**
     * 请求正常 业务处理成功
     */
    public ResultData() {
        this.bizResult = true;
        this.code = STATUS_SUCCESS;
        this.msg = OPERATE_SUCCESS;
    }

    /**
     * 请求正常 业务处理成功
     *
     * @param msg
     */
    public ResultData(String msg) {
        this.bizResult = true;
        this.code = STATUS_SUCCESS;
        this.msg = msg;
    }

    /**
     * 请求 业务处理状态 入参：请求正常则业务处理成功 请求异常则业务处理失败
     *
     * @param code
     * @param msg
     */
    public ResultData(String code, String msg) {
        this.bizResult = STATUS_SUCCESS.equals(code);
        this.code = code;
        this.msg = msg;

    }

    /**
     * 请求正常 业务处理状态 入参
     *
     * @param bizResult
     * @param msg
     */
    public ResultData(Boolean bizResult, String msg) {
        this.code = STATUS_SUCCESS;
        this.bizResult = bizResult;
        this.msg = msg;
    }

    /**
     * 请求正常 业务处理状态 入参
     *
     * @param bizResult
     * @param msg
     * @param data
     */
    public ResultData(Boolean bizResult, String msg, T data) {
        this.code = STATUS_SUCCESS;
        this.bizResult = bizResult;
        this.msg = msg;
        this.data = data;
    }
}
