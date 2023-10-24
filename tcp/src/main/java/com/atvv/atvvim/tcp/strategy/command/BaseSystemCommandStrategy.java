package com.atvv.atvvim.tcp.strategy.command;

/**
 * 系统命令执行策略抽象类
 */
public abstract class BaseSystemCommandStrategy implements SystemCommandStrategy{
    /**
     * 兜底策略
     */
    @Override
    public void systemStrategy(CommandExecution commandExecution) {

    }
}
