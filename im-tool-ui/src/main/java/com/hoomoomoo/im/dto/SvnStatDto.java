package com.hoomoomoo.im.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.dto
 * @date 2021/05/30
 */

@Data
public class SvnStatDto {

    private String userCode;

    private String userName;

    private String firstTime;

    private String lastTime;

    private Integer submitTimes;

    private Integer fileNum;

    private Integer fileTimes;

    private String notice;

    private Map<String, Integer> file;
}
