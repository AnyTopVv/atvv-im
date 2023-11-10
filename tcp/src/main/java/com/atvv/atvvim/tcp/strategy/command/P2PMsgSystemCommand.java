package com.atvv.atvvim.tcp.strategy.command;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atvv.atvvim.tcp.constants.ChannelConstants;
import com.atvv.atvvim.tcp.service.rabbitmq.MessageProducer;
import com.atvv.atvvim.tcp.utils.RedisManager;
import com.atvv.im.codec.proto.MessagePack;
import com.atvv.im.common.constant.RedisConstants;
import com.atvv.im.codec.proto.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.Set;

/**
 * 用户单聊执行策略
 */
@Slf4j
public class P2PMsgSystemCommand extends BaseSystemCommandStrategy{

    @Override
    public void systemStrategy(CommandExecution commandExecution) {
        //获取消息
        Message msg = commandExecution.getMsg();
        MessagePack<?> messagePack = msg.getMessagePack();
        RedissonClient redissonClient = RedisManager.getRedissonClient();
        //获取接受者消息
        RMap<String, String> map = redissonClient
                .getMap(RedisConstants.USER_SESSION + ":"
                        + messagePack.getReceiverId());
        Set<String> set = map.keySet();

        messagePack.setUserId((Long)commandExecution.getCtx().channel().attr(AttributeKey.valueOf(ChannelConstants.USER_ID)).get());
        if (set.isEmpty()){
            // TODO 消息持久化为离线消息
            log.info("用户{}不在线",messagePack.getReceiverId());
        }else {
            for (String clientType:
                 set) {
                //发送到mq
                msg.getMessagePack().setClientType(Integer.parseInt(clientType));
                MessageProducer.sendMessage(msg,msg.getMessageHeader().getCommand());
            }
        }
    }
}
