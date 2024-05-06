package io.fusion.distributed.transaction.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author enhao
 */
public interface OrderServiceApi {

    @PostMapping(value = "/distributed-transaction/order", produces = "application/json")
    String order(@RequestParam String userId, @RequestParam String commodityCode, @RequestParam int orderCount,
                 @RequestHeader String failPos);

    @PostMapping(value = "/distributed-transaction/tcc/preOrder", produces = "application/json")
    String preOrder(@RequestParam String userId, @RequestParam String commodityCode, @RequestParam int orderCount,
                    @RequestHeader String failPos);
}
