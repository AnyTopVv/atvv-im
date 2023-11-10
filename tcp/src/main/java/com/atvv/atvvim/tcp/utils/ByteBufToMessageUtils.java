package com.atvv.atvvim.tcp.utils;

import com.alibaba.fastjson.JSON;
import com.atvv.im.codec.proto.Message;
import com.atvv.im.codec.proto.MessageHeader;
import com.atvv.im.codec.proto.MessagePack;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

/**
 * 将ByteBuf转化为Message实体，根据私有协议转换
 * 私有协议规则，
 * 4位表示Command表示消息的开始，
 * 4位表示version
 * 4位表示clientType
 * 4位表示messageType
 * 4位表示数据长度
 * data
 */
@Slf4j
public class ByteBufToMessageUtils {

    public static Message transition(ByteBuf in){

        // 获取command
        int command = in.readInt();

        // 获取version
        int version = in.readInt();

        // 获取clientType
        int clientType = in.readInt();

        // 获取messageType
        int messageType = in.readInt();

        // 获取bodyLen*
        int bodyLen = in.readInt();

        if(in.readableBytes() < bodyLen){
            in.resetReaderIndex();
            return null;
        }

        byte [] bodyData = new byte[bodyLen];
        in.readBytes(bodyData);

        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setClientType(clientType);
        messageHeader.setCommand(command);
        messageHeader.setLength(bodyLen);
        messageHeader.setVersion(version);

        Message message = new Message();
        message.setMessageHeader(messageHeader);

        if(messageType == 0x0){
            String body = new String(bodyData);
            MessagePack messagePack = JSON.parseObject(body, MessagePack.class);
            message.setMessagePack(messagePack);
        }

        in.markReaderIndex();
        return message;
    }

}

