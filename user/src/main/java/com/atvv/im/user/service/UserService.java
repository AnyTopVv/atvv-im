package com.atvv.im.user.service;

import com.atvv.im.common.model.vo.ResponseVO;
import com.atvv.im.common.model.po.User;
import com.atvv.im.user.model.dto.LoginDto;
import com.atvv.im.user.model.dto.RegisterDto;
import com.atvv.im.user.model.vo.LoginVo;

/**
 * @author hjq
 * @date 2023/9/13 10:44
 */
public interface UserService {
    /**
     * 登录
     * @param loginDto 用户信息
     * @return 结果
     */
    LoginVo login(LoginDto loginDto);

    /**
     * 刷新token
     * @param refreshToken refreshToken
     * @return 结果
     */
    LoginVo refreshToken(String refreshToken);

    /**
     * 注册
     * @param user 用户信息
     * @return 结果
     */
    void register(RegisterDto registerDto);
}
