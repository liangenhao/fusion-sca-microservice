package io.fusion.distributed.transaction.account.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author enhao
 */
@Data
@TableName("account_tbl")
public class Account {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("user_id")
    private String userId;

    private Integer money;
}
