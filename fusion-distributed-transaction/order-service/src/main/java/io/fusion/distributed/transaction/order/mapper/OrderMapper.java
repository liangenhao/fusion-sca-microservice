package io.fusion.distributed.transaction.order.mapper;

import io.fusion.distributed.transaction.entity.OrderTcc;
import org.apache.ibatis.annotations.*;

/**
 * @author enhao
 */
// @Mapper
public interface OrderMapper {

    @Insert("insert into order_tbl(user_id, commodity_code, count, money) values(#{userId}, #{commodityCode}, #{orderCount}, #{orderMoney})")
    int insertOrder(String userId, String commodityCode, int orderCount, int orderMoney);

    @Insert("insert into order_tcc_tbl(user_id, commodity_code, count, money, `status`, create_time) " +
            "values(#{userId}, #{commodityCode}, #{count}, #{money}, #{status}, now())")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    int insertPreOrder(OrderTcc orderTcc);

    @Update("update order_tcc_tbl set `status` = 1, update_time = now() where id = #{orderId} and `status` = 0")
    int commitPreOrder(Integer orderId);

    @Delete("delete from order_tcc_tbl where id = #{orderId} and `status` = 0")
    int deletePreOrder(Integer orderId);
}
