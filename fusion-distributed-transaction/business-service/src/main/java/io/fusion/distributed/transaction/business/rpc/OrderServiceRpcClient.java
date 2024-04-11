package io.fusion.distributed.transaction.business.rpc;

import io.fusion.distributed.transaction.api.OrderServiceApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author enhao
 */
@FeignClient("order-service")
public interface OrderServiceRpcClient extends OrderServiceApi {
}
