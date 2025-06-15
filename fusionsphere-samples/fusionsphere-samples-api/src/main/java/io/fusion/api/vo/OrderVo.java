package io.fusion.api.vo;

import lombok.Data;

/**
 * @author enhao
 */
@Data
public class OrderVo {

    private Integer id;

    private String commodityCode;

    private Integer count;

    private Integer money;
}
