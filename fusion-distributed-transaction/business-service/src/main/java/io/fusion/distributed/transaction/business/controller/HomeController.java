package io.fusion.distributed.transaction.business.controller;

import io.fusion.distributed.transaction.business.rpc.OrderServiceRpcClient;
import io.fusion.distributed.transaction.business.rpc.StorageServiceRpcClient;
import io.fusion.distributed.transaction.common.CommonConst;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * storage-service -> order-service ( account-service + order )
 *
 * @author enhao
 */
@Slf4j
@RestController
public class HomeController {

    private final RestTemplate restTemplate;

    private final OrderServiceRpcClient orderServiceRpcClient;

    private final StorageServiceRpcClient storageServiceRpcClient;

    public HomeController(RestTemplate restTemplate, OrderServiceRpcClient orderServiceRpcClient,
                          StorageServiceRpcClient storageServiceRpcClient) {
        this.restTemplate = restTemplate;
        this.orderServiceRpcClient = orderServiceRpcClient;
        this.storageServiceRpcClient = storageServiceRpcClient;
    }

    @GlobalTransactional(timeoutMills = 300000)
    @GetMapping(value = "/seata/rest", produces = "application/json")
    public String rest(@RequestHeader String failPos) {
        // 扣减库存
        String commodityCode = "C0001";
        int orderCount = 2;
        String userId = "U1001";

        String storageResult = restTemplate.postForObject("http://127.0.0.1:10002/distributed-transaction/storage/" + commodityCode + "/" + orderCount, null, String.class);
        if (!CommonConst.SUCCESS.equals(storageResult)) {
            return "deduct storage error";
        }

        // 下单（包括：账号金额扣减 + 下单）
        String orderResult = orderServiceRpcClient.order(userId, commodityCode, orderCount, failPos);
        if (!CommonConst.SUCCESS.equals(orderResult)) {
            return "order error";
        }

        return CommonConst.SUCCESS;
    }


}
