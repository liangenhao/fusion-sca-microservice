package io.fusion.consumer.client;

import io.fusion.api.interfaces.ProviderServiceEchoApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "fusion-provider",
        // fallback = ProviderServiceEchoRpcClientFallback.class,
        fallbackFactory = ProviderServiceEchoRpcClientFallbackFactory.class)
public interface ProviderServiceEchoRpcClient extends ProviderServiceEchoApi {
}
