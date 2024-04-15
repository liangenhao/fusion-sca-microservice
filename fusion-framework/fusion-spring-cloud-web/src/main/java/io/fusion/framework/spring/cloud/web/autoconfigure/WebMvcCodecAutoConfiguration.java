package io.fusion.framework.spring.cloud.web.autoconfigure;

import io.fusion.framework.spring.cloud.web.servlet.mvc.ApiResponseGlobalExceptionHandler;
import io.fusion.framework.spring.cloud.web.servlet.mvc.ApiResponseWrapConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Servlet;

import static io.fusion.framework.spring.cloud.web.autoconfigure.WebMvcCodecAutoConfiguration.PREFIX;

/**
 * @author enhao
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({DispatcherServlet.class, Servlet.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = PREFIX, name = "codec.enabled", havingValue = "true", matchIfMissing = true)
@Import({ApiResponseGlobalExceptionHandler.class, ApiResponseWrapConfiguration.class})
public class WebMvcCodecAutoConfiguration {

    public static final String PREFIX = "fusion.webmvc";
}

