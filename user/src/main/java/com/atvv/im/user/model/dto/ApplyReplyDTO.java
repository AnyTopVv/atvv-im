package com.atvv.im.user.model.dto;

import lombok.Data;

/**
 * 回应申请DTO
 */
@Data
public class ApplyReplyDTO {

    /**
     * 申请目标id
     */
    private Long userApplyId;

    /**
     * 选项
     */
    private Integer option;


}
