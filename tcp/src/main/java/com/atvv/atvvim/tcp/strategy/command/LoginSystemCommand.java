package com.atvv.atvvim.tcp.strategy.command;

import com.alibaba.fastjson.JSONObject;
import com.atvv.atvvim.tcp.constants.ChannelConstants;
import com.atvv.atvvim.tcp.model.dto.UserClientDto;
import com.atvv.atvvim.tcp.utils.RedisManager;
import com.atvv.atvvim.tcp.utils.UserChannelRepository;
import com.atvv.im.codec.proto.Message;
import com.atvv.im.codec.proto.MessagePack;
import com.atvv.im.common.constant.RedisConstants;
import com.atvv.im.common.model.dto.UserRedisSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

/**
 * 登录命令执行策略
 */
@Slf4j
public class LoginSystemCommand extends BaseSystemCommandStrategy{
    @Override
    public void systemStrategy(CommandExecution commandExecution) {
        Message msg = commandExecution.getMsg();
        ChannelHandlerContext ctx = commandExecution.getCtx();
        MessagePack<?> messagePack = msg.getMessagePack();
        //解析到msg组装UserDTO
        UserClientDto userClientDto = new UserClientDto();
        userClientDto.setUserId(messagePack.getUserId());
        userClientDto.setClientType(msg.getMessageHeader().getClientType());

        // channel 设置属性
        ctx.channel().attr(AttributeKey.valueOf(ChannelConstants.USER_ID)).set(userClientDto.getUserId());
        ctx.channel().attr(AttributeKey.valueOf(ChannelConstants.CLIENT_TYPE)).set(userClientDto.getClientType());
        UserChannelRepository.bind(userClientDto,ctx.channel());

        UserRedisSession userRedisSession = new UserRedisSession();
        userRedisSession.setUserId(messagePack.getUserId());
        userRedisSession.setClientType(msg.getMessageHeader().getClientType());
        userRedisSession.setVersion(msg.getMessageHeader().getVersion());
        userRedisSession.setToken("temp:none");
        userRedisSession.setBrokerId(commandExecution.getBrokeId());
        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<String, String> map = redissonClient
                .getMap(RedisConstants.USER_SESSION + ":"
                        + messagePack.getMessageId());
        map.put(String.valueOf(msg.getMessageHeader().getClientType()), JSONObject.toJSONString(userRedisSession));
        log.info("登录成功：userId: {}",messagePack.getMessageId());
    }
}
