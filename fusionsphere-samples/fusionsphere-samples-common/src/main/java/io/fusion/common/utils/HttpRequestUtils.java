package io.fusion.common.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Http Request Utilities
 *
 * @author enhao
 */
public class HttpRequestUtils {

    private HttpRequestUtils() {
    }

    /**
     * 获取当前请求的 {@link HttpServletRequest} 对象
     *
     * @return {@link HttpServletRequest} 对象
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest();
    }
}
