package com.hoomoomoo.im.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ParamRealtimeDto {

    private List<ParamRealtimeApiTabDto> paramRealtimeApiTabList;

    private List<ParamRealtimeApiComponentDto> paramRealtimeApiComponentDtoList;

    private List<String> requestContent;

    private List<String> requestPartData;

    private String beginValidDate;

    private Map<String, List<String>> primaryKey = new HashMap<>();

}
