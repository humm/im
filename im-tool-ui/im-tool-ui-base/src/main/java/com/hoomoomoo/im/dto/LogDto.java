package com.hoomoomoo.im.dto;

import com.hoomoomoo.im.utils.CommonUtils;
import lombok.Data;

import java.util.List;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.dto
 * @date 2021/05/07
 */
@Data
public class LogDto extends BaseDto implements Comparable<LogDto> {

    private String time;

    private String msg;

    private String name;

    private String num;

    private String getNum;

    private String match;

    private String version;

    private String serialNo;

    private String codeVersion;

    private List<String> file;

    @Override
    public int compareTo(LogDto logDto) {
        if (CommonUtils.isNumber(logDto.version) && CommonUtils.isNumber(this.version)) {
            return (int) (Long.parseLong(logDto.version) - Long.parseLong(this.version));
        }
        return logDto.version.compareTo(this.version);
    }
}
