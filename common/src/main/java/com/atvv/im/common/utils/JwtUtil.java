package com.atvv.im.common.utils;


import com.atvv.im.common.model.dto.UserInfo;
import io.jsonwebtoken.*;


import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * @author hjq
 * @date 2023/9/11 21:58
 */
public class JwtUtil {
    /**
     * token过期时间 12小时
     */
    public static final Long EXPIRED_TIME =60 * 1000L;

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

    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
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
                .setSubject(subject)
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
     * @param subject token中要存放的数据（userid）
     * @return token
     */
    public static String createJWT(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID());
        return builder.compact();
    }

    /**
     * 生成jtw TODO token中存放 userInfo
     * @param subject token中要存放的数据（userid）
     * @param ttlMillis token超时时间
     * @return token
     */
    public static String createJWT(String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, getUUID());
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
        SecretKey secretKey = generalKey();
        try{
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwt)
                    .getBody();
        }catch (ExpiredJwtException e){
            return e.getClaims();
        }

    }

    /**
     * TODO 获取jwt当前用户
     * @param token
     * @return
     */
    public static UserInfo getCurrentUser(String token) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(Long.valueOf(parseJWT(token).getSubject()));
        return userInfo;
    }
}
