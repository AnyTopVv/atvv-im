package com.atvv.atvvim.tcp.strategy.command;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.atvv.atvvim.tcp.constants.ChannelConstants;
import com.atvv.atvvim.tcp.model.dto.UserClientDto;
import com.atvv.atvvim.tcp.utils.UserChannelRepository;
import com.atvv.im.codec.proto.Message;
import com.atvv.im.codec.pack.LoginPack;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录命令执行策略
 */
@Slf4j
public class LoginSystemCommand extends BaseSystemCommandStrategy{
    @Override
    public void systemStrategy(CommandExecution commandExecution) {
        Message msg = commandExecution.getMsg();
        ChannelHandlerContext ctx = commandExecution.getCtx();
        LoginPack loginPack = JSON.parseObject(JSONObject.toJSONString(msg.getMessagePack()),
                new TypeReference<LoginPack>() {}.getType());
        //解析到msg组装UserDTO
        UserClientDto userClientDto = new UserClientDto();
        userClientDto.setUserId(loginPack.getUserId());
        userClientDto.setClientType(msg.getMessageHeader().getClientType());

        // channel 设置属性
        ctx.channel().attr(AttributeKey.valueOf(ChannelConstants.USER_ID)).set(userClientDto.getUserId());
        ctx.channel().attr(AttributeKey.valueOf(ChannelConstants.CLIENT_TYPE)).set(userClientDto.getClientType());
        UserChannelRepository.bind(userClientDto,ctx.channel());
        log.info("登录成功：userId: {}",loginPack.getUserId());
    }
}
