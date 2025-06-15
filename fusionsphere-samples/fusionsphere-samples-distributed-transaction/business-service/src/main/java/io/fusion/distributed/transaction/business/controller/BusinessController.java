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
 * <p>
 * feign 的统一返回和异常处理、降级熔断 情况下，分布式事务支持问题
 * 1. 统一返回和异常处理中包括了对业务异常处理。
 * 2. sentinel-feign 开启 fallback 时会造成所有异常都走到 fallback 方法中，会导致业务异常信息无法抛出，且分布式事务无法回滚。
 * 2.1 解决分布式事务无法回滚问题可在fallback方法里调用 {@code GlobalTransactionContext.reload(RootContext.getXID()).rollback();}
 * 2.2 解决所有异常都会进入fallback，导致业务异常信息无法抛出问题。要么不开启 fallback，
 * 方式一：重写 SentinelInvocationHandler 逻辑，实现类似 {@code HystrixBadRequestException} 异常的实现，或实现 exceptionsToIgnore 的逻辑。
 * 方式二：使用 {@link com.alibaba.csp.sentinel.annotation.SentinelResource} 替代 sentinel-feign 的适配
 *
 * @author enhao
 */
@Slf4j
@RestController
public class BusinessController {

    private final RestTemplate restTemplate;

    private final OrderServiceRpcClient orderServiceRpcClient;

    private final StorageServiceRpcClient storageServiceRpcClient;

    public BusinessController(RestTemplate restTemplate, OrderServiceRpcClient orderServiceRpcClient,
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

    @GlobalTransactional(timeoutMills = 300000)
    @GetMapping(value = "/seata/tcc")
    public String tcc(@RequestHeader String failPos) {
        String commodityCode = "C0001";
        int orderCount = 2;
        String userId = "U1001";

        // 扣减库存
        storageServiceRpcClient.deductStorageCount(commodityCode, orderCount);

        // 下单（包括：账号金额扣减 + 下单）
        orderServiceRpcClient.preOrder(userId, commodityCode, orderCount, failPos);

        return CommonConst.SUCCESS;
    }


}
