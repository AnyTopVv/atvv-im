package com.atvv.im.gateway.handler;


import com.atvv.im.common.constant.enums.common.ErrorCode;
import com.atvv.im.common.model.vo.ResponseVO;
import com.atvv.im.gateway.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
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

//    @Override
//    protected Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
//        int code = ErrorCode.ERROR.getCode();
//        Throwable error = super.getError(request);
//        if (error instanceof ServiceException) {
//            code = ((ServiceException) error).getErrorCode();
//        }
//        String errorMessage = error.getMessage();
//        log.error("异常信息:{}", error.getMessage());
//        return response(code, this.buildMessage(request, errorMessage));
//    }

    @Override
    protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        // 这里和父类的做法一样，取得DefaultErrorAttributes整理出来的所有异常信息
        Map<String, Object> errorMap = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.ALL));
        int code = getHttpStatus(errorMap);
        Throwable error = super.getError(request);
        if (error instanceof ServiceException) {
            code = ((ServiceException) error).getErrorCode();
        }
        String errorMessage = error.getMessage();
        log.error("异常信息:{}", error.getMessage());
        return ServerResponse
                // http返回码
                .status(code)
                // 类型和以前一样
                .contentType(MediaType.APPLICATION_JSON)
                // 响应body的内容
                .body(BodyInserters.fromValue(response(code,this.buildMessage(request, errorMessage))));
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
        return (int) errorAttributes.get("status");
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
        Map<String, Object> map = new HashMap<>();
        map.put("code",code);
        map.put("msg",errorMessage);
        map.put("data",null);
        return map;
    }
}
