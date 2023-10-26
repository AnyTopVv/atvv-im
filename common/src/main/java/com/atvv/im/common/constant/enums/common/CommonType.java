package com.atvv.im.common.constant.enums.common;

import com.atvv.im.common.constant.enums.CodeAdapter;

/**
 * 通用枚举
 */
public enum CommonType implements CodeAdapter {

    USER(4),

    FRIEND(3),

    GROUP(2),

    MESSAGE(1),
    ;

    private final Integer code;


    CommonType(Integer code) {
        this.code = code;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
