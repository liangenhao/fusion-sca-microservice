package io.fusion.distributed.transaction.order.service;

/**
 * @author enhao
 */
public interface OrderService {

    boolean saveOrder(String userId, String commodityCode, int orderCount, int orderMoney);
}
