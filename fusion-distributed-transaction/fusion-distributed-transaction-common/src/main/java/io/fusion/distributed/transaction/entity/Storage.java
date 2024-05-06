package io.fusion.distributed.transaction.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @author enhao
 */
@Data
@Builder
public class Storage {

    private Integer id;

    private String commodityCode;

    private Integer count;
}
