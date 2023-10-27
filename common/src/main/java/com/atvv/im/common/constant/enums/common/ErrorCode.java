package com.atvv.im.common.constant.enums.common;

/**
 * http返回码
 */
public enum ErrorCode {

    // 系统模块
    SUCCESS(0, "操作成功"),
    ERROR(1, "操作失败"),
    SERVER_ERROR(500, "服务器异常"),

    // 用户模块
    NEED_LOGIN(20001, "登录失效"),
    TOKEN_EXPIRED(20002,"token已过期"),
    USER_EXISTS(20003,"用户已存在"),
    USER_NOT_EXISTS(20004, "用户不存在"),
    WRONG_PASSWORD(20005, "密码错误"),
    USER_APPLY_REPEAT(20011,"用户申请重复"),
    NOT_FRIEND(20021,"不是好友关系"),
    ALREADY_FRIEND(20022,"已经是好友关系"),
    NOT_GROUP_MEMBER(20031,"不是群成员"),
    ALREADY_GROUP_MEMBER(20032,"已经是群成员");
    private final Integer code;

    private final String msg;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    ErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
