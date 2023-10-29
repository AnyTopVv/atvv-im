package com.atvv.atvvim.tcp.handler;


import com.atvv.atvvim.tcp.strategy.command.CommandExecution;
import com.atvv.atvvim.tcp.strategy.command.CommandFactory;
import com.atvv.atvvim.tcp.strategy.command.SystemCommandStrategy;
import com.atvv.im.common.codec.proto.Message;
import com.atvv.atvvim.tcp.utils.UserChannelRepository;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.UUID;

/**
 * netty server处理器
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {

    private final Integer brokerId;

    public NettyServerHandler(Integer brokerId) {
        this.brokerId = brokerId;
    }

    /**
     * CommandExecution对象池，避免重复创建对象
     */
    private final GenericObjectPool<CommandExecution> commandExecutionRequestPool
            = new GenericObjectPool<>(new CommandExecutionFactory());

    /**
     * 有读消息来的时候
     *
     * @param ctx 上下文
     * @param msg 消息
     * @throws Exception 异常
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        //使用策略模式
        Integer command = parseCommand(msg);
        SystemCommandStrategy commandStrategy = CommandFactory.getCommandStrategy(command);
        CommandExecution commandExecution = null;
        try{
            //从对象池获取对象
            commandExecution = commandExecutionRequestPool.borrowObject();
            commandExecution.setCtx(ctx);
            commandExecution.setBrokeId(brokerId);
            commandExecution.setMsg(msg);
            if (commandStrategy != null) {
                // 执行策略
                commandStrategy.systemStrategy(commandExecution);
            }
        } finally {
            // 将对象归还给对象池
            if (commandExecution != null) {
                commandExecutionRequestPool.returnObject(commandExecution);
            }
        }
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

    /**
     * CommandExecution 对象工厂
     */
    private static class CommandExecutionFactory extends BasePooledObjectFactory<CommandExecution> {
        @Override
        public CommandExecution create() throws Exception {
            return new CommandExecution();
        }

        @Override
        public PooledObject<CommandExecution> wrap(CommandExecution obj) {
            return new DefaultPooledObject<>(obj);
        }
    }

}
