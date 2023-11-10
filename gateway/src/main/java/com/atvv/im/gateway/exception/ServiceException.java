package com.atvv.im.gateway.exception;


import com.atvv.im.common.constant.enums.common.ErrorCode;
import lombok.Data;

/**
 * @author hjq
 * @date 2023/10/9 15:32
 */
@Data
public class ServiceException extends RuntimeException {
    private Integer errorCode;

    private String errorMessage;

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorMessage = errorCode.getMsg();
        this.errorCode = errorCode.getCode();
    }

}
