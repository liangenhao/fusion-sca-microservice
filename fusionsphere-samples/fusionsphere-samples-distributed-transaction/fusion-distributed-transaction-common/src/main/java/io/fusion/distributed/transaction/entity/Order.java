package io.fusion.distributed.transaction.entity;

import lombok.Data;

/**
 * @author enhao
 */
@Data
public class Order {

    private Integer id;

    private String userId;

    private String commodityCode;

    private Integer count;

    private Integer money;
}
