package com.atvv.im.enums.device;


import com.atvv.im.constant.CodeAdapter;

/**
 * 端类型
 * 目前仅支持WEB端
 */
public enum ClientType implements CodeAdapter {
    WEBAPI(0, "webapi"),
    WEB(1, "web"),
    IOS(2, "ios"),
    ANDROID(3, "android"),
    WINDOWS(4, "windows"),
    MAC(5, "mac"),
    ;

    private final Integer code;
    private final String info;

    ClientType(int code, String info) {
        this.code = code;
        this.info = info;
    }

    /**
     * 判断是否为同一类型客户端
     *
     * @param dtoClientType     用户当前登录端信息
     * @param channelClientType 用户之前登录端信息
     * @return 是否为同一类型客户端
     */
    public static boolean isSameClient(Integer dtoClientType, Integer channelClientType) {
        if ((IOS.getCode().equals(dtoClientType) || ANDROID.getCode().equals(dtoClientType)) &&
                (IOS.getCode().equals(channelClientType) || ANDROID.getCode().equals(channelClientType))) {
            return true;
        }
        return (MAC.getCode().equals(dtoClientType) || WINDOWS.getCode().equals(dtoClientType)) &&
                (MAC.getCode().equals(channelClientType) || WINDOWS.getCode().equals(channelClientType));
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    public String getInfo() {
        return info;
    }

}
