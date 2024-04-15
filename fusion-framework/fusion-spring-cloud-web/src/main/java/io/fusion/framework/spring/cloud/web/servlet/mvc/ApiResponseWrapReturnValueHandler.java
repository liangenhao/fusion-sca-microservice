package io.fusion.framework.spring.cloud.web.servlet.mvc;

import io.fusion.framework.core.api.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Rest Api 返回值针对 {@link ApiResponse} 包装 {@link HandlerMethodReturnValueHandler} 实现
 * @author enhao
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor
 */
public class ApiResponseWrapReturnValueHandler implements HandlerMethodReturnValueHandler {

    private final HandlerMethodReturnValueHandler delegate;

    public ApiResponseWrapReturnValueHandler(HandlerMethodReturnValueHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return delegate.supportsReturnType(returnType);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {
        if (returnValue == null) {
            delegate.handleReturnValue(null, returnType, mavContainer, webRequest);
            return;
        }

        if (!(returnValue instanceof ApiResponse)) {
            // 方法返回值不是 ApiResponse，进行包装
            returnValue = ApiResponse.ok(returnValue);
        }
        // 方法返回值是 ApiResponse，不重复包装
        delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }
}
