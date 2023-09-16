package com.atvv.im.user.controller;

import com.atvv.im.dto.ResultDto;
import com.atvv.im.entity.User;
import com.atvv.im.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * @author hjq
 * @date 2023/9/13 10:43
 */
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/login")
    public ResultDto login(@RequestBody User user){
        return userService.login(user);
    }

    @GetMapping("/logout")
    public ResultDto logout(){
        return userService.logout();
    }

}
