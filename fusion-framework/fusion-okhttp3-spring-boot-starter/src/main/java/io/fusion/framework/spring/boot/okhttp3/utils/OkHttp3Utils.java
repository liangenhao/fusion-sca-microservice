package io.fusion.framework.spring.boot.okhttp3.utils;

import com.alibaba.fastjson.JSON;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.internal.sse.RealEventSource;
import okhttp3.sse.EventSourceListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * {@link okhttp3.OkHttpClient} Utils
 *
 * @author enhao
 */
@Slf4j
public class OkHttp3Utils {

    public static final String APPLICATION_JSON = "application/json;charset=UTF-8";

    private final OkHttpClient okHttpClient;

    public OkHttp3Utils(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    // GET 请求

    public String get(String url) {
        return get(url, null, String.class, null);
    }

    public String get(String url, Map<String, String> headers) {
        return get(url, headers, String.class, null);
    }

    public String get(String url, Map<String, String> headers, TimeoutConfig timeoutConfig) {
        return get(url, headers, String.class, timeoutConfig);
    }

    public <T> T get(String url, Class<T> cls) {
        return get(url, null, cls, null);
    }

    /**
     * GET 请求
     *
     * @param url           请求地址
     * @param headers       请求头 {@link HashMap}
     * @param cls           响应结果转换 Class 对象
     * @param timeoutConfig {@link TimeoutConfig} 超时时间配置
     * @param <T>           响应结果泛型
     * @return T
     */
    public <T> T get(String url, Map<String, String> headers, Class<T> cls, TimeoutConfig timeoutConfig) {
        Request request = getRequest("GET", url, headers, null, null, timeoutConfig);
        return execute(request, cls);
    }

    public void getAsync(String url, Map<String, String> headers, Consumer<Optional<String>> successHandler) {
        getAsync(url, headers, String.class, successHandler, DEFAULT_FAIL_HANDLER, DEFAULT_EXCEPTION_HANDLER, null);
    }

    public void getAsync(String url, Map<String, String> headers, Consumer<Optional<String>> successHandler,
                         TimeoutConfig timeoutConfig) {
        getAsync(url, headers, String.class, successHandler, DEFAULT_FAIL_HANDLER, DEFAULT_EXCEPTION_HANDLER, timeoutConfig);
    }

    public void getAsync(String url, Map<String, String> headers, Consumer<Optional<String>> successHandler,
                         BiConsumer<Call, Response> failHandler, BiConsumer<Call, IOException> exceptionHandler) {
        getAsync(url, headers, String.class, successHandler, failHandler, exceptionHandler, null);
    }

    public void getAsync(String url, Map<String, String> headers, Consumer<Optional<String>> successHandler,
                         BiConsumer<Call, Response> failHandler, BiConsumer<Call, IOException> exceptionHandler,
                         TimeoutConfig timeoutConfig) {
        getAsync(url, headers, String.class, successHandler, failHandler, exceptionHandler, timeoutConfig);
    }

    /**
     * GET 异步请求
     *
     * @param url              请求地址
     * @param headers          请求头 {@link HashMap}
     * @param cls              响应结果转换 Class 对象
     * @param successHandler   成功回调处理器
     * @param failHandler      失败回调处理器
     * @param exceptionHandler 异常回调处理器
     * @param timeoutConfig    {@link TimeoutConfig} 超时时间配置
     * @param <T>              响应结果泛型
     */
    public <T> void getAsync(String url, Map<String, String> headers, Class<T> cls, Consumer<Optional<T>> successHandler,
                             BiConsumer<Call, Response> failHandler, BiConsumer<Call, IOException> exceptionHandler,
                             TimeoutConfig timeoutConfig) {
        Request request = getRequest("GET", url, headers, null, null, timeoutConfig);
        executeAsync(request, cls, successHandler, failHandler, exceptionHandler);
    }

    /**
     * Server-Sent Events GET 请求
     *
     * @param url                 请求地址
     * @param headers             请求头 {@link HashMap}
     * @param eventSourceListener {@link EventSourceListener}
     */
    public void getOfSse(String url, Map<String, String> headers, EventSourceListener eventSourceListener) {
        Request request = getRequest("GET", url, headers, null, null, null);
        executeOfSse(request, eventSourceListener);
    }

    // POST 请求

    public String post(String url, String body) {
        return post(url, null, APPLICATION_JSON, body, String.class, null);
    }

    public String post(String url, String contentType, String body) {
        return post(url, null, contentType, body, String.class, null);
    }

    public String post(String url, Map<String, String> headers, String body) {
        return post(url, headers, APPLICATION_JSON, body, String.class, null);
    }

    public String post(String url, Map<String, String> headers, String contentType, String body) {
        return post(url, headers, contentType, body, String.class, null);
    }

    public String post(String url, Map<String, String> headers, String contentType, String body, TimeoutConfig timeoutConfig) {
        return post(url, headers, contentType, body, String.class, timeoutConfig);
    }

    public <T> T post(String url, String body, Class<T> cls) {
        return post(url, null, APPLICATION_JSON, body, cls, null);
    }

    public <T> T post(String url, String contentType, String body, Class<T> cls) {
        return post(url, null, contentType, body, cls, null);
    }

    public <T> T post(String url, Map<String, String> headers, String body, Class<T> cls) {
        return post(url, headers, APPLICATION_JSON, body, cls, null);
    }

    /**
     * POST 请求
     *
     * @param url           请求地址
     * @param headers       请求头 {@link HashMap}
     * @param contentType   内容类型，默认{@link #APPLICATION_JSON}
     * @param body          请求体，JSON 字符串
     * @param cls           响应结果转换 Class 对象
     * @param timeoutConfig {@link TimeoutConfig} 超时时间配置
     * @param <T>           响应结果泛型
     * @return T
     */
    public <T> T post(String url, Map<String, String> headers, String contentType, String body, Class<T> cls,
                      TimeoutConfig timeoutConfig) {
        Request request = getRequest("POST", url, headers, contentType, body, timeoutConfig);
        return execute(request, cls);
    }

    public <T> void postAsync(String url, Map<String, String> headers, String contentType, String body, Class<T> cls,
                              Consumer<Optional<T>> successHandler) {
        postAsync(url, headers, contentType, body, cls, successHandler, null);
    }

    public <T> void postAsync(String url, Map<String, String> headers, String contentType, String body, Class<T> cls,
                              Consumer<Optional<T>> successHandler, TimeoutConfig timeoutConfig) {
        postAsync(url, headers, contentType, body, cls, successHandler, DEFAULT_FAIL_HANDLER, DEFAULT_EXCEPTION_HANDLER, timeoutConfig);
    }

    /**
     * POST 异步请求
     *
     * @param url              请求地址
     * @param headers          请求头 {@link HashMap}
     * @param contentType      内容类型，默认{@link #APPLICATION_JSON}
     * @param body             请求体，JSON 字符串
     * @param cls              响应结果转换 Class 对象
     * @param successHandler   成功回调处理器
     * @param failHandler      失败回调处理器
     * @param exceptionHandler 异常回调处理器
     * @param timeoutConfig    {@link TimeoutConfig} 超时时间配置
     * @param <T>              响应结果泛型
     */
    public <T> void postAsync(String url, Map<String, String> headers, String contentType, String body, Class<T> cls,
                              Consumer<Optional<T>> successHandler, BiConsumer<Call, Response> failHandler,
                              BiConsumer<Call, IOException> exceptionHandler, TimeoutConfig timeoutConfig) {
        Request request = getRequest("POST", url, headers, contentType, body, timeoutConfig);
        executeAsync(request, cls, successHandler, failHandler, exceptionHandler);
    }

    /**
     * Server-Sent Events POST 请求
     *
     * @param url                 请求地址
     * @param headers             请求头 {@link HashMap}
     * @param contentType         内容类型，默认{@link #APPLICATION_JSON}
     * @param body                请求体，JSON 字符串
     * @param eventSourceListener {@link EventSourceListener}
     */
    public void postOfSse(String url, Map<String, String> headers, String contentType, String body,
                          EventSourceListener eventSourceListener) {
        Request request = getRequest("POST", url, headers, contentType, body, null);
        executeOfSse(request, eventSourceListener);
    }

    private Request getRequest(String method, String url, Map<String, String> headers, String contentType, String body,
                              TimeoutConfig timeoutConfig) {
        contentType = Optional.ofNullable(contentType).orElse(APPLICATION_JSON);

        Request.Builder builder = new Request.Builder().url(url);

        if ("GET".equals(method)) {
            builder.get();
        } else if ("POST".equals(method)) {
            builder.post(RequestBody.create(MediaType.get(contentType), body));
        } else if ("PUT".equals(method)) {
            builder.put(RequestBody.create(MediaType.get(contentType), body));
        } else if ("DELETE".equals(method)) {
            builder.delete(RequestBody.create(MediaType.get(contentType), body));
        } else {
            throw new IllegalArgumentException("Request method '" + method + "' Not Supported");
        }

        Map<String, String> headerMap = Optional.ofNullable(headers).orElseGet(HashMap::new);
        Optional.ofNullable(timeoutConfig).ifPresent(t -> headerMap.put("X-Timeout", t.toString()));

        builder.headers(Headers.of(headerMap));

        return builder.build();
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private <T> T execute(Request request, Class<T> cls) {
        if (null == cls) {
            throw new IllegalArgumentException("cls not be null.");
        }
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        if (!response.isSuccessful()) {
            log.error("[OkHttp3Utils] Sync Invoke Failed {} {}", request, response);
            return null;
        }

        if (null == response.body()) {
            return null;
        }
        // string() 会自动关闭 ResponseBody
        String body = response.body().string();

        if (String.class.equals(cls)) {
            return (T) body;
        }

        return JSON.parseObject(body, cls);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private <T> void executeAsync(Request request, Class<T> cls,
                                 Consumer<Optional<T>> successHandler,
                                 BiConsumer<Call, Response> failHandler,
                                 BiConsumer<Call, IOException> exceptionHandler) {
        if (null == cls) {
            throw new IllegalArgumentException("cls not be null.");
        }
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (null != exceptionHandler) {
                    exceptionHandler.accept(call, e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    if (null != failHandler) {
                        failHandler.accept(call, response);
                    }
                    return;
                }

                if (null == response.body()) {
                    if (null != successHandler) {
                        successHandler.accept(Optional.empty());
                    }
                    return;
                }

                String body = response.body().string();
                T result = String.class.equals(cls) ? (T) body : JSON.parseObject(body, cls);
                if (null != successHandler) {
                    successHandler.accept(Optional.ofNullable(result));
                }
            }
        });
    }

    private void executeOfSse(Request request, EventSourceListener eventSourceListener) {
        RealEventSource realEventSource = new RealEventSource(request, eventSourceListener);
        realEventSource.connect(okHttpClient);
    }

    private static final BiConsumer<Call, Response> DEFAULT_FAIL_HANDLER = (call, response) -> {
        log.error("[OkHttp3Utils] Async Invoke Failed, {} {}", call.request(), response);
    };

    private static final BiConsumer<Call, IOException> DEFAULT_EXCEPTION_HANDLER = (call, exception) -> {
        log.error("[OkHttp3Utils] Async Invoke Exception, {} ", call.request(), exception);
    };

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeoutConfig {

        /**
         * 单位毫秒
         */
        private Integer connectionTimeout = 10_000;

        /**
         * 单位毫秒
         */
        private Integer readTimeout = 10_000;

        /**
         * 单位毫秒
         */
        private Integer writeTimeout = 10_000;

        @Override
        public String toString() {
            Integer connectionTimeoutMs = Optional.ofNullable(connectionTimeout).orElse(10_000);
            Integer readTimeoutMs = Optional.ofNullable(readTimeout).orElse(10_000);
            Integer writeTimeoutMs = Optional.ofNullable(writeTimeout).orElse(10_000);
            return String.format("%d:%d:%d", connectionTimeoutMs, readTimeoutMs, writeTimeoutMs);
        }
    }

}
