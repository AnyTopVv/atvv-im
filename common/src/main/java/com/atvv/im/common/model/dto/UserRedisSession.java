package com.atvv.im.common.model.dto;

import lombok.Data;

/**
 * 登录后存在redis中的用户消息
 * @author: zoy0
 * @date: 2023/10/29 22:22
 */
@Data
public class UserRedisSession {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 端信息，后续如果有多平台登录限制需要使用到
     */
    private Integer clientType;

    /**
     * 客户端版本好
     */
    private Integer version;

    /**
     * 服务端标识
     */
    private Integer brokerId;

    /**
     * 令牌
     */
    private String token;
}
