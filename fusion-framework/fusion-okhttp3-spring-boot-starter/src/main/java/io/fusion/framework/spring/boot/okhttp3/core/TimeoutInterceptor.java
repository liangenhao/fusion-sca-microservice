package io.fusion.framework.spring.boot.okhttp3.core;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp3 每个请求动态设置超时时间
 *
 * @author enhao
 */
public class TimeoutInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String timeout = request.header("X-Timeout");
        if (!StringUtils.hasText(timeout)) {
            return chain.proceed(request);
        }
        String[] timeoutArr = timeout.split(":");
        if (timeoutArr.length == 0) {
            return chain.proceed(request);
        }

        Chain newChain = null;
        int connectTimeout = Integer.parseInt(timeoutArr[0]);
        if (chain.connectTimeoutMillis() != connectTimeout) {
            newChain = chain.withConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        }

        if (timeoutArr.length >= 2) {
            int readTimeout = Integer.parseInt(timeoutArr[1]);
            if (chain.readTimeoutMillis() != readTimeout) {
                newChain = chain.withReadTimeout(readTimeout, TimeUnit.MILLISECONDS);
            }
        }
        if (timeoutArr.length >= 3) {
            int writeTimeout = Integer.parseInt(timeoutArr[2]);
            if (chain.writeTimeoutMillis() != writeTimeout) {
                newChain = chain.withWriteTimeout(writeTimeout, TimeUnit.MILLISECONDS);
            }
        }

        if (null == newChain) {
            return chain.proceed(request);
        }

        return newChain.proceed(request);
    }

}
