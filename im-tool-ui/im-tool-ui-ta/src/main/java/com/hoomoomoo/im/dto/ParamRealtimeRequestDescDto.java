package com.hoomoomoo.im.dto;

public class ParamRealtimeRequestDescDto {

    private String code;

    private String type;

    private String length;

    private String format;

    private String desc;

    private String remark;

    private String require;

    public ParamRealtimeRequestDescDto(String code, String desc, String remark, String require, String type, String length, String format) {
        this.code = code;
        this.desc = desc;
        this.remark = remark;
        this.require = require;
        this.type = type;
        this.length = length;
        this.format = format;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRequire() {
        return require;
    }

    public void setRequire(String require) {
        this.require = require;
    }
}
