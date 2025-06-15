package io.fusion.distributed.transaction.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @author enhao
 */
@Data
@Builder
public class StorageTcc {

    private Integer id;

    private String commodityCode;

    private Integer count;

    /**
     * 预扣库存
     */
    private Integer preCount;
}
