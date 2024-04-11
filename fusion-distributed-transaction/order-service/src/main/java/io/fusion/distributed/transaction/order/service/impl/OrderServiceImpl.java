package io.fusion.distributed.transaction.order.service.impl;

import io.fusion.distributed.transaction.order.mapper.OrderMapper;
import io.fusion.distributed.transaction.order.service.OrderService;
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
}
