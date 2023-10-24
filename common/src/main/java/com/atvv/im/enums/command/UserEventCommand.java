package com.atvv.im.enums.command;

/**
 * 用户状态事件类型
 */
public enum UserEventCommand implements Command {

    // 用户在线状态通知报文 4004 --> 0xfa4
    USER_ONLINE_STATUS_CHANGE_NOTIFY(0xfa4),

    ;

    private final int command;

    UserEventCommand(int command){
        this.command=command;
    }


    @Override
    public Integer getCommand() {
        return command;
    }

}
