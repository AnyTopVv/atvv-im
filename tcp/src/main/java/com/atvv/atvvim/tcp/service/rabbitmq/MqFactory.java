package com.atvv.atvvim.tcp.service.rabbitmq;



import com.atvv.atvvim.tcp.config.ImBootstrapConfig;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

/**
 * 通过mq工厂初始化连接
 */
public class MqFactory {
    private static final ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>();
    private static ConnectionFactory factory = null;

    private static Connection getConnection() throws IOException, TimeoutException {
        return factory.newConnection();
    }

    /**
     * 通过channel名字获取一个channel
     *
     * @param channelName channel名字
     * @return channel mq的channel
     * @throws IOException      异常
     * @throws TimeoutException 异常
     */
    public static Channel getChannel(String channelName) throws IOException, TimeoutException {
        return channelMap.computeIfAbsent(channelName, k -> {
            try {
                return getConnection().createChannel();
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 初始化直接从配置文件读取信息进行初始化
     *
     * @param rabbitmq mq配置文件
     */
    public static void init(ImBootstrapConfig.Rabbitmq rabbitmq) {
        if (factory == null) {
            factory = new ConnectionFactory();
            factory.setHost(rabbitmq.getHost());
            factory.setPort(rabbitmq.getPort());
            factory.setUsername(rabbitmq.getUserName());
            factory.setPassword(rabbitmq.getPassword());
            factory.setVirtualHost(rabbitmq.getVirtualHost());
        }
    }
}
