package com.atvv.atvvim.tcp;

import com.alibaba.nacos.api.exception.NacosException;
import com.atvv.atvvim.tcp.config.ImBootstrapConfig;
import com.atvv.atvvim.tcp.server.ImServer;
import com.atvv.atvvim.tcp.service.rabbitmq.MessageListener;
import com.atvv.atvvim.tcp.service.rabbitmq.MqFactory;
import com.atvv.atvvim.tcp.utils.RedisManager;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public class Starter {
    public static void main(String[] args) throws NacosException {
        start();
    }

    private static void start() {
        try (InputStream is = Starter.class.getResourceAsStream("/application.yaml")) {
            Yaml yaml = new Yaml();
            ImBootstrapConfig config = yaml.loadAs(is, ImBootstrapConfig.class);
            if (Objects.isNull(config.getIm().getBrokerId())){
                int uuid= UUID.randomUUID().toString().replaceAll("-","").hashCode();
                config.getIm().setBrokerId(uuid < 0 ? -uuid : uuid);
            }
            RedisManager.init(config);
            log.info("redisson初始化成功");
            MqFactory.init(config.getIm().getRabbitmq());
            MessageListener.init(config.getIm().getBrokerId());
            log.info("rabbitmq初始化成功");
            new ImServer(config.getIm()).start();
        } catch (Exception e) {
            e.printStackTrace();
            // 程序退出
            System.exit(500);
        }
    }
}
