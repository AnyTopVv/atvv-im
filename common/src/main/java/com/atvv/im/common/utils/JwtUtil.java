package com.atvv.im.common.utils;


import com.alibaba.fastjson.JSON;
import com.atvv.im.common.model.dto.UserInfo;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;


import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * @author hjq
 * @date 2023/9/11 21:58
 */
@Slf4j
public class JwtUtil {
    /**
     * token过期时间 12小时
     */
    public static final Long EXPIRED_TIME =60 * 1000L;
    /**
     * token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 私钥
     */
    public static final String KEY = "ATVVIM";

    /**
     * 生成加密后的秘钥 secretKey
     * @return secretKey
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(JwtUtil.KEY);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }

    private static JwtBuilder getJwtBuilder(UserInfo userInfo, Long ttlMillis, String uuid) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if(ttlMillis==null){
            ttlMillis=JwtUtil.EXPIRED_TIME;
        }
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);
        return Jwts.builder()
                //唯一的ID
                .setId(uuid)
                // 主题  userId
                .setSubject(JSON.toJSONString(userInfo))
                // 签发者
                .setIssuer(KEY)
                // 签发时间
                .setIssuedAt(now)
                //使用HS256对称加密算法签名, 第二个参数为秘钥
                .signWith(signatureAlgorithm, secretKey)
                .setExpiration(expDate);
    }


    /**
     * 生成jtw
     * @param userInfo 用户信息
     * @return token
     */
    public static String createJWT(UserInfo userInfo) {
        JwtBuilder builder = getJwtBuilder(userInfo, null, getUUID());
        return builder.compact();
    }

    /**
     * 生成jtw TODO token中存放 userInfo
     * @param userInfo 用户信息
     * @param ttlMillis token超时时间
     * @return token
     */
    public static String createJWT(UserInfo userInfo, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(userInfo, ttlMillis, getUUID());
        return builder.compact();
    }

    public static String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    /**
     * 解析
     *
     * @param jwt token
     * @return
     * @throws Exception
     */
    public static Claims parseJWT(String jwt)  {
        if (StringUtils.hasText(jwt)){
            String token = jwt.replace(TOKEN_PREFIX, "");
            SecretKey secretKey = generalKey();
            try{
                return Jwts.parser()
                        .setSigningKey(secretKey)
                        .parseClaimsJws(token)
                        .getBody();
            }catch (ExpiredJwtException e){
                return e.getClaims();
            }catch (Exception e){
                log.error("token解析异常",e);
            }
        }

        return null;
    }

    /**
     * TODO 获取jwt当前用户
     * @param token
     * @return
     */
    public static UserInfo getCurrentUser(String token) {
        String subject = Objects.requireNonNull(parseJWT(token)).getSubject();
        return JSON.parseObject(subject, UserInfo.class);
    }
}
