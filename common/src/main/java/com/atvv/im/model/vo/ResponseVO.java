package com.atvv.im.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hjq
 * @date 2023/9/14 20:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseVO<T> {
    private Integer code;

    private String msg;

    private T data;

    public ResponseVO(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseVO(Integer code, T data) {
        this.code = code;
        this.data = data;
    }
}
