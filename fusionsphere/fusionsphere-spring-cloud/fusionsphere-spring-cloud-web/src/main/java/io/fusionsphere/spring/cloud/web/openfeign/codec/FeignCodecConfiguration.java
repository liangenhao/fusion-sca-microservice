package io.fusionsphere.spring.cloud.web.openfeign.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import feign.optionals.OptionalDecoder;
import io.fusionsphere.spring.cloud.web.constant.HeaderConst;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author enhao
 */
@Configuration(proxyBeanMethods = false)
public class FeignCodecConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Decoder apiResponseUnwrapFeignDecoder(ObjectFactory<HttpMessageConverters> messageConverters,
                                           ObjectProvider<HttpMessageConverterCustomizer> customizers,
                                           ObjectMapper objectMapper) {
        OptionalDecoder delegate = new OptionalDecoder(new ResponseEntityDecoder(new SpringDecoder(messageConverters, customizers)));
        return new ApiResponseUnwrapFeignDecoder(delegate, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public ErrorDecoder apiResponseFeignErrorDecoder(ObjectMapper objectMapper) {
        return new ApiResponseFeignErrorDecoder(objectMapper);
    }

    @Bean
    public RequestInterceptor headerInterceptor() {
        return requestTemplate -> requestTemplate.header(HeaderConst.X_RPC_TAG_HEADER, HeaderConst.RPC_FEIGN);
    }
}
