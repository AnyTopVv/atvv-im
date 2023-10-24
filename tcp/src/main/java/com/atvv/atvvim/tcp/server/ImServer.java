package com.atvv.atvvim.tcp.server;

import com.atvv.atvvim.tcp.handler.HeartBeatHandler;
import com.atvv.atvvim.tcp.handler.NettyServerHandler;
import com.atvv.atvvim.tcp.handler.WebSocketMessageDecoderHandler;
import com.atvv.atvvim.tcp.handler.WebSocketMessageEncoderHandler;
import com.atvv.atvvim.tcp.config.ImBootstrapConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 *  IM服务启动server
 * @author 扬
 */
@Slf4j
public class ImServer {

    private final ImBootstrapConfig.TcpConfig config;
    private final NioEventLoopGroup mainGroup;
    private final NioEventLoopGroup subGroup;
    private final ServerBootstrap bootstrap;

    public ImServer(ImBootstrapConfig.TcpConfig config) {
        this.config = config;
        // 创建主从线程组
        mainGroup = new NioEventLoopGroup();
        subGroup = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
        bootstrap.group(mainGroup, subGroup)
                .channel(NioServerSocketChannel.class)
                // 服务端可连接的最大队列数量
                .option(ChannelOption.SO_BACKLOG, 10240)
                // 允许重复使用本地地址和端口
                .option(ChannelOption.SO_REUSEADDR, true)
                // 子线程组禁用 Nagle 算法，简单点说是否批量发送数据 true关闭 false开启。 开启的话可以减少一定的网络开销，但影响消息实时性
                .childOption(ChannelOption.TCP_NODELAY, true)
                // 保活机制，2h 没数据会发送心跳包检测
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        // websocket 基于http协议，所以要有http编解码器
                        pipeline.addLast("http-codec", new HttpServerCodec());
                        // 对写大数据流的支持
                        pipeline.addLast("http-chunked", new ChunkedWriteHandler());
                        // 几乎在netty中的编程，都会使用到此hanler
                        pipeline.addLast("aggregator", new HttpObjectAggregator(65535));
                        /*
                         * websocket 服务器处理的协议，用于指定给客户端连接访问的路由 : /ws
                         * 本handler会帮你处理一些繁重的复杂的事
                         * 会帮你处理握手动作： handshaking（close, ping, pong） ping + pong = 心跳
                         * 对于websocket来讲，都是以frames进行传输的，不同的数据类型对应的frames也不同
                         */
                        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                        // 心跳检测 保活 (开发调试暂不启用)
//                        pipeline.addLast(new IdleStateHandler(3, 5, 7));
//                        pipeline.addLast(new HeartBeatHandler(config.getHeartBeatTime()));
                        pipeline.addLast(new WebSocketMessageDecoderHandler());
                        pipeline.addLast(new WebSocketMessageEncoderHandler());
                        pipeline.addLast(new NettyServerHandler());
                    }
                });
    }

    public void start() {
        // 启动服务端
        this.bootstrap.bind(config.getWebSocketPort());
        log.info("web start success");
    }

}
