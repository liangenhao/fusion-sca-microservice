package io.fusion.framework.spring.boot.okhttp3.utils;

import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.internal.sse.RealEventSource;
import okhttp3.sse.EventSourceListener;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * {@link okhttp3.OkHttpClient} Utils，不依赖于 Spring 环境，静态方法调用。
 *
 * @author enhao
 */
@Slf4j
public class OkHttp3PureUtils {

    public static final String APPLICATION_JSON = "application/json;charset=UTF-8";

    private static final OkHttpClient okHttpClient;

    private static final X509TrustManager x509TrustManager = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };

    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .sslSocketFactory(getSocketFactory(), x509TrustManager)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .pingInterval(0, TimeUnit.SECONDS)
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES));

        okHttpClient = builder.build();
    }

    @SneakyThrows
    private static SSLSocketFactory getSocketFactory() {
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[]{x509TrustManager}, null);
        return sc.getSocketFactory();
    }

    // GET 请求

    public static String get(String url) {
        return get(url, null, String.class);
    }

    public static String get(String url, Map<String, String> headers) {
        return get(url, headers, String.class);
    }

    public static <T> T get(String url, Class<T> cls) {
        return get(url, null, cls);
    }

    public static <T> T get(String url, Map<String, String> headers, Class<T> cls) {
        Request request = getRequest("GET", url, headers, null, null);
        return execute(request, cls);
    }

    public static void getAsync(String url, Map<String, String> headers, Consumer<Optional<String>> successHandler) {
        getAsync(url, headers, String.class, successHandler, DEFAULT_FAIL_HANDLER, DEFAULT_EXCEPTION_HANDLER);
    }

    public static void getAsync(String url, Map<String, String> headers, Consumer<Optional<String>> successHandler,
                                BiConsumer<Call, Response> failHandler, BiConsumer<Call, IOException> exceptionHandler) {
        getAsync(url, headers, String.class, successHandler, failHandler, exceptionHandler);
    }

    public static <T> void getAsync(String url, Map<String, String> headers, Class<T> cls, Consumer<Optional<T>> successHandler,
                                    BiConsumer<Call, Response> fail, BiConsumer<Call, IOException> exception) {
        Request request = getRequest("GET", url, headers, null, null);
        executeAsync(request, cls, successHandler, fail, exception);
    }

    public static void getOfSse(String url, Map<String, String> headers, EventSourceListener eventSourceListener) {
        Request request = getRequest("GET", url, headers, null, null);
        executeOfSse(request, eventSourceListener);
    }

    // POST 请求

    public static String post(String url, String body) {
        return post(url, null, APPLICATION_JSON, body, String.class);
    }

    public static String post(String url, String contentType, String body) {
        return post(url, null, contentType, body, String.class);
    }

    public static String post(String url, Map<String, String> headers, String body) {
        return post(url, headers, APPLICATION_JSON, body, String.class);
    }

    public static String post(String url, Map<String, String> headers, String contentType, String body) {
        return post(url, headers, contentType, body, String.class);
    }

    public static <T> T post(String url, String body, Class<T> cls) {
        return post(url, null, APPLICATION_JSON, body, cls);
    }

    public static <T> T post(String url, String contentType, String body, Class<T> cls) {
        return post(url, null, contentType, body, cls);
    }

    public static <T> T post(String url, Map<String, String> headers, String body, Class<T> cls) {
        return post(url, headers, APPLICATION_JSON, body, cls);
    }

    public static <T> T post(String url, Map<String, String> headers, String contentType, String body, Class<T> cls) {
        Request request = getRequest("POST", url, headers, contentType, body);
        return execute(request, cls);
    }

    public static <T> void postAsync(String url, Map<String, String> headers, String contentType, String body, Class<T> cls,
                                     Consumer<Optional<T>> successHandler) {
        Request request = getRequest("POST", url, headers, contentType, body);
        executeAsync(request, cls, successHandler, DEFAULT_FAIL_HANDLER, DEFAULT_EXCEPTION_HANDLER);
    }

    public static <T> void postAsync(String url, Map<String, String> headers, String contentType, String body, Class<T> cls,
                                     Consumer<Optional<T>> successHandler, BiConsumer<Call, Response> failHandler,
                                     BiConsumer<Call, IOException> exceptionHandler) {
        Request request = getRequest("POST", url, headers, contentType, body);
        executeAsync(request, cls, successHandler, failHandler, exceptionHandler);
    }

    public static void postOfSse(String url, Map<String, String> headers, String contentType, String body,
                                 EventSourceListener eventSourceListener) {
        Request request = getRequest("POST", url, headers, contentType, body);
        executeOfSse(request, eventSourceListener);
    }

    public static Request getRequest(String method, String url, Map<String, String> headers, String contentType, String body) {
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

        Optional.ofNullable(headers).ifPresent(header -> header.forEach(builder::addHeader));

        return builder.build();
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> T execute(Request request, Class<T> cls) {
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
    public static <T> void executeAsync(Request request, Class<T> cls,
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

    private static void executeOfSse(Request request, EventSourceListener eventSourceListener) {
        RealEventSource realEventSource = new RealEventSource(request, eventSourceListener);
        realEventSource.connect(okHttpClient);
    }

    private static final BiConsumer<Call, Response> DEFAULT_FAIL_HANDLER = (call, response) -> {
        log.error("[OkHttp3Utils] Async Invoke Failed, {} {}", call.request(), response);
    };

    private static final BiConsumer<Call, IOException> DEFAULT_EXCEPTION_HANDLER = (call, exception) -> {
        log.error("[OkHttp3Utils] Async Invoke Exception, {} ", call.request(), exception);
    };
}
