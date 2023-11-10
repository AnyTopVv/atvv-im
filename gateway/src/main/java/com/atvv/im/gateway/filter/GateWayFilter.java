package com.atvv.im.gateway.filter;


import com.atvv.im.gateway.exception.ServiceException;
import com.atvv.im.common.constant.enums.common.ErrorCode;
import com.atvv.im.gateway.constant.StringConstant;
import com.atvv.im.common.utils.JwtUtil;

import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;


/**
 * @author hjq
 * @date 2023/9/18 17:25
 */

@Slf4j
@Component
public class GateWayFilter implements GlobalFilter, Ordered {

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
        String token = tokens.get(0);
        try {
            JwtUtil.verifyJwt(token);
        } catch (TokenExpiredException e) {
            throw new ServiceException(ErrorCode.TOKEN_EXPIRED);
        } catch (Exception e){
            throw new ServiceException(ErrorCode.TOKEN_ERROR);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
