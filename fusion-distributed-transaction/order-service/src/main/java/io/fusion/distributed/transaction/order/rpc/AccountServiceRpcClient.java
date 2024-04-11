package io.fusion.distributed.transaction.order.rpc;

import io.fusion.distributed.transaction.api.AccountServiceApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author enhao
 */
@FeignClient("account-service")
public interface AccountServiceRpcClient extends AccountServiceApi {
}
