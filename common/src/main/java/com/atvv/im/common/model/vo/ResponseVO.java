package com.atvv.im.common.model.vo;

import com.atvv.im.common.constant.enums.common.ErrorCode;
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


    /**
     * Success return with nothing.
     * @param <T> data type
     * @return ResponseVO
     */
    public static <T> ResponseVO<T> success() {
        return new ResponseVO<>(ErrorCode.SUCCESS.getCode(),null);
    }

    /**
     * Success return with data.
     * @param <T> data type
     * @return ResponseVO
     */
    public static <T> ResponseVO<T> success(T data) {
        return new ResponseVO<>(ErrorCode.SUCCESS.getCode(),data);
    }

    /**
     * Failed return with message and detail error information.
     * @return ResponseVO
     */
    public static ResponseVO<String> failure(String message) {
        return new ResponseVO<>(ErrorCode.SERVER_ERROR.getCode(), message);
    }

    /**
     * Failed return with errorCode and message.
     * @param <T> data type
     * @return ResponseVO
     */
    public static <T> ResponseVO<T> failure(ErrorCode errorCode) {
        return new ResponseVO<>(errorCode.getCode(), errorCode.getMsg());
    }

    /**
     * Failed return with errorCode, message and data.
     * @param <T> data type
     * @return ResponseVO
     */
    public static <T> ResponseVO<T> failure(ErrorCode errorCode, T data) {
        return new ResponseVO<>(errorCode.getCode(), errorCode.getMsg(), data);
    }
}
