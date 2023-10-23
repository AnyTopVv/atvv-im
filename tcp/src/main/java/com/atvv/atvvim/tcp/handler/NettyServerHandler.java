package com.atvv.atvvim.tcp.handler;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.atvv.atvvim.tcp.codec.Message;
import com.atvv.atvvim.tcp.codec.MessagePack;
import com.atvv.atvvim.tcp.codec.pack.command.LoginPack;
import com.atvv.atvvim.tcp.constants.ChannelConstants;
import com.atvv.atvvim.tcp.model.dto.UserClientDto;
import com.atvv.atvvim.tcp.utils.UserChannelRepository;
import com.atvv.im.enums.command.MessageCommand;
import com.atvv.im.enums.command.SystemCommand;
import com.atvv.im.enums.device.ClientType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * netty server处理器
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {

    public NettyServerHandler() {

    }

    /**
     * 有读消息来的时候
     *
     * @param ctx 上下文
     * @param msg 消息
     * @throws Exception 异常
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        if (msg.getMessageHeader().getCommand().equals(SystemCommand.COMMAND_LOGIN.getCommand())){
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
        }else if (msg.getMessageHeader().getCommand().equals(MessageCommand.MSG_P2P.getCommand())){
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
        }else if(msg.getMessageHeader().getCommand().equals(SystemCommand.COMMAND_PING.getCommand())){
            ctx.channel()
                    .attr(AttributeKey
                            .valueOf(ChannelConstants.READ_TIME))
                    .set(System.currentTimeMillis());
        }
        System.out.println(msg);
    }

    /**
     * 新上线一个channel
     *
     * @param ctx 上下文
     * @throws Exception 异常
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("新上线channel:"+ctx.channel());
        UserChannelRepository.add(ctx.channel());
    }

    /**
     * channel不活跃了
     *
     * @param ctx 上下文
     * @throws Exception 异常
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        UserChannelRepository.remove(ctx.channel());
    }

    /**
     * 异常处理
     *
     * @param ctx   上下文
     * @param cause 什么原因
     * @throws Exception 异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        UserChannelRepository.remove(ctx.channel());
    }

    /**
     * 根据发过来的消息获取到消息的指令
     *
     * @param msg 消息
     * @return 指令
     */
    protected Integer parseCommand(Message msg) {
        return msg.getMessageHeader().getCommand();
    }


}
