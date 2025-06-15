package io.fusion.distributed.transaction.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author enhao
 */
public interface StorageServiceApi {

    @PostMapping(value = "/distributed-transaction/storage/{commodityCode}/{count}", produces = "application/json")
    String storage(@PathVariable String commodityCode, @PathVariable Integer count);

    @PostMapping(value = "/distributed-transaction/tcc/deductStorageCount/{commodityCode}/{count}", produces = "application/json")
    String deductStorageCount(@PathVariable String commodityCode, @PathVariable Integer count);
}
