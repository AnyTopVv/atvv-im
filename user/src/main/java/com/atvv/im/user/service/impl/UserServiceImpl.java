package com.atvv.im.user.service.impl;

import com.atvv.im.dto.ResultDto;
import com.atvv.im.model.User;
import com.atvv.im.mapper.UserMapper;
import com.atvv.im.user.constant.RedisConstant;
import com.atvv.im.user.dto.LoginUser;
import com.atvv.im.user.exception.ServiceException;
import com.atvv.im.user.service.UserService;
import com.atvv.im.user.utils.RedisUtil;
import com.atvv.im.utils.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public ResultDto<?> login(User user) {
//      AuthenticationManager authenticate  进行用户认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getName(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

//        没通过，给出提示
        if (Objects.isNull(authenticate)) {
            throw new ServiceException(200,"登录失败");
        }
//        通过，生成jwt，存储用户信息
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();

        String accessToken = JwtUtil.createJWT(userId);
        String refreshToken = JwtUtil.createJWT(userId, 600 * 1000L);
        HashMap<String, String> map = new HashMap<>(2);
        map.put("access_token", accessToken);
        map.put("refresh_token",refreshToken);
//        存入redis
        redisUtil.setCacheObject(RedisConstant.LOGIN_KEY + userId, loginUser.getUser());
        redisUtil.setCacheObject(RedisConstant.REFRESH_TOKEN_KEY+userId,refreshToken);
        log.info("用户{}登录成功",userId);
        return new ResultDto<>(200, "登陆成功", map);
    }

    @Override
    public ResultDto<?> logout() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        redisUtil.deleteObject(RedisConstant.REFRESH_TOKEN_KEY+user.getId());
        redisUtil.deleteObject(RedisConstant.LOGIN_KEY+ user.getId());
        log.info("用户{}退出成功",user.getId());
        return new ResultDto<>(200,"退出成功");
    }

    @Override
    public ResultDto<?> refreshToken(String refreshToken) {
        String userId = JwtUtil.parseJWT(refreshToken).getSubject();
        String oldRefreshToken = redisUtil.getCacheObject(RedisConstant.REFRESH_TOKEN_KEY+userId);
        if (oldRefreshToken==null||!oldRefreshToken.equals(refreshToken)){
            log.info("用户{}refreshToken错误",userId);
            throw new ServiceException(200,"refresh_token错误");
        }

        String newRefreshToken = JwtUtil.createJWT(userId, 600 * 1000L);
        String accessToken = JwtUtil.createJWT(userId);
        HashMap<String, String> map = new HashMap<>(2);
        map.put("access_token",accessToken);
        map.put("refresh_token",newRefreshToken);
        redisUtil.setCacheObject(RedisConstant.REFRESH_TOKEN_KEY+userId,newRefreshToken);
        log.info("用户{}刷新token成功",userId);
        return new ResultDto<>(200,"刷新成功",map);
    }

    @Override
    public ResultDto<?> register(User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getName,user.getName());
        if (userMapper.selectOne(wrapper)!=null){
            log.info("用户{}已存在",user.getName());
            throw new ServiceException(200,"用户名已存在!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insert(user);
        log.info("用户{}注册成功",user.getName());
        return new ResultDto<>(200,"注册成功");
    }
}
