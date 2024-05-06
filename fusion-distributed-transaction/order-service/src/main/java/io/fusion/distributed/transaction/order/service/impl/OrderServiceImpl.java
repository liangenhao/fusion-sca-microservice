package io.fusion.distributed.transaction.order.service.impl;

import io.fusion.distributed.transaction.entity.OrderTcc;
import io.fusion.distributed.transaction.order.mapper.OrderMapper;
import io.fusion.distributed.transaction.order.service.OrderService;
import io.fusion.framework.core.enums.ApiStatusCode;
import io.fusion.framework.core.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author enhao
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Override
    public boolean saveOrder(String userId, String commodityCode, int orderCount, int orderMoney) {
        int count = orderMapper.insertOrder(userId, commodityCode, orderCount, orderMoney);
        return count > 0;
    }

    @Override
    public Integer preOrder(String userId, String commodityCode, int orderCount, int orderMoney) {
        OrderTcc orderTcc = OrderTcc.builder().userId(userId)
                .commodityCode(commodityCode)
                .count(orderCount)
                .money(orderMoney)
                .status("0").build();
        int cnt = orderMapper.insertPreOrder(orderTcc);
        if (cnt == 0) {
            throw new BizException(ApiStatusCode.SYSTEM_ERROR, "创建预订单失败");
        }
        return orderTcc.getId();
    }

    @Override
    public boolean commitPreOrder(Integer orderId) {
        return orderMapper.commitPreOrder(orderId) > 0;
    }

    @Override
    public boolean rollbackPreOrder(Integer orderId) {
        return orderMapper.deletePreOrder(orderId) > 0;
    }
}
