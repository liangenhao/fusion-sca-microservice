package io.fusion.distributed.transaction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author enhao
 */
@Data
@TableName("account_tcc_tbl")
public class AccountTcc {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("user_id")
    private String userId;

    private Integer money;

    /**
     * 预扣金额
     */
    @TableField("pre_money")
    private Integer preMoney;
}
