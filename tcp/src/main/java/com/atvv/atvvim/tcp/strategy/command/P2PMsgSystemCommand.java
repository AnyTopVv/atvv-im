package com.atvv.atvvim.tcp.strategy.command;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atvv.atvvim.tcp.service.rabbitmq.MessageProducer;
import com.atvv.atvvim.tcp.utils.RedisManager;
import com.atvv.atvvim.tcp.utils.UserChannelRepository;
import com.atvv.im.common.constant.RedisConstants;
import com.atvv.im.common.constant.enums.device.ClientType;
import com.atvv.im.common.codec.proto.Message;
import com.atvv.im.common.codec.proto.MessagePack;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
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
        Message msg = commandExecution.getMsg();
        ChannelHandlerContext ctx = commandExecution.getCtx();
        JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(msg.getMessagePack()));
        String sendId = jsonObject.getString("sendId");
        String receiverId = jsonObject.getString("receiverId");
        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<String, String> map = redissonClient
                .getMap(RedisConstants.USER_SESSION + ":"
                        + receiverId);
        Set<String> set = map.keySet();
        if (set.isEmpty()){
            //消息持久化
            log.info("用户{}不在线",receiverId);
        }else {
            //发送到mq
            MessageProducer.sendMessage(msg,msg.getMessageHeader().getCommand());
        }
    }
}
