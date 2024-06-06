package io.fusion.spring.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author enhao
 */
public class MySpringEvent extends ApplicationEvent {

    public MySpringEvent(Object source) {
        super(source);
    }
}
