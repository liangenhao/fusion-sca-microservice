package io.fusion.distributed.transaction.order.tcc.action.impl;

import io.fusion.distributed.transaction.order.rpc.AccountServiceRpcClient;
import io.fusion.distributed.transaction.order.service.OrderService;
import io.fusion.distributed.transaction.order.tcc.action.PreOrderTccAction;
import io.fusion.framework.core.enums.ApiStatusCode;
import io.fusion.framework.core.exception.BizException;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextUtil;
import org.springframework.stereotype.Service;

/**
 * @author enhao
 */
@Service
public class PreOrderTccActionImpl implements PreOrderTccAction {

    private final OrderService orderService;

    private final AccountServiceRpcClient accountServiceRpcClient;

    public PreOrderTccActionImpl(OrderService orderService, AccountServiceRpcClient accountServiceRpcClient) {
        this.orderService = orderService;
        this.accountServiceRpcClient = accountServiceRpcClient;
    }

    @Override
    public boolean prepareOrder(BusinessActionContext actionContext, String userId, String commodityCode, Integer count,
                                Integer orderMoney, String failPos) {
        Integer orderId = orderService.preOrder(userId, commodityCode, count, orderMoney);
        BusinessActionContextUtil.addContext("orderId", orderId);

        if ("order".equals(failPos)) {
            throw new BizException(ApiStatusCode.SYSTEM_ERROR, "Order Service Exception");
        }
        return true;
    }

    @Override
    public boolean commitOrder(BusinessActionContext actionContext) {
        Integer orderId = actionContext.getActionContext("orderId", Integer.class);
        return orderService.commitPreOrder(orderId);
    }

    @Override
    public boolean rollbackOrder(BusinessActionContext actionContext) {
        Integer orderId = actionContext.getActionContext("orderId", Integer.class);
        return orderService.rollbackPreOrder(orderId);
    }
}
