package io.fusion.spring.event;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author enhao
 */
@EnableAsync
public class ApplicationListenerDemo {

    // 基于 ApplicationListener 接口的事件监听
    @Test
    public void testApplicationListener() {
        GenericApplicationContext context = new GenericApplicationContext();

        // 向 Spring 应用上下文注册注册事件监听器，监听 ApplicationEvent 事件，即所有的 应用事件
        context.addApplicationListener(new ApplicationListener<ApplicationEvent>() {
            @Override
            public void onApplicationEvent(ApplicationEvent event) {
                println("ApplicationListener 接受到 Spring 事件： " + event);
            }
        });

        context.refresh();

        // context.start();

        context.close();
    }

    // 基于 @EventListener 注解的事件监听
    @Test
    public void testEventListener() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        // 将引导类 ApplicationListenerDemo 作为 configuration class
        context.register(ApplicationListenerDemo.class);

        // 基于 @EventListener 注解

        context.refresh();

        context.start();

        context.close();
    }



    @EventListener
    @Order(2)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        println("@EventListener(onApplicationEvent) 接受到 Spring ContextRefreshedEvent： " + event);
    }

    @EventListener
    @Order(1)
    public void onApplicationEvent2(ContextRefreshedEvent event) {
        println("@EventListener(onApplicationEvent2) 接受到 Spring ContextRefreshedEvent： " + event);
    }


    @EventListener
    public void onApplicationEvent(ContextStartedEvent event) {
        println("@EventListener 接受到 Spring ContextStartedEvent： " + event);
    }

    @EventListener
    public void onApplicationEvent(ContextClosedEvent event) {
        println("@EventListener 接受到 Spring ContextClosedEvent： " + event);
    }

    @EventListener
    @Async
    public void onApplicationEventAsync(ContextRefreshedEvent event) {
        println("@EventListener（异步） 接受到 Spring ContextRefreshedEvent： " + event);
    }

    private static void println(Object printable) {
        System.out.printf("[线程：%s] : %s\n", Thread.currentThread().getName(), printable);
    }
}
