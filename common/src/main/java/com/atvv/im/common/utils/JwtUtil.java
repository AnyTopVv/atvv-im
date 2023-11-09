package com.atvv.im.common.utils;


import com.alibaba.fastjson.JSON;
import com.atvv.im.common.model.dto.UserInfo;

import com.atvv.im.common.model.po.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;


import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

/**
 * @author hjq
 * @date 2023/9/11 21:58
 */
@Slf4j
public class JwtUtil {
    /**
     * token过期时间 12小时
     */
    public static final Long EXPIRED_TIME = 60 * 1000L;


    /**
     * 签名
     */
    private static final String SIGN = "atvv";

    public static String createJwt(User user) {
        return createJwt(user, null);
    }

    /**
     * 创建jwt
     *
     * @param user 用户
     * @return jwt
     */
    public static String createJwt(User user, Long ttlMillis) {
        //jwt 头部信息
        Map<String, Object> map = new HashMap<>();

        long nowMillis = System.currentTimeMillis();
        if (ttlMillis == null) {
            ttlMillis = JwtUtil.EXPIRED_TIME;
        }
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setName(user.getName());
        userInfo.setPhone(user.getPhone());
        userInfo.setEmail(user.getEmail());

        String token = JWT.create()
                .withHeader(map)
                .withClaim("userId", 1)
                .withExpiresAt(expDate) //过期时间
                .sign(Algorithm.HMAC256(SIGN));
        return token;
    }

    /**
     * 验证jwt是否有效，有效则返回true
     *
     * @param jwt jwt
     * @return
     */
    public static boolean verifyJwt(String jwt) {
        // 通过签名生成验证对象
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SIGN)).build();
        DecodedJWT verify = jwtVerifier.verify(jwt);
        return verify.getExpiresAt().after(new Date());
    }

    /**
     * 通过jwt获取userId
     *
     * @param jwt jwt
     * @return userId
     */
    public static Long getLoginUserId(String jwt) {
        // 通过签名生成验证对象
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SIGN)).build();
        DecodedJWT verify = jwtVerifier.verify(jwt);
        return verify.getClaim("userId").asLong();
    }
}
