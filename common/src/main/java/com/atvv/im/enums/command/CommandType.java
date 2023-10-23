package com.atvv.im.enums.command;

/**
 * @ClassName CommandType
 * @Description
 * @date 2023/5/31 13:39
 * @Author yanceysong
 * @Version 1.0
 */
public enum CommandType {
    USER("4"),

    FRIEND("3"),

    GROUP("2"),

    MESSAGE("1"),

    ;

    private final String commandType;

    public String getCommandType() {
        return commandType;
    }

    CommandType(String commandType) {
        this.commandType = commandType;
    }

    public static CommandType getCommandType(String ordinal) {
        for (int i = 0; i < CommandType.values().length; i++) {
            if (CommandType.values()[i].getCommandType().equals(ordinal)) {
                return CommandType.values()[i];
            }
        }
        return null;
    }
}
