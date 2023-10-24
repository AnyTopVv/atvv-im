package com.atvv.im.gateway.filter;


import com.atvv.im.model.User;
import com.atvv.im.gateway.constant.StringConstant;
import com.atvv.im.gateway.exception.ServiceException;
import com.atvv.im.gateway.utils.RedisUtil;
import com.atvv.im.utils.JwtUtil;

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
        List<String> tokens = request.getHeaders().get(StringConstant.ACCESS_TOKEN);

        if (tokens == null || tokens.size() == 0) {
            log.info("请求头缺少token:"+path);
            throw new ServiceException("请求头缺少token!");
        }
        String userId = null;

        Claims claims = JwtUtil.parseJWT(tokens.get(0));
        if (claims.getExpiration().before(new Date())) {
            log.info("token已过期:"+path);
            throw new ServiceException("token已过期!");
        }
        userId = claims.getSubject();


        User user = redisUtil.getCacheObject(StringConstant.LOGIN + userId);
        if (Objects.isNull(user)) {
            log.info("用户{}未登录,请求接口{}",userId,path);
            throw new ServiceException("用户未登录!");
        }
        log.info("用户{},访问接口放行{}：",userId,path);
        return chain.filter(exchange);

    }

    @Override
    public int getOrder() {
        return -1;
    }
}
