package com.atvv.atvvim.tcp.strategy.command;

import com.atvv.im.common.constant.enums.command.MessageCommand;
import com.atvv.im.common.constant.enums.command.SystemCommand;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 命令执行策略工厂类
 */
public class CommandFactory {

    //私有构造方法，防止被实例化
    private CommandFactory() {
    }

    /**
     * 命令维护策略组
     */
    protected static Map<Integer, SystemCommandStrategy> commandStrategyMap = new ConcurrentHashMap<>();

    static {
        commandStrategyMap.put(SystemCommand.COMMAND_LOGIN.getCommand(),new LoginSystemCommand());
        commandStrategyMap.put(SystemCommand.COMMAND_PING.getCommand(), new PingSystemCommand());
        commandStrategyMap.put(MessageCommand.MSG_P2P.getCommand(), new P2PMsgSystemCommand());
    }

    /**
     * 获取策略类
     * @param command 命令
     * @return 策略类
     */
    public static SystemCommandStrategy getCommandStrategy(Integer command) {
        return commandStrategyMap.get(command);
    }
}
