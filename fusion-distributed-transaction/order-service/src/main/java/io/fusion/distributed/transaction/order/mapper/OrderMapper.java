package io.fusion.distributed.transaction.order.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author enhao
 */
// @Mapper
public interface OrderMapper {

    @Insert("insert into order_tbl(user_id, commodity_code, count, money) values(#{userId}, #{commodityCode}, #{orderCount}, #{orderMoney})")
    int insertOrder(String userId, String commodityCode, int orderCount, int orderMoney);
}
