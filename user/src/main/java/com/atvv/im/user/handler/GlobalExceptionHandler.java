package com.atvv.im.user.handler;

import com.atvv.im.common.constant.enums.common.ErrorCode;
import com.atvv.im.common.model.vo.ResponseVO;
import com.atvv.im.user.exception.UserServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author hjq
 * @date 2023/10/9 15:42
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserServiceException.class)
    public ResponseVO<?> serviceException(UserServiceException userServiceException){
        return new ResponseVO<>(userServiceException.getErrorCode(),userServiceException.getErrorMessage());
    }

    @ExceptionHandler(value = {InternalAuthenticationServiceException.class,BadCredentialsException.class})
    public ResponseVO<?> internalAuthenticationServiceException(Exception exception){
        return new ResponseVO<>(200,exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseVO<?> exception(Exception exception){
        log.error("发生未知错误",exception);
        return ResponseVO.failure(ErrorCode.ERROR);
    }


}
