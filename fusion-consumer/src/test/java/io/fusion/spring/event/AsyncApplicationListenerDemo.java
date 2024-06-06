package io.fusion.spring.event;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * @author enhao
 */
@EnableAsync
public class AsyncApplicationListenerDemo {

    @Bean
    public static Executor taskExecutor() {
        ExecutorService taskExecutor = newSingleThreadExecutor(new CustomizableThreadFactory("my-spring-event-thread-pool-a"));
        return taskExecutor;
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        context.register(AsyncApplicationListenerDemo.class);
        context.register(OwnApplicationListener.class);

        context.refresh();

        context.publishEvent(new MySpringEvent("hello"));

        context.close();
    }
    static class OwnApplicationListener implements ApplicationListener<MySpringEvent> {
        @Override
        @Async
        public void onApplicationEvent(MySpringEvent event) {
            System.out.printf("[线程: %s] 接受到事件: %s", Thread.currentThread().getName(), event);
        }

    }

}
