package com.atvv.im.user.handler;

import com.atvv.im.dto.ResultDto;
import com.atvv.im.user.exception.ServiceException;
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

    @ExceptionHandler(ServiceException.class)
    public ResultDto<?> serviceException(ServiceException serviceException){
        return new ResultDto<>(serviceException.getErrorCode(),serviceException.getErrorMessage());
    }

    @ExceptionHandler(value = {InternalAuthenticationServiceException.class,BadCredentialsException.class})
    public ResultDto<?> internalAuthenticationServiceException(Exception exception){
        return new ResultDto<>(200,exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResultDto<?> exception(Exception exception){
        log.error("发生未知错误",exception);
        return new ResultDto<>(500,"系统错误");
    }


}
