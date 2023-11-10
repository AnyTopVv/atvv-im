package com.atvv.im.user.controller;

import com.atvv.im.common.model.vo.ResponseVO;
import com.atvv.im.common.model.po.User;
import com.atvv.im.user.model.dto.LoginDto;
import com.atvv.im.user.model.dto.RegisterDto;
import com.atvv.im.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * @author hjq
 * @date 2023/9/13 10:43
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/login")
    public ResponseVO<?> login(LoginDto loginDto){
        return ResponseVO.success(userService.login(loginDto));
    }

    @PostMapping("/refresh")
    public ResponseVO<?> refreshToken(String refreshToken){
        return ResponseVO.success(userService.refreshToken(refreshToken));
    }

    @PostMapping("/register")
    public ResponseVO<?> register(RegisterDto registerDto){
        userService.register(registerDto);
        return ResponseVO.success();
    }

}
