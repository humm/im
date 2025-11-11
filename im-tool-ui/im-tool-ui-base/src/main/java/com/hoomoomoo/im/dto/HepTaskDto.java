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
    private String oriTaskName;
    private String productName;
    private String sprintVersion;
    private String sprintVersionFull;
    private String status;
    private String statusName;
    // yyyy-MM-dd HH:mm:ss
    private String estimateFinishTime;

    private String realFinishTime;
    private String realWorkload;
    private String modifiedFile;
    private String editDescription;
    private String suggestion;
    private String selfTestDesc;

    private String operateType;

    // yyyy-MM-dd
    private String createTime;
    // HH:mm:ss
    private String finishTime;
    // yyyy-MM-dd
    private String finishDate;
    // yyyy-MM-dd
    private String oriCloseDate;
    // yyyy-MM-dd
    private String oriPublishDate;
    // yyyyMMdd
    private String minCompleteBySort;
    // yyyyMMdd
    private String minCompleteByMark;
    // yyyyMMdd
    private String sortDate;

    private String sortCode;

    private String customer;

    private String customerFull;

    private String description;

    private String assigneeName;

    private String demandNo;

    private String taskMark;

    private String taskLevel;

    private String creatorId;

    private String creatorName;

    // 开发人
    private String assigneeId;

    // 审核人
    private String reviewerId;

    private String reviewerName;

}
