package io.fusion.distributed.transaction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author enhao
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("account_tbl")
public class Account extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("user_id")
    private String userId;

    @TableField("money")
    private Integer money;

    @TableField("extend")
    private String extend;

}
