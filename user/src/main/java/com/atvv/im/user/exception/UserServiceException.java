package com.atvv.im.user.exception;

import com.atvv.im.common.constant.enums.common.ErrorCode;
import lombok.Data;

/**
 * @author hjq
 * @date 2023/10/9 15:32
 */

@Data
public class UserServiceException extends RuntimeException{
    private Integer errorCode;

    private String errorMessage;

    public UserServiceException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public UserServiceException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorMessage = errorCode.getMsg();
        this.errorCode = errorCode.getCode();
    }

    public UserServiceException(Integer errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

}
