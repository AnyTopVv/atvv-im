package com.atvv.im.common.common.gateway.exception;



/**
 * @author hjq
 * @date 2023/10/9 15:32
 */

public class ServiceException extends RuntimeException {
    public ServiceException(String errorMessage) {
        super(errorMessage);
    }

}
