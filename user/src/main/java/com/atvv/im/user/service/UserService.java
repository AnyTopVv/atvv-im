package com.atvv.im.user.service;

import com.atvv.im.dto.ResultDto;
import com.atvv.im.entity.User;
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
    ResultDto login(User user);

    /**
     * 退出
     * @return 结果
     */
    ResultDto logout();
}
