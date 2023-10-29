package com.atvv.atvvim.tcp.utils;

import com.atvv.atvvim.tcp.config.ImBootstrapConfig;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

/**
 * redis 客户端
 * @author: zoy0
 * @date: 2023/10/29 22:38
 */
public class RedisManager {
    private static RedissonClient redissonClient;

    public static void init(ImBootstrapConfig imBootstrapConfig) {
        Config config = new Config();
        ImBootstrapConfig.RedisConfig redisConfig = imBootstrapConfig.getIm().getRedis();
        String node = redisConfig.getSingle().getAddress();
        node = node.startsWith("redis://") ? node : "redis://" + node;
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(node)
                .setDatabase(redisConfig.getDatabase())
                .setTimeout(redisConfig.getTimeout())
                .setConnectionMinimumIdleSize(redisConfig.getPoolMinIdle())
                .setConnectTimeout(redisConfig.getPoolConnTimeout())
                .setConnectionPoolSize(redisConfig.getPoolSize());
        if (StringUtils.isNotBlank(redisConfig.getPassword())) {
            serverConfig.setPassword(redisConfig.getPassword());
        }
        StringCodec stringCodec = new StringCodec();
        config.setCodec(stringCodec);
        redissonClient = Redisson.create(config);
    }

    public static RedissonClient getRedissonClient() {
        return redissonClient;
    }
}
