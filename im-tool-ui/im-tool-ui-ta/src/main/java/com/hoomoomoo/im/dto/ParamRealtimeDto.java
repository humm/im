package com.hoomoomoo.im.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParamRealtimeDto {

    private List<ParamRealtimeApiTabDto> paramRealtimeApiTabList;

    private List<ParamRealtimeApiComponentDto> paramRealtimeApiComponentDtoList;

    private List<String> requestContent;

    private Map<String, List<String>> primaryKey = new HashMap<>();

    public Map<String, List<String>> getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Map<String, List<String>> primaryKey) {
        this.primaryKey = primaryKey;
    }

    public List<ParamRealtimeApiTabDto> getParamRealtimeApiTabList() {
        return paramRealtimeApiTabList;
    }

    public void setParamRealtimeApiTabList(List<ParamRealtimeApiTabDto> paramRealtimeApiTabList) {
        this.paramRealtimeApiTabList = paramRealtimeApiTabList;
    }

    public List<ParamRealtimeApiComponentDto> getParamRealtimeApiComponentDtoList() {
        return paramRealtimeApiComponentDtoList;
    }

    public void setParamRealtimeApiComponentDtoList(List<ParamRealtimeApiComponentDto> paramRealtimeApiComponentDtoList) {
        this.paramRealtimeApiComponentDtoList = paramRealtimeApiComponentDtoList;
    }

    public List<String> getRequestContent() {
        return requestContent;
    }

    public void setRequestContent(List<String> requestContent) {
        this.requestContent = requestContent;
    }
}
