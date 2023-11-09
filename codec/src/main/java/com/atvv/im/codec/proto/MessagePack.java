package com.atvv.im.codec.proto;

import lombok.Data;

import java.io.Serializable;


@Data
public class MessagePack<T> implements Serializable {

    private String userId;

    /**
     * 接收方
     */
    private String receiverId;

    /**
     * 客户端标识
     */
    private int clientType;


    private String messageId;


    private Integer command;


    private T messageBody;

}
