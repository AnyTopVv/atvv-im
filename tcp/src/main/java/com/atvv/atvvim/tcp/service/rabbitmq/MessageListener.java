package com.atvv.atvvim.tcp.service.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.atvv.atvvim.tcp.utils.UserChannelRepository;
import com.atvv.im.codec.proto.MessagePack;
import com.atvv.im.common.constant.RabbitmqConstants;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 消息监听器
 */
@Slf4j
public class MessageListener {

    public static Integer brokerId;

    /**
     * 每一个服务器节点，都绑定一个对应的queue，格式为 MessageService2Im + brokerId（每一个服务端的唯一编号）
     */
    private static void startListenerMessage() {
        try {
            //mq的channel broker Id 不同会有不同的队列
            Channel channel = MqFactory.getChannel(RabbitmqConstants.MESSAGE_SERVICE2_IM + brokerId);

            channel.exchangeDeclare(RabbitmqConstants.MESSAGE_SERVICE2_IM, BuiltinExchangeType.DIRECT,true);
            // 声明队列： 队列名，持久化，非私有的，不自动删除
            channel.queueDeclare(RabbitmqConstants.MESSAGE_SERVICE2_IM + brokerId, true, false, true, null);
            //队列绑定交换机，routingKey为空即可
            channel.queueBind(RabbitmqConstants.MESSAGE_SERVICE2_IM + brokerId, RabbitmqConstants.MESSAGE_SERVICE2_IM, "");
            //启动消费者并设置不自动ack
            channel.basicConsume(RabbitmqConstants.MESSAGE_SERVICE2_IM + brokerId
                    , false,
                    new DefaultConsumer(channel) {

                        @Override
                        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                            String msgStr = new String(body, StandardCharsets.UTF_8);
                            try {
                                log.info("服务端监听消息信息为 {} ", msgStr);
                                // 消息写入数据通道
                                MessagePack<?> messagePack = JSONObject.parseObject(msgStr, MessagePack.class);
                                io.netty.channel.Channel userChannel = UserChannelRepository.getUserChannel(messagePack.getReceiverId(), messagePack.getClientType());
                                // 消息成功写入通道后发送应答 Ack
                                if (Objects.nonNull(userChannel)) {
                                    userChannel.writeAndFlush(messagePack);
                                }
                                channel.basicAck(envelope.getDeliveryTag(), false);
                            } catch (Exception e) {
                                e.printStackTrace();
                                // 消息不能正常写入通道，发送失败应答 NAck
                                channel.basicNack(envelope.getDeliveryTag(), false, false);
                            }
                            log.info(msgStr);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 开始监听
     */
    public static void init(Integer brokerId) {
        if (Objects.isNull(MessageListener.brokerId)) {
            MessageListener.brokerId = brokerId;
        }
        startListenerMessage();
    }
}
