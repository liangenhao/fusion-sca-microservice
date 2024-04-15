package io.fusion.consumer.config;

import io.fusion.consumer.client.ProviderServiceEchoRpcClientFallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration {

    @Bean
    public ProviderServiceEchoRpcClientFallback echoFeignClientFallback() {
        return new ProviderServiceEchoRpcClientFallback();
    }
}
