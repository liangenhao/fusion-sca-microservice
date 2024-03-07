package io.fusion.sse.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.sse.RealEventSource;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 整合实现 Server-Sent Events
 * <p>
 * API: /sse/provider/{count} 通过 {@link SseEmitter} 实现响应 event-stream 接口
 * <p>
 * API: /sse/consumer/{count} 通过 {@link OkHttpClient} 调用 /sse/provider/{count} 获取流数据，
 * 并通过 {@link SseEmitter} 实现响应 event-stream 接口
 *
 * @author enhao
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/API/Server-sent_events">Server-Sent Events</a>
 * @see <a href="https://www.ruanyifeng.com/blog/2017/05/server-sent_events.html">server-sent_events</a>
 */
@Slf4j
@Controller
public class ServerSentEventController {

    private final ExecutorService nonBlockingService = Executors
            .newCachedThreadPool();

    @GetMapping("/sse/provider/{count}")
    public SseEmitter handleSse(@PathVariable("count") Integer count) {
        SseEmitter emitter = new SseEmitter();
        AtomicInteger id = new AtomicInteger();
        nonBlockingService.execute(() -> {
            try {
                for (int i = 0; i < count; i++) {
                    emitter.send(SseEmitter.event()
                            .id(String.valueOf(id.incrementAndGet()))
                            .name("message")
                            .data("/sse" + " @ " + new Date())
                    );
                    Thread.sleep(1000);
                    // emitter.send(SseEmitter.event().data("only data"));
                }

                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }

    OkHttpClient httpClient = new OkHttpClient.Builder()
            .build();

    @GetMapping("/sse/consumer/{count}")
    public SseEmitter consumerSse(@PathVariable("count") Integer count) {
        SseEmitter emitter = new SseEmitter();

        Request request = new Request.Builder().url("http://localhost:9010/sse/provider/" + count).build();
        RealEventSource realEventSource = new RealEventSource(request, new EventSourceListener() {
            @Override
            public void onOpen(EventSource eventSource, Response response) {
            }

            @SneakyThrows
            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                SseEmitter.SseEventBuilder event = SseEmitter.event();
                if (StringUtils.hasLength(id)) {
                    event = event.id(id);
                }
                if (StringUtils.hasLength(type)) {
                    event = event.name(type);
                }
                emitter.send(event.data(data));
            }

            @Override
            public void onClosed(EventSource eventSource) {
                emitter.complete();
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                log.error("invoke error {}", eventSource.request(), t);
                emitter.completeWithError(t);
            }
        });
        realEventSource.connect(httpClient);

        return emitter;
    }
}