package com.atvv.atvvim.tcp.service.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atvv.im.common.codec.proto.Message;
import com.atvv.im.common.constant.RabbitmqConstants;
import com.atvv.im.common.constant.enums.common.CommonType;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息生产者
 * @author: zoy0
 * @date: 2023/10/29 23:07
 */
@Slf4j
public class MessageProducer {

    public static void sendMessage(Message message, Integer command) {
        String num = command.toString();
        String channelName;
        Integer commandTypeCode = Integer.valueOf(num.substring(0, 1));
        if (commandTypeCode.equals(CommonType.MESSAGE.getCode())) {
            channelName = RabbitmqConstants.MESSAGE_SERVICE2_IM;
        }else {
            log.info("占不支持其他类型消息");
            return;
        }
        Channel channel;
        try {
            channel = MqFactory.getChannel(channelName);
            // 解析私有协议的内容
            JSONObject o = (JSONObject) JSON.toJSON(message.getMessagePack());
            o.put("userId",o.get("sendId"));
            o.put("command",command);
            o.put("data",o.get("messageBody"));
            channel.basicPublish(channelName, "",
                    null, o.toJSONString().getBytes());
        } catch (Exception e) {
            log.error("发送消息出现异常：{}", e.getMessage());
        }
    }
}
