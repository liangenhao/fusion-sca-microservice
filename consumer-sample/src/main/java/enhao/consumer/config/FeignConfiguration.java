package enhao.consumer.config;

import enhao.consumer.client.EchoFeignClientFallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration {

    @Bean
    public EchoFeignClientFallback echoFeignClientFallback() {
        return new EchoFeignClientFallback();
    }
}
