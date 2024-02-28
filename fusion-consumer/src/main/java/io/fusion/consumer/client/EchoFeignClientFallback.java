package io.fusion.consumer.client;

public class EchoFeignClientFallback implements EchoFeignClient {

    @Override
    public String echo(String string) {
        return "echo FeignClientFallback";
    }
}
