package com.atvv.im.user.service;

import com.atvv.im.model.vo.ResponseVO;
import com.atvv.im.model.po.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author hjq
 * @date 2023/9/13 10:44
 */
public interface UserService extends IService<User> {
    /**
     * 登录
     * @param user 用户信息
     * @return 结果
     */
    ResponseVO<?> login(User user);

    /**
     * 退出
     * @return 结果
     */
    ResponseVO<?> logout();


    /**
     * 刷新token
     * @param refreshToken refreshToken
     * @return 结果
     */
    ResponseVO<?> refreshToken(String refreshToken);

    /**
     * 注册
     * @param user 用户信息
     * @return 结果
     */
    ResponseVO<?> register(User user);
}
