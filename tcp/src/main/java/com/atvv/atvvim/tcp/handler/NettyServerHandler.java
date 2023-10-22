package com.atvv.atvvim.tcp.handler;


import com.atvv.atvvim.tcp.codec.Message;
import com.atvv.atvvim.tcp.utils.UserChannelRepository;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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
