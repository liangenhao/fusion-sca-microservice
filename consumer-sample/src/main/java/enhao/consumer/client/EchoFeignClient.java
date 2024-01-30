package enhao.consumer.client;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import enhao.consumer.config.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "provider-sample", configuration = FeignConfiguration.class)
public interface EchoFeignClient {

    @GetMapping(value = "/echo/{string}")
    String echo(@PathVariable("string") String string);
}
