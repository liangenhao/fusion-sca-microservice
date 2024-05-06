package io.fusion.distributed.transaction.config;

import feign.Feign;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * {@link RequestMappingHandlerMapping#isHandler(Class)} 方法会将标注了
 * {@link org.springframework.stereotype.Controller} 和 {@link org.springframework.web.bind.annotation.RequestMapping}
 * 注解的类都当作 Controller 处理。
 * 通过 feign 服务调用利用抽取的公共 api 作为 FeignClient时，将 {@link FeignClient} 标注在类上时，就会重复注入。
 * <p>
 * 通过本配置类，重写 {@code isHandler} 方法，排除标注了 {@link FeignClient} 为 Handler 的判断。
 * 当然，也可以通过不在类上标注 {@link org.springframework.web.bind.annotation.RequestMapping} 注解予以避免，但会损失该特性。
 * <p>
 * NOTE: Spring 6 版本已修改为只根据 {@link org.springframework.stereotype.Controller} 注解判断。
 * <p>
 * 但是 spring-cloud-openfeign 已不支持在类级别上使用 {@link org.springframework.web.bind.annotation.RequestMapping}
 *
 * @author enhao
 * @see <a href="https://github.com/spring-cloud/spring-cloud-netflix/issues/466">@FeignClient with top level @RequestMapping annotation is also registered as Spring MVC handler</a>
 * @see <a href="https://github.com/spring-projects/spring-framework/issues/22154#issuecomment-936906502">@RequestMapping without @Controller registered as handler</a>
 * @see <a href="https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/#spring-cloud-feign-inheritance">Feign Inheritance Support</a>
 * @see <a href="https://github.com/spring-cloud/spring-cloud-openfeign/issues/678">Bring back @RequestMapping</a>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Feign.class, FeignClient.class})
@Deprecated
public class FeignMappingConfiguration {

    @Bean
    public WebMvcRegistrations feignWebMvcRegistrations() {
        return new WebMvcRegistrations() {
            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                return new FeignFilterRequestMappingHandlerMapping();
            }
        };
    }

    private static class FeignFilterRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
        @Override
        protected boolean isHandler(Class<?> beanType) {
            return super.isHandler(beanType)
                    && !AnnotatedElementUtils.hasAnnotation(beanType, FeignClient.class);
        }
    }
}
