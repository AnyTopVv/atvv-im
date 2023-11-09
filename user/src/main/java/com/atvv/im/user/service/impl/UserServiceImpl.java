package com.atvv.im.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.atvv.im.common.constant.enums.common.ErrorCode;
import com.atvv.im.common.model.dto.CurrentUserInfo;
import com.atvv.im.common.model.dto.UserInfo;
import com.atvv.im.common.utils.SecureUtil;
import com.atvv.im.user.constant.RedisConstant;
import com.atvv.im.user.exception.UserServiceException;
import com.atvv.im.user.model.dto.LoginDto;
import com.atvv.im.user.model.dto.RegisterDto;
import com.atvv.im.user.model.vo.LoginVo;
import com.atvv.im.user.service.UserService;
import com.atvv.im.user.utils.RedisUtil;
import com.atvv.im.common.model.vo.ResponseVO;
import com.atvv.im.common.model.po.User;
import com.atvv.im.common.dao.UserDao;
import com.atvv.im.common.utils.JwtUtil;

import com.auth0.jwt.exceptions.TokenExpiredException;
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
    public LoginVo login(LoginDto loginDto) {
        User user = userDao.findByUserName(loginDto.getUserName());
        if (user == null) {
            throw new UserServiceException(ErrorCode.USER_NOT_EXISTS);
        }
        if (!StrUtil.equals(SecureUtil.md5Encode(loginDto.getPassword()), user.getPassword())) {
            throw new UserServiceException(ErrorCode.WRONG_PASSWORD);
        }
        String accessToken = JwtUtil.createJwt(user);
        String refreshToken = JwtUtil.createJwt(user, 600 * 1000L);
        log.info("用户{}登录成功", user.getId());
        return new LoginVo(accessToken, refreshToken);
    }


    @Override
    public LoginVo refreshToken(String refreshToken) {
        try {
            JwtUtil.verifyJwt(refreshToken);
        } catch (TokenExpiredException e) {
            throw new UserServiceException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        } catch (Exception e) {
            throw new UserServiceException(ErrorCode.REFRESH_TOKEN_ERROR);
        }
        Long refreshTokenUserId = JwtUtil.getLoginUserId(refreshToken);
        Long userId = CurrentUserInfo.get();
        if (!refreshTokenUserId.equals(userId)) {
            throw new UserServiceException(ErrorCode.ERROR_REFRESH_USER_TOKEN);
        }
        User user = new User();
        user.setId(userId);
        String newRefreshToken = JwtUtil.createJwt(user, 600 * 1000L);
        String accessToken = JwtUtil.createJwt(user);
        return new LoginVo(newRefreshToken, accessToken);
    }

    @Override
    public void register(RegisterDto registerDto) {
        if (Objects.nonNull(userDao.findByUserName(registerDto.getUserName()))) {
            log.info("用户{}已存在", registerDto.getUserName());
            throw new UserServiceException(ErrorCode.USER_EXISTS);
        }
        User user = new User();
        user.setPassword(SecureUtil.md5Encode(registerDto.getPassword()));
        userDao.insert(user);
        log.info("用户{}注册成功", user.getName());
    }
}
