package com.atvv.im.gateway.filter;


import com.atvv.im.entity.User;
import com.atvv.im.gateway.constant.StringConstant;
import com.atvv.im.gateway.utils.RedisUtil;
import com.atvv.im.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
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
//        TODO:登录注册路径放行
//        TODO：路径有效性校验
        List<String> tokens = request.getHeaders().get(StringConstant.TOKEN);

        if (tokens == null || tokens.size() == 0) {
            throw new RuntimeException("请求头缺少token!");
        }
        String userId = null;
        try {
            userId = JwtUtil.parseJWT(tokens.get(0)).getSubject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        User user= redisUtil.getCacheObject(StringConstant.LOGIN +userId);
        if (Objects.isNull(user)){
            throw new RuntimeException("用户未登录");
        }
        return chain.filter(exchange);

    }

    @Override
    public int getOrder() {
        return -1;
    }
}
