package com.atvv.im.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.atvv.im.common.constant.enums.common.ErrorCode;
import com.atvv.im.common.model.dto.CurrentUserInfo;
import com.atvv.im.common.model.dto.UserInfo;
import com.atvv.im.common.utils.PasswordUtil;
import com.atvv.im.user.constant.RedisConstant;
import com.atvv.im.user.exception.UserServiceException;
import com.atvv.im.user.service.UserService;
import com.atvv.im.user.utils.RedisUtil;
import com.atvv.im.common.model.vo.ResponseVO;
import com.atvv.im.common.model.po.User;
import com.atvv.im.common.dao.UserDao;
import com.atvv.im.common.utils.JwtUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Objects;


/**
 * @author hjq
 * @date 2023/9/13 10:45
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;


    @Resource
    private RedisUtil redisUtil;

    /**
     * token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    @Override
    public ResponseVO<?> login(User user) {
        User userName = userDao.findByUserName(user.getName());
        if (userName==null){
            throw new UserServiceException(ErrorCode.USER_NOT_EXISTS);
        }
        if (!StrUtil.equals(PasswordUtil.md5Encode(user.getPassword()),userName.getPassword())){
            throw new UserServiceException(ErrorCode.WRONG_PASSWORD);
        }
        UserInfo userInfo = userTOUserInfo(userName);
        String accessToken = JwtUtil.createJWT(userInfo);
        String refreshToken = JwtUtil.createJWT(userInfo, 600 * 1000L);
        HashMap<String, String> map = new HashMap<>(2);
        map.put("authorization", accessToken);
        map.put("refresh_token",refreshToken);
//        存入redis
        redisUtil.setCacheObject(RedisConstant.TOKEN_KEY+userName.getId(),accessToken);
        redisUtil.setCacheObject(RedisConstant.REFRESH_TOKEN_KEY+userName.getId(),refreshToken);
        log.info("用户{}登录成功",userName.getId());
        return ResponseVO.success(map);
    }

    private UserInfo userTOUserInfo(User user){
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setName(user.getName());
        userInfo.setPhone(user.getPhone());
        userInfo.setEmail(user.getEmail());
        return userInfo;
    }

    @Override
    public ResponseVO<?> logout() {
        UserInfo userInfo = CurrentUserInfo.get();
        redisUtil.deleteObject(RedisConstant.TOKEN_KEY+ userInfo.getUserId());
        redisUtil.deleteObject(RedisConstant.REFRESH_TOKEN_KEY+ userInfo.getUserId());
        log.info("用户{}退出成功",userInfo.getUserId());
        return new ResponseVO<>(200,"退出成功");
    }

    @Override
    public ResponseVO<?> refreshToken(String refreshToken) {
        UserInfo userInfo = JwtUtil.getCurrentUser(TOKEN_PREFIX+refreshToken);
        String oldRefreshToken = redisUtil.getCacheObject(RedisConstant.REFRESH_TOKEN_KEY+userInfo.getUserId());
        if (oldRefreshToken==null||!oldRefreshToken.equals(refreshToken)){
            log.info("用户{}refreshToken错误",userInfo.getUserId());
            throw new UserServiceException(200,"refresh_token错误");
        }

        String newRefreshToken = JwtUtil.createJWT(userInfo, 600 * 1000L);
        String accessToken = JwtUtil.createJWT(userInfo);
        HashMap<String, String> map = new HashMap<>(2);
        map.put("authorization",accessToken);
        map.put("refresh_token",newRefreshToken);
        redisUtil.setCacheObject(RedisConstant.TOKEN_KEY+userInfo.getUserId(),accessToken);
        redisUtil.setCacheObject(RedisConstant.REFRESH_TOKEN_KEY+userInfo.getUserId(),newRefreshToken);
        log.info("用户{}刷新token成功",userInfo.getUserId());
        return ResponseVO.success(map);
    }

    @Override
    public ResponseVO<?> register(User user) {
        if (Objects.nonNull(userDao.findByUserName(user.getName()))){
            log.info("用户{}已存在",user.getName());
            throw new UserServiceException(ErrorCode.USER_EXISTS);
        }
        user.setPassword(PasswordUtil.md5Encode(user.getPassword()));
        userDao.insert(user);
        log.info("用户{}注册成功",user.getName());
        return ResponseVO.success();
    }
}
