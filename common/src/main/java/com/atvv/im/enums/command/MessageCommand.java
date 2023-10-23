package com.atvv.im.enums.command;

/**
 * @ClassName MessageCommand
 * @Description
 * @date 2023/5/16 9:39
 * @Author yanceysong
 * @Version 1.0
 */
public enum MessageCommand implements Command {
    //单聊消息 1103
    MSG_P2P(0x44F),

    // 单聊消息ACK 1046
    MSG_ACK(0x416),

    // 消息收到ACK 1107
    MSG_RECEIVE_ACK(0x453),

    //发送消息已读   1106
    MSG_READ(0x452),

    //消息已读通知给同步端 1053
    MSG_READ_NOTIFY(0x41D),

    //消息已读回执，给原消息发送方 1054
    MSG_READ_RECEIPT(0x41E),

    //消息撤回 1050
    MSG_RECALL(0x41A),

    //消息撤回通知 1052
    MSG_RECALL_NOTIFY(0x41C),

    //消息撤回回报 1051
    MSG_RECALL_ACK(0x41B),

    ;

    private final int command;

    MessageCommand(int command) {
        this.command = command;
    }


    @Override
    public Integer getCommand() {
        return command;
    }
}
