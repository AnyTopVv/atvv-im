package com.atvv.im.user.model.dto;

import lombok.Data;

/**
 * @author: zoy0
 * @date: 2023/11/9 20:50
 */
@Data
public class RegisterDto {
    /**
     * 用户邮箱
     */
    private String userName;

    /**
     * 密码
     */
    private String password;
}
