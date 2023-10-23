package com.atvv.atvvim.tcp.handler;

import com.atvv.atvvim.tcp.constants.ChannelConstants;
import com.atvv.atvvim.tcp.utils.UserChannelRepository;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName HeartBeatHandler
 * @Description
 * @date 2023/4/25 14:35
 * @Author yanceysong
 * @Version 1.0
 */
@Slf4j
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {
    private final Long heartBeatTime;

    public HeartBeatHandler(Long heartBeatTime) {
        this.heartBeatTime = heartBeatTime;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 判断 evt 是否是 IdleStateEvent (用于触发用户事件，包含 读空闲/写空闲/读写空闲
        if (evt instanceof IdleStateEvent) {
            // 强制类型转换
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.info("进入读空闲...");
            } else if (event.state() == IdleState.WRITER_IDLE) {
                log.info("进入写空闲...");
            } else if (event.state() == IdleState.ALL_IDLE) {
                Long lastReadTime = (Long) ctx
                        .channel()
                        .attr(AttributeKey.valueOf(ChannelConstants.READ_TIME))
                        .get();
                long nowReadTime = System.currentTimeMillis();
                if (lastReadTime != null && nowReadTime - lastReadTime > heartBeatTime) {
                    // 用户退后台
                    log.info("uid:{} 退出",ctx.channel().attr(AttributeKey.valueOf(ChannelConstants.USER_ID)).get());
                    UserChannelRepository.forceOffLine(ctx.channel());
                }
            }
        }
    }

}
