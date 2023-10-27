package com.atvv.im.common.common.gateway.filter;


import com.alibaba.fastjson.JSON;
import com.atvv.im.common.common.gateway.exception.ServiceException;
import com.atvv.im.common.common.gateway.utils.RedisUtil;
import com.atvv.im.common.constant.enums.common.ErrorCode;
import com.atvv.im.common.model.dto.UserInfo;
import com.atvv.im.common.model.po.User;
import com.atvv.im.common.common.gateway.constant.StringConstant;
import com.atvv.im.common.utils.JwtUtil;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * @author hjq
 * @date 2023/9/18 17:25
 */

@Slf4j
@Component
public class GateWayFilter implements GlobalFilter, Ordered {
    @Resource
    private RedisUtil redisUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

//        TODO:登录注册路径放行
        String path = request.getPath().toString();
        if (path.endsWith("login") || path.endsWith("refresh") || path.endsWith("register")) {
            log.info("放行路径："+path);
            return chain.filter(exchange);
        }
//        TODO：路径有效性校验
        List<String> tokens = request.getHeaders().get(StringConstant.AUTHORIZATION);

        if (tokens == null || tokens.size() == 0) {
            log.info("请求头缺少token:"+path);
            throw new ServiceException(ErrorCode.NEED_LOGIN);
        }
        UserInfo userInfo = null;
        String token = tokens.get(0);
        Claims claims = JwtUtil.parseJWT(token);
        if (claims.getExpiration().before(new Date())) {
            log.info("token已过期:"+path);
            throw new ServiceException(ErrorCode.TOKEN_EXPIRED);
        }
        userInfo = JSON.parseObject(claims.getSubject(), UserInfo.class);

        token=token.replace(JwtUtil.TOKEN_PREFIX,"");
        String redisToken = redisUtil.getCacheObject(StringConstant.REDIS_TOKEN + userInfo.getUserId());
        if (redisToken==null||!redisToken.equals(token)) {
            log.info("token错误");
            throw new ServiceException(200,"token错误");
        }
        log.info("用户{},访问接口放行{}：",userInfo.getUserId(),path);
        return chain.filter(exchange);

    }

    @Override
    public int getOrder() {
        return -1;
    }
}
