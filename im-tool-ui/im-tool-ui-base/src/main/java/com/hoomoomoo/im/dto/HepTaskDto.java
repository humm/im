package com.hoomoomoo.im.dto;

import lombok.Data;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.dto
 * @date 2023-07-30
 */

@Data
public class HepTaskDto extends BaseDto{

    private String id;
    private String taskNumber;
    private String name;
    private String productName;
    private String sprintVersion;
    private String status;
    private String statusName;
    private String estimateFinishDate;
    private String estimateFinishTime;

    private String realFinishTime;
    private String realWorkload;
    private String modifiedFile;
    private String editDescription;
    private String suggestion;

    private String operateType;

    private String closeDate;
    private String publishDate;
    private String customer;

    private String description;
}
