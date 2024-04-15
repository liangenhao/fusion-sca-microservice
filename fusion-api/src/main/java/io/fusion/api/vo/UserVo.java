package io.fusion.api.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author enhao
 */
@Data
public class UserVo implements Serializable {

    private static final long serialVersionUID = 1689508717572123782L;

    private Integer id;

    private String username;

    private Integer age;

    private AccountVo account;

    private List<OrderVo> orders;
}
