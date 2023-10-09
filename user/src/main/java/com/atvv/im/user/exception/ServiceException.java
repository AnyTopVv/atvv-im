package com.atvv.im.user.exception;

import lombok.Data;

/**
 * @author hjq
 * @date 2023/10/9 15:32
 */

@Data
public class ServiceException extends RuntimeException{
    private Integer errorCode;

    private String errorMessage;

    public ServiceException(){
        super();
    }

    public ServiceException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public ServiceException(Integer errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

}
