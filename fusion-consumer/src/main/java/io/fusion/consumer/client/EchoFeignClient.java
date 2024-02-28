package io.fusion.consumer.client;

import io.fusion.consumer.config.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "fusion-provider", configuration = FeignConfiguration.class)
public interface EchoFeignClient {

    @GetMapping(value = "/echo/{string}")
    String echo(@PathVariable("string") String string);
}
