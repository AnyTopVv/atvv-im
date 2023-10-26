package com.atvv.atvvim.tcp.strategy.command;

import com.atvv.im.common.codec.proto.Message;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

/**
 *执行策略参数
 */
@Data
public class CommandExecution {

    private ChannelHandlerContext ctx;

    private Message msg;

    private Integer brokeId;

}
