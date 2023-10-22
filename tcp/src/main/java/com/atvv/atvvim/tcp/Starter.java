package com.atvv.atvvim.tcp;

import com.alibaba.nacos.api.exception.NacosException;
import com.atvv.atvvim.tcp.config.ImBootstrapConfig;
import com.atvv.atvvim.tcp.server.ImServer;
import org.yaml.snakeyaml.Yaml;
import sun.misc.Unsafe;

import java.io.InputStream;
import java.lang.reflect.Field;

public class Starter {
    public static void main(String[] args) throws NacosException {
        // 忽略unsafe类的警告
        disableWarning();
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

    /**
     * 忽略unsafe类警告
     */
    private static void disableWarning() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe u = (Unsafe) theUnsafe.get(null);
            Class<?> cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
        } catch (Exception e) {
            // ignore
        }
    }
}
