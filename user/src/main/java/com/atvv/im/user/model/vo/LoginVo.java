package com.atvv.im.user.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author: zoy0
 * @date: 2023/11/9 20:25
 */
@Data
@AllArgsConstructor
public class LoginVo {

    private String accessToken;

    private String refreshToken;
}
