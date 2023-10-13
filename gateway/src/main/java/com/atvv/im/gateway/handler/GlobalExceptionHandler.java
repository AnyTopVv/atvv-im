package com.atvv.im.gateway.handler;


import com.atvv.im.dto.ResultDto;
import com.atvv.im.gateway.exception.ServiceException;
import com.atvv.im.gateway.utils.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.function.server.*;

import java.util.Map;


/**
 * @author hjq
 * @date 2023/10/9 15:42
 */
@Slf4j
public class GlobalExceptionHandler extends DefaultErrorWebExceptionHandler {


    public GlobalExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources,
                                  ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resources, errorProperties, applicationContext);
    }

    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        int code = 500;
        String errorMessage;
        Throwable error = super.getError(request);
        if (error instanceof ServiceException) {
            code = 200;
        }
        errorMessage = error.getMessage();
        log.error("异常信息:{}", errorMessage);
        return response(code, this.buildMessage(request, errorMessage));
    }

    /**
     * 指定响应处理方法为JSON处理的方法
     *
     * @param errorAttributes errorAttributes
     */
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    /**
     * 根据code获取对应的HttpStatus
     *
     * @param errorAttributes errorAttributes
     */
    @Override
    protected int getHttpStatus(Map<String, Object> errorAttributes) {
        return (int) errorAttributes.get("code");
    }

    /**
     * 构建异常信息
     *
     * @param request          请求
     * @param exceptionMessage 异常信息
     * @return 返回构建的异常信息
     */
    private String buildMessage(ServerRequest request, String exceptionMessage) {
        return "Failed to handle request [" + request.methodName() +
                " " +
                request.uri() +
                "]" +
                ": " +
                exceptionMessage;
    }

    private Map<String, Object> response(Integer code, String errorMessage) {
        ResultDto<Object> resultDto = new ResultDto<>(code, errorMessage);
        return MapUtil.beanToMap(resultDto);
    }
}
