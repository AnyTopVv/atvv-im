package com.atvv.im.enums.command;

/**
 * 好友关系类型
 */
public enum FriendshipEventCommand implements Command {

    //好友申请审批
    FRIEND_REQUEST_APPROVER(3005),


    ;

    private final int command;

    FriendshipEventCommand(int command){
        this.command=command;
    }

    @Override
    public Integer getCommand() {
        return command;
    }
}
