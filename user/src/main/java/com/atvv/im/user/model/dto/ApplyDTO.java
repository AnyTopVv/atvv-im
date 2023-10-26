package com.atvv.im.user.model.dto;

import lombok.Data;

/**
 * 好友申请DTO
 */
@Data
public class ApplyDTO {

    /**
     * 申请消息
     */
    private String attach;

    /**
     * 申请目标id
     */
    private Long targetId;

    /**
     * 申请类型 1为好友申请，2为群聊申请
     */
    private Integer type;

}
