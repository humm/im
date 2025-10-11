package com.hoomoomoo.im.dto;

import java.util.List;

public class ParamRealtimeDto {

    private List<ParamRealtimeApiTabDto> paramRealtimeApiTabList;

    private List<ParamRealtimeApiComponentDto> paramRealtimeApiComponentDtoList;

    private String requestContent;

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

    public String getRequestContent() {
        return requestContent;
    }

    public void setRequestContent(String requestContent) {
        this.requestContent = requestContent;
    }
}
