package com.atvv.atvvim.tcp.utils;

import com.alibaba.fastjson.JSONObject;
import com.atvv.atvvim.tcp.constants.ChannelConstants;
import com.atvv.atvvim.tcp.model.dto.UserClientDto;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户的channel仓库 用于处理user与channel的关系记录 用户信息与 Channel 双向存储
 */
@Slf4j
public class UserChannelRepository {
    private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static final Map<UserClientDto, Channel> USER_CHANNEL = new ConcurrentHashMap<>();
    private static final Object bindLocker = new Object();
    private static final Object removeLocker = new Object();

    /**
     * 绑定
     *
     * @param userClientDto user
     * @param channel       这个用户的channel
     */
    public static void bind(UserClientDto userClientDto, Channel channel) {
        synchronized (bindLocker) {
            // 此时channel一定已经在ChannelGroup中了,因为一个channel已上线就已经添加进来了
            // 如果之前已经绑定过了，移除并释放掉之前绑定的channel
            // map  userChannelKey --> channel
            if (USER_CHANNEL.containsKey(userClientDto)) {
                Channel oldChannel = USER_CHANNEL.get(userClientDto);
                CHANNEL_GROUP.remove(oldChannel);
                oldChannel.close();
            }

            // 双向绑定
            // channel -> user property
            channel.attr(AttributeKey.valueOf(ChannelConstants.USER_ID)).set(userClientDto.getUserId());
            channel.attr(AttributeKey.valueOf(ChannelConstants.APP_ID)).set(userClientDto.getAppId());
            channel.attr(AttributeKey.valueOf(ChannelConstants.CLIENT_TYPE)).set(userClientDto.getClientType());
            channel.attr(AttributeKey.valueOf(ChannelConstants.IMEI)).set(userClientDto.getImei());
            channel.attr(AttributeKey.valueOf(ChannelConstants.CLIENT_IMEI)).set(userClientDto.getClientType() + ":" + userClientDto.getImei());

            // userChannelKey -> channel
            USER_CHANNEL.put(userClientDto, channel);
        }
    }

    /**
     * 从通道中获取用户信息。只要 userClientDto 和 channel 绑定中，这个方法就一定能获取的到
     *
     * @param channel channel
     * @return UserClientDto
     */
    public static UserClientDto getUserInfo(Channel channel) {
        String userId = (String) channel.attr(AttributeKey.valueOf(ChannelConstants.USER_ID)).get();
        Integer appId = (Integer) channel.attr(AttributeKey.valueOf(ChannelConstants.APP_ID)).get();
        Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(ChannelConstants.CLIENT_TYPE)).get();
        String imei = (String) channel.attr(AttributeKey.valueOf(ChannelConstants.IMEI)).get();
        return new UserClientDto(appId, userId, clientType, imei);
    }

    /**
     * 根据用户的一些基本信息获取到这个channel
     *
     * @param appId      app的id
     * @param userId     用户的id
     * @param clientType 客户端类型
     * @param imei       设备imei
     * @return 用户的channel
     */
    public static Channel getUserChannel(Integer appId, String userId, Integer clientType, String imei) {
        UserClientDto dto = new UserClientDto();
        dto.setUserId(userId);
        dto.setAppId(appId);
        dto.setClientType(clientType);
        dto.setImei(imei);
        if (!USER_CHANNEL.containsKey(dto)) {
            log.error("channel 通道 没有 [{}] 信息", JSONObject.toJSONString(dto));
            return null;
        }
        return USER_CHANNEL.get(dto);
    }

    /**
     * 从通道中获取userId。只要userId和channel绑定着，这个方法就一定能获取的到
     *
     * @param channel channel
     * @return id
     */
    public static String getUserChannelKey(Channel channel) {
        AttributeKey<String> key = AttributeKey.valueOf(ChannelConstants.USER_CHANNEL_KEY);
        return channel.attr(key).get();
    }

    /**
     * 添加一个channel
     *
     * @param channel channel
     */
    public static void add(Channel channel) {
        CHANNEL_GROUP.add(channel);
    }

    /**
     * 删除一个channel
     * 1、关闭channel
     * 2、删除userKey和channel的绑定关系
     * 3、双删，redisson 和 本地缓存
     * @param channel 要删除的channel
     */
    public static void remove(Channel channel) {
        // 确保原子性
        synchronized (removeLocker) {
            UserClientDto userInfo = getUserInfo(channel);
            // userInfo 有可能为空。可能 chanelActive 之后，由于前端原因（或者网络原因）没有及时绑定 userInfo。
            // 此时 netty 认为 channelInactive 了，就移除通道，这时 userInfo 就是 null
            if (ObjectUtils.isEmpty(userInfo)) {
                log.info("用户信息不存在，请检查");
                return;
            }
            // TODO 延迟双删：等待数据包传输完再删除 channel
            USER_CHANNEL.remove(userInfo);
            CHANNEL_GROUP.remove(channel);

            // 关闭channel
            channel.close();
        }
    }

    /**
     * 根据用户id删除
     *
     * @param userClientDto 用户
     */
    public static void remove(UserClientDto userClientDto) {
        // 确保原子性
        synchronized (removeLocker) {

            Channel channel = USER_CHANNEL.get(userClientDto);
            USER_CHANNEL.remove(userClientDto);
            CHANNEL_GROUP.remove(channel);

            // 关闭channel
            if (!ObjectUtils.isEmpty(channel)) {
                channel.close();
            }
        }
    }

    /**
     * 判断用户是否在线
     * map 和 channelGroup 中均能找得到对应的 channel 说明用户在线
     *
     * @return 在线就返回对应的channel，不在线返回null
     */
    public static Channel isBind(UserClientDto userClientDto) {
        Channel channel = USER_CHANNEL.get(userClientDto);
        if (ObjectUtils.isEmpty(channel)) {
            return null;
        }
        return CHANNEL_GROUP.find(channel.id());
    }

    /**
     * 判断用户是否在线
     * map 和 channelGroup 中均能找得到对应的 channel 说明用户在线
     *
     * @return 结果
     */
    public static boolean isBind(Channel channel) {
        UserClientDto userInfo = getUserInfo(channel);
        return !ObjectUtils.isEmpty(userInfo) &&
                !ObjectUtils.isEmpty(USER_CHANNEL.get(userInfo));
    }

    /**
     * 强制下线
     *
     * @param userClientDto 用户
     */
    public static void forceOffLine(UserClientDto userClientDto) {
        Channel channel = isBind(userClientDto);
        if (!ObjectUtils.isEmpty(channel)) {
            // 移除通道。服务端单方面关闭连接。前端心跳会发送失败
            remove(userClientDto);
        }
    }

    /**
     * 强制下线
     *
     * @param channel channel
     */
    public static void forceOffLine(Channel channel) {
        UserClientDto userInfo = getUserInfo(channel);
        try {
            forceOffLine(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 遍历所有信息获取某用户绑定的所有 Channel
     *
     * @param appId  app的id
     * @param userId 用户的id
     * @return channel
     */
    public static List<Channel> getUserChannels(Integer appId, String userId) {
        Set<UserClientDto> channelInfos = USER_CHANNEL.keySet();
        List<Channel> channels = new ArrayList<>();
        channelInfos.forEach(channel -> {
            if (appId.equals(channel.getAppId()) && userId.equals(channel.getUserId())) {
                channels.add(USER_CHANNEL.get(channel));
            }
        });
        return channels;
    }

    public synchronized static void print() {
        log.info("所有通道的长id：");
        for (Channel channel : CHANNEL_GROUP) {
            log.info(channel.id().asLongText());
        }
        log.info("userId -> channel 的映射：");
        for (Map.Entry<UserClientDto, Channel> entry : USER_CHANNEL.entrySet()) {
            log.info("userId: {} ---> channelId: {}", entry.getKey(), entry.getValue().id().asLongText());
        }
    }

    /**
     * 解析UserDTO返回该实体类的userId，该id作为后续channelId
     * 顺序为:userid:appId:clientType:version:connectState
     *
     * @param obj UserDTO
     * @return channelId or userId
     */
    public static String parseUserClientDto(Object obj) {
        StringBuilder sb = new StringBuilder();
        Class<?> clazz = obj.getClass();//获得实体类名
        Field[] fields = obj.getClass().getDeclaredFields();//获得属性
        //获得Object对象中的所有方法
        for (Field field : fields) {
            try {
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                Method getMethod = pd.getReadMethod();//获得get方法
                Object s = getMethod.invoke(obj);//此处为执行该Object对象的get方法
                sb.append(s).append(":");
            } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return sb.substring(0, sb.length() - 1);
    }
}
