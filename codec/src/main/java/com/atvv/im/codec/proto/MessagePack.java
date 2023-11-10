package com.atvv.im.codec.proto;

import lombok.Data;

import java.io.Serializable;


@Data
public class MessagePack<T> implements Serializable {

    private Long userId;

    /**
     * 接收方
     */
    private Long receiverId;

    /**
     * 客户端标识
     */
    private int clientType;


    /**
     * uuid 由客户端生成
     */
    private String messageId;

    /**
     * 指令
     */
    private Integer command;


    /**
     * 消息体
     */
    private Object messageBody;

}
