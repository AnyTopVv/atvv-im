package com.atvv.atvvim.tcp.handler;

import com.atvv.im.common.codec.proto.Message;
import com.atvv.atvvim.tcp.utils.ByteBufToMessageUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * WebSocket消息处理器
 */
@Slf4j
public class WebSocketMessageDecoderHandler extends MessageToMessageDecoder<BinaryWebSocketFrame> {

    @Override
    protected void decode(ChannelHandlerContext ctx, BinaryWebSocketFrame msg, List<Object> out) throws Exception {
        ByteBuf content = msg.content();
        if (content.readableBytes() < Message.MESSAGE_MIN_SIZE) {
            return;
        }
        Message message = ByteBufToMessageUtils.transition(content);
        if (message == null) {
            return;
        }
        out.add(message);
    }
}
