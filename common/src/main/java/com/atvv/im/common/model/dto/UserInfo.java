package com.atvv.im.common.model.dto;

import lombok.Data;

@Data
public class UserInfo {

    private Long userId;

    private String name;

    private String email;

    private String phone;
}
