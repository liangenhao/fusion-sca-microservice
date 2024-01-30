package enhao.consumer.client;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;

public class EchoFeignClientFallback implements EchoFeignClient {

    @Override
    public String echo(String string) {
        return "echo FeignClientFallback";
    }
}
