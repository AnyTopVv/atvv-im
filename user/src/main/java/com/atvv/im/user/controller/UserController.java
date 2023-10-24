package com.atvv.im.user.controller;

import com.atvv.im.dto.ResultDto;
import com.atvv.im.model.User;
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
    public ResultDto<?> login(User user){
        return userService.login(user);
    }

    @GetMapping("/test")
    public String test(){
        return "test";
    }

    @GetMapping("/logout")
    public ResultDto<?> logout(){
        return userService.logout();
    }

    @GetMapping("/refresh")
    public ResultDto<?> refreshToken(@RequestHeader String refresh_token){
        return userService.refreshToken(refresh_token);
    }

    @PostMapping("/register")
    public ResultDto<?> register(User user){
        return userService.register(user);
    }

}
