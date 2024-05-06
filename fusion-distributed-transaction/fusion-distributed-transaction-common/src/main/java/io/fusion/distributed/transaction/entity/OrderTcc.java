package io.fusion.distributed.transaction.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author enhao
 */
@Data
@Builder
public class OrderTcc {

    private Integer id;

    private String userId;

    private String commodityCode;

    private Integer count;

    private Integer money;

    /**
     * 订单状态 0: 预下单；1：下单成功；
     */
    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
