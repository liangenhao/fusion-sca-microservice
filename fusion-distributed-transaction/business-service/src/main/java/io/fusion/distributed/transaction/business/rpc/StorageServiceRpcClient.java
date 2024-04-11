package io.fusion.distributed.transaction.business.rpc;

import io.fusion.distributed.transaction.api.StorageServiceApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author enhao
 */
@FeignClient("storage-service")
public interface StorageServiceRpcClient extends StorageServiceApi {
}
