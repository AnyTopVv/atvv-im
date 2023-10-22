package com.atvv.atvvim.tcp.codec;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName MessagePack
 * @Description 消息服务发送给 tcp 的包体信息，tcp 再更具改包体协议解析成 Message 发送给客户端
 * @date 2023/4/27 10:12
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class MessagePack<T> implements Serializable {
    private String userId;

    private Integer appId;

    /**
     * 接收方
     */
    private String receiverId;

    /**
     * 客户端标识
     */
    private int clientType;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 客户端设备唯一标识
     */
    private String imei;

    private Integer command;

    /**
     * 业务数据对象，如果是聊天消息则不需要解析直接透传
     */
    private T data;

//    /** 用户签名*/
//    private String userSign;

}
