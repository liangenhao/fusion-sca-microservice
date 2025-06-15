package io.fusion.distributed.transaction.order.rpc;

import io.fusion.distributed.transaction.api.AccountServiceApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author enhao
 */
@FeignClient(value = "account-service"/*, fallbackFactory = AccountServiceRpcClientFallbackFactory.class*/)
public interface AccountServiceRpcClient extends AccountServiceApi {
}
