package com.atvv.im.codec.proto;

import lombok.Data;

/**
 *  +-----------------------------------------------------+
 * | 指令 4byte     | 协议版本号 4byte  |  消息解析方式 4byte |
 * +------------------------------------------------------+
 * | 平台ID 4byte   |数据长度 4byte    | 数据内容(请求体)   |
 * +------------------------------------------------------+
 */
@Data
public class MessageHeader {

    /**
     * 消息操作指令(长度为4字节) 十六进制 一个消息的开始通常以0x开头
     */
    private Integer command;

    /**
     * 4字节 版本号
     */
    private Integer version;

    /**
     * 4字节 端类型 (PC,Android,ios,ipad等)
     */
    private Integer clientType;

    /**
     * 4字节 包体长度
     */
    private int length;

}
