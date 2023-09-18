package com.atvv.im.user.service.impl;

import com.atvv.im.dto.ResultDto;
import com.atvv.im.entity.User;
import com.atvv.im.mapper.UserMapper;
import com.atvv.im.user.constant.RedisConstant;
import com.atvv.im.user.dto.LoginUser;
import com.atvv.im.user.service.UserService;
import com.atvv.im.user.utils.RedisUtil;
import com.atvv.im.utils.JwtUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author hjq
 * @date 2023/9/13 10:45
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public ResultDto login(User user) {
//      AuthenticationManager authenticate  进行用户认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getName(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

//        没通过，给出提示
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("登录失败");
        }
//        通过，生成jwt，存储用户信息
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        if (!passwordEncoder.matches(user.getPassword(),loginUser.getPassword())){
            return new ResultDto<>(200,"密码错误");
        }
        String token = JwtUtil.createJWT(loginUser.getUser().getId().toString());
        HashMap<String, String> map = new HashMap<>(2);
        map.put("token", token);
//        存入redis
        redisUtil.setCacheObject(RedisConstant.TOKEN_KEY+loginUser.getUser().getId(),token,1, TimeUnit.HOURS);
        redisUtil.setCacheObject(RedisConstant.LOGIN_KEY + loginUser.getUser().getId(), loginUser);

        return new ResultDto(200, "登陆成功", map);
    }

    @Override
    public ResultDto logout() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        redisUtil.deleteObject(RedisConstant.LOGIN_KEY+ loginUser.getUser().getId());
        return new ResultDto(200,"退出成功");
    }
}
