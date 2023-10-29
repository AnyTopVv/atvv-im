package com.atvv.im.common.codec.proto;

import lombok.Data;

/**
 * 消息
 */
@Data
public class Message {
    /**
     * 一条消息的最短长度
     */
    public static final int MESSAGE_MIN_SIZE = 20;
    private MessageHeader messageHeader;

    private Object messagePack;

    @Override
    public String toString() {
        return "Message{" +
                "messageHeader=" + messageHeader +
                ", messagePack=" + messagePack +
                '}';
    }
}
