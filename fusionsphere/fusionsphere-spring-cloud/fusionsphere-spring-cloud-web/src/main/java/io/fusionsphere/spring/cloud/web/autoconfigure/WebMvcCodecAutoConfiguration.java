package io.fusionsphere.spring.cloud.web.autoconfigure;

import io.fusionsphere.spring.cloud.web.servlet.mvc.ApiResponseGlobalExceptionHandler;
import io.fusionsphere.spring.cloud.web.servlet.mvc.ApiResponseWrapConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Servlet;

/**
 * @author enhao
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({DispatcherServlet.class, Servlet.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = WebMvcCodecAutoConfiguration.PREFIX, name = "codec.enabled", havingValue = "true", matchIfMissing = true)
@Import({ApiResponseGlobalExceptionHandler.class, ApiResponseWrapConfiguration.class})
public class WebMvcCodecAutoConfiguration {

    public static final String PREFIX = "fusionsphere.webmvc";
}

