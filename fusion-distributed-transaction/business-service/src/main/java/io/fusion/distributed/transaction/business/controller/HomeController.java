package io.fusion.distributed.transaction.business.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import io.fusion.distributed.transaction.business.rpc.OrderServiceRpcClient;
import io.fusion.distributed.transaction.business.rpc.StorageServiceRpcClient;
import io.fusion.distributed.transaction.common.CommonConst;
import io.fusion.framework.core.api.ApiResponse;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * storage-service -> order-service ( account-service + order )
 * <p>
 * TODO feign 的统一返回和异常处理、降级熔断 情况下，分布式事务支持问题
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
        String commodityCode = "C0001";
        int orderCount = 2;
        String userId = "U1001";

        // 扣减库存
        restTemplate.postForObject("http://127.0.0.1:10002/distributed-transaction/storage/" + commodityCode + "/" + orderCount, null, String.class);

        // 下单（包括：账号金额扣减 + 下单）
        orderServiceRpcClient.order(userId, commodityCode, orderCount, failPos);


        return CommonConst.SUCCESS;
    }


}
