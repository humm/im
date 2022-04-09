package com.hoomoomoo.im.dto;

import lombok.Data;

import java.util.Map;

import static com.hoomoomoo.im.consts.BaseConst.STR_0;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.dto
 * @date 2021/05/30
 */

@Data
public class SvnStatDto extends BaseDto{

    private String userCode;

    private String userName;

    private String firstTime;

    private String lastTime;

    private String submitTimes;

    private String fileNum;

    private String fileTimes;

    private String notice;

    private Map<String, Integer> file;

    private Map<Long, Long> svnNum;

    public SvnStatDto() {
        this.submitTimes = STR_0;
        this.fileNum = STR_0;
        this.fileTimes = STR_0;
    }
}
