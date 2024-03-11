package io.fusion.sse.controller;

import com.alibaba.fastjson.JSONObject;
import io.fusion.framework.spring.boot.okhttp3.utils.OkHttp3Utils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
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

    @PostMapping("/sse/provider/post/{count}")
    public SseEmitter handlePostSse(@PathVariable("count") Integer count, @RequestBody String body) {
        SseEmitter emitter = new SseEmitter();
        AtomicInteger id = new AtomicInteger();
        nonBlockingService.execute(() -> {
            try {
                for (int i = 0; i < count; i++) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("code", 200);
                    jsonObject.put("message", "成功");
                    jsonObject.put("data", LocalDateTime.now());

                    emitter.send(SseEmitter.event()
                            .id(String.valueOf(id.incrementAndGet()))
                            .name("reply")
                            .data(jsonObject.toJSONString())
                    );
                    Thread.sleep(1000);
                }

                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }

    @Autowired
    private OkHttp3Utils okHttp3Utils;

    static class DefaultEventSourceListener extends EventSourceListener {

        private final SseEmitter emitter;

        public DefaultEventSourceListener(SseEmitter emitter) {
            this.emitter = emitter;
        }

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
    }


    @GetMapping("/sse/consumer/{count}")
    public SseEmitter consumerSse(@PathVariable("count") Integer count) {
        SseEmitter emitter = new SseEmitter();

        okHttp3Utils.getOfSse("http://localhost:9010/sse/provider/" + count, null,
                new DefaultEventSourceListener(emitter));

        return emitter;
    }

    @PostMapping("/sse/consumer/post/{count}")
    public SseEmitter consumerPostSse(@PathVariable("count") Integer count, @RequestBody String body) {
        SseEmitter emitter = new SseEmitter();

        okHttp3Utils.postOfSse("http://localhost:9010/sse/provider/post/" + count, null, null,
                body, new DefaultEventSourceListener(emitter));

        return emitter;
    }
}