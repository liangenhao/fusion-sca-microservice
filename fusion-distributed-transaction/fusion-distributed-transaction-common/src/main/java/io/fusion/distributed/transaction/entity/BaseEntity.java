package io.fusion.distributed.transaction.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

/**
 * @author enhao
 */
@Data
public class BaseEntity {


    @TableField("deleted_by")
    private Long deletedBy;

    @TableField("deleted_name")
    private String deletedName;

    @TableField("deleted_at")
    @TableLogic(value = "0", delval = "unix_timestamp()")
    private Long deletedAt;
}
