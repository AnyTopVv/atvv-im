package com.atvv.atvvim.tcp.strategy.command;

import com.atvv.atvvim.tcp.constants.ChannelConstants;
import io.netty.util.AttributeKey;

/**
 * ping命令执行策略
 */
public class PingSystemCommand extends BaseSystemCommandStrategy{
    @Override
    public void systemStrategy(CommandExecution commandExecution) {
        commandExecution.getCtx().channel()
                .attr(AttributeKey
                        .valueOf(ChannelConstants.READ_TIME))
                .set(System.currentTimeMillis());
    }
}
