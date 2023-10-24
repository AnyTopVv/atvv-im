package com.atvv.atvvim.tcp;

import com.alibaba.nacos.api.exception.NacosException;
import com.atvv.atvvim.tcp.config.ImBootstrapConfig;
import com.atvv.atvvim.tcp.server.ImServer;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

public class Starter {
    public static void main(String[] args) throws NacosException {
        start();
    }

    private static void start() {
        try (InputStream is = Starter.class.getResourceAsStream("/application.yaml")) {
            Yaml yaml = new Yaml();
            ImBootstrapConfig config = yaml.loadAs(is, ImBootstrapConfig.class);
            new ImServer(config.getIm()).start();
        } catch (Exception e) {
            e.printStackTrace();
            // 程序退出
            System.exit(500);
        }
    }
}
