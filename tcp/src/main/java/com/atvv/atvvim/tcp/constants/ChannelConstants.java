package com.atvv.atvvim.tcp.constants;

/**
 * @ClassName ChannelConstants
 * @Description
 * @date 2023/5/17 10:22
 * @Author yanceysong
 * @Version 1.0
 */
public class ChannelConstants {
    /**
     * channel 绑定的 userId Key
     */
    public static final String USER_ID = "userId";
    /**
     * channel 绑定的 appId Key
     */
    public static final String APP_ID = "appId";
    /**
     * channel 绑定的端类型
     */
    public static final String CLIENT_TYPE = "clientType";
    /**
     * channel 绑定的读写时间
     */
    public static final String READ_TIME = "readTime";
    /**
     * channel 绑定的imei 号，标识用户登录设备号
     */
    public static final String IMEI = "imei";
    /**
     * redisson存的userChannel的key
     */
    public static final String USER_CHANNEL_KEY = APP_ID + ":" + USER_ID + ":" + CLIENT_TYPE;
    /**
     * channel 绑定的 clientType 和 imei Key
     */
    public static final String CLIENT_IMEI = "clientImei";

}
