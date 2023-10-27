package com.atvv.im.common.common.gateway.exception;


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

    public ServiceException(String errorMessage) {
        super(errorMessage);
        this.errorMessage=errorMessage;
    }

    public ServiceException(Integer errorCode,String errorMessage){
        super(errorMessage);
        this.errorCode=errorCode;
        this.errorMessage=errorMessage;
    }

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorMessage = errorCode.getMsg();
        this.errorCode = errorCode.getCode();
    }

}
