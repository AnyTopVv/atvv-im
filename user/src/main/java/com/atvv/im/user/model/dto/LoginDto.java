package com.atvv.im.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author: zoy0
 * @date: 2023/11/9 18:25
 */
@Data
public class LoginDto {

    /**
     * 用户邮箱
     */
    private String userName;

    /**
     * 密码
     */
    private String password;
}
