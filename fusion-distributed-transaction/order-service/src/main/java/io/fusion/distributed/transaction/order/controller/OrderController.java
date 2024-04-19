package io.fusion.distributed.transaction.order.controller;

import io.fusion.distributed.transaction.api.OrderServiceApi;
import io.fusion.distributed.transaction.common.CommonConst;
import io.fusion.distributed.transaction.order.rpc.AccountServiceRpcClient;
import io.fusion.distributed.transaction.order.service.OrderService;
import io.fusion.framework.core.enums.ApiStatusCode;
import io.fusion.framework.core.exception.BizException;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * @author enhao
 */
@Slf4j
@RestController
public class OrderController implements OrderServiceApi {

    private final OrderService orderService;

    private final AccountServiceRpcClient accountServiceRpcClient;

    private final Random random = new Random();

    public OrderController(OrderService orderService, AccountServiceRpcClient accountServiceRpcClient) {
        this.orderService = orderService;
        this.accountServiceRpcClient = accountServiceRpcClient;
    }

    @Override
    public String order(String userId, String commodityCode, int orderCount, @RequestHeader String failPos) {
        log.info("Order Service Begin ... xid: " + RootContext.getXID());

        // 计算订单价格
        int orderMoney = calculate(commodityCode, orderCount);

        // 账户金额扣减
        String account = accountServiceRpcClient.account(userId, orderMoney, failPos);
        if (!CommonConst.SUCCESS.equals(account)) {
            throw new BizException(ApiStatusCode.SYSTEM_ERROR, "账号api调用失败");
        }

        // 订单入库
        boolean success = orderService.saveOrder(userId, commodityCode, orderCount, orderMoney);
        if (!success) {
            throw new BizException(ApiStatusCode.SYSTEM_ERROR, "订单入库失败");
        }

        if ("order".equals(failPos)) {
            throw new RuntimeException("Order Service Exception");
        }

        return CommonConst.SUCCESS;
    }

    private int calculate(String commodityCode, int orderCount) {
        return 2 * orderCount;
    }
}
