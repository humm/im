package com.hoomoomoo.im.dto;

import lombok.Data;

import java.util.List;

/**
 * @author humm23693
 * @description svn提交记录
 * @package com.hoomoomoo.im.dto
 * @date 2021/04/19
 */

@Data
public class SvnLogDto extends BaseDto implements Comparable<SvnLogDto> {

    private Long version;

    private String time;

    private Integer num;

    private List<String> file;

    @Override
    public int compareTo(SvnLogDto svnLogDto) {
        return (int) (svnLogDto.version - this.version);
    }
}
