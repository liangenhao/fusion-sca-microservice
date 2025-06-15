package io.fusionsphere.spring.cloud.web.servlet.mvc;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author enhao
 */
@Configuration(proxyBeanMethods = false)
public class ApiResponseWrapConfiguration implements InitializingBean {

    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    public ApiResponseWrapConfiguration(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<HandlerMethodReturnValueHandler> returnValueHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();
        if (returnValueHandlers == null) {
            return;
        }
        ArrayList<HandlerMethodReturnValueHandler> newHandlers = new ArrayList<>(returnValueHandlers.size());
        returnValueHandlers.forEach(handler -> {
            if (handler instanceof RequestResponseBodyMethodProcessor) {
                newHandlers.add(new ApiResponseWrapReturnValueHandler(handler));
            } else {
                newHandlers.add(handler);
            }
        });

        requestMappingHandlerAdapter.setReturnValueHandlers(newHandlers);
    }
}
