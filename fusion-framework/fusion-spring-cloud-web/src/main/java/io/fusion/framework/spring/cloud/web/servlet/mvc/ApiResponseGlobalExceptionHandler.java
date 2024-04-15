package io.fusion.framework.spring.cloud.web.servlet.mvc;

import feign.codec.DecodeException;
import io.fusion.framework.core.api.ApiResponse;
import io.fusion.framework.core.enums.ApiStatusCode;
import io.fusion.framework.core.exception.BizException;
import io.fusion.framework.core.exception.FeignRpcBizException;
import io.fusion.framework.spring.cloud.web.constant.HeaderConst;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author enhao
 */
@Slf4j
@RestControllerAdvice
public class ApiResponseGlobalExceptionHandler {

    @ExceptionHandler(DecodeException.class)
    public ApiResponse<Void> onDecodeException(DecodeException exception, HandlerMethod handlerMethod,
                                               HttpServletRequest request, HttpServletResponse response) {
        Throwable cause = exception.getCause();
        if (cause instanceof BizException) {
            return onBizException((BizException) cause, handlerMethod, request, response);
        }
        log.error("[onDecodeException]", exception);
        ApiResponse<Void> apiResponse = ApiResponse.fail(ApiStatusCode.FEIGN_DECODER_EXCEPTION,
                (null == cause) ? ExceptionUtils.getMessage(exception) : ExceptionUtils.getMessage(cause));
        return handleRpcException(apiResponse, handlerMethod, request, response);
    }

    @ExceptionHandler(BizException.class)
    public ApiResponse<Void> onBizException(BizException exception, HandlerMethod handlerMethod,
                                            HttpServletRequest request, HttpServletResponse response) {
        log.error("[onBizException]", exception);
        ApiResponse<Void> apiResponse = ApiResponse.fail(exception.getStatusCode(), exception.getReason());
        return handleRpcException(apiResponse, handlerMethod, request, response);
    }

    @ExceptionHandler(FeignRpcBizException.class)
    public ApiResponse<Void> onFeignRpcBizException(FeignRpcBizException exception, HandlerMethod handlerMethod,
                                                    HttpServletRequest request, HttpServletResponse response) {
        log.error("[onFeignRpcBizException]", exception);
        ApiResponse<Void> apiResponse = ApiResponse.fail(exception.getCode(), exception.getCodeMessage(), exception.getReason());
        return handleRpcException(apiResponse, handlerMethod, request, response);
    }

    @ExceptionHandler(Throwable.class)
    public ApiResponse<Void> onThrowable(Throwable throwable, HandlerMethod handlerMethod,
                                         HttpServletRequest request, HttpServletResponse response) {
        log.error("[onThrowable]", throwable);
        ApiResponse<Void> apiResponse = ApiResponse.error(ExceptionUtils.getRootCauseMessage(throwable));
        return handleRpcException(apiResponse, handlerMethod, request, response);
    }

    private ApiResponse<Void> handleRpcException(ApiResponse<Void> apiResponse, HandlerMethod handlerMethod,
                                                 HttpServletRequest request, HttpServletResponse response) {
        String rpcTag = request.getHeader(HeaderConst.X_RPC_TAG_HEADER);
        Class<?> returnCls = handlerMethod.getReturnType().getParameterType();
        boolean isVoidReturnCls = Void.class == returnCls || void.class == returnCls;
        if (HeaderConst.RPC_FEIGN.equals(rpcTag) && isVoidReturnCls) {
            // feign rpc 调用 且 方法返回值是 void 类型时，将 Http Status 设置为500，通过 Feign ErrorDecoder 处理异常情况
            // 因为 void 类型返回值方法不经过 Feign Decoder 处理，所以异常会丢失。
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return apiResponse;
    }
}
