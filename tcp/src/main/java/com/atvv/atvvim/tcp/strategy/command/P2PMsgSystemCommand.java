package com.atvv.atvvim.tcp.strategy.command;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atvv.atvvim.tcp.utils.UserChannelRepository;
import com.atvv.im.enums.device.ClientType;
import com.atvv.im.codec.proto.Message;
import com.atvv.im.codec.proto.MessagePack;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * 用户单聊执行策略
 */
public class P2PMsgSystemCommand extends BaseSystemCommandStrategy{
    @Override
    public void systemStrategy(CommandExecution commandExecution) {
        Message msg = commandExecution.getMsg();
        ChannelHandlerContext ctx = commandExecution.getCtx();
        JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(msg.getMessagePack()));
        String sendId = jsonObject.getString("sendId");
        String receiverId = jsonObject.getString("receiverId");
        Channel userChannel = UserChannelRepository.getUserChannel(receiverId, ClientType.WEB.getCode());
        if (userChannel != null) {
            MessagePack<Object> messagePack = new MessagePack<>();
            messagePack.setUserId(sendId);
            messagePack.setReceiverId(sendId);
            messagePack.setCommand(msg.getMessageHeader().getCommand());
            messagePack.setData(msg.getMessagePack());
            userChannel.writeAndFlush(messagePack);
        }else {
            ctx.channel().writeAndFlush("用户未在线");
        }
    }
}
