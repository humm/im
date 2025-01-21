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

    private String match;

    private String taskNo;

    private String submitNo;

    private String codeVersion;

    private List<String> file;

    @Override
    public int compareTo(LogDto logDto) {
        return logDto.time.compareTo(this.time);
    }
}
