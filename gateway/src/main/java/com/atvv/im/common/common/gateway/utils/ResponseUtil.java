package com.atvv.im.common.common.gateway.utils;


import com.alibaba.fastjson.JSONObject;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.core.io.buffer.DataBuffer;

import java.nio.charset.StandardCharsets;

/**
 * @author hjq
 * @date 2023/9/19 20:29
 */
public class ResponseUtil {

    public static DataBuffer getResponseBuffer(ServerHttpResponse response, String message, Object data){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code",200);
        jsonObject.put("data",data);
        jsonObject.put("msg",message);
        byte[] bytes = jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8);

        return response.bufferFactory().wrap(bytes);
    }
}
