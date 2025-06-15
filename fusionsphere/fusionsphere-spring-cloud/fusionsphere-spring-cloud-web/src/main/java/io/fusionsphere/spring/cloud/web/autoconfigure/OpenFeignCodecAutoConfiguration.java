package io.fusionsphere.spring.cloud.web.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import io.fusionsphere.spring.cloud.web.openfeign.codec.FeignCodecConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author enhao
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Encoder.class, Decoder.class, ErrorDecoder.class, ObjectMapper.class, FeignClient.class})
@ConditionalOnProperty(prefix = OpenFeignCodecAutoConfiguration.PREFIX, name = "codec.enabled", havingValue = "true", matchIfMissing = true)
@Import(FeignCodecConfiguration.class)
public class OpenFeignCodecAutoConfiguration {

    public static final String PREFIX = "fusionsphere.feign";
}
