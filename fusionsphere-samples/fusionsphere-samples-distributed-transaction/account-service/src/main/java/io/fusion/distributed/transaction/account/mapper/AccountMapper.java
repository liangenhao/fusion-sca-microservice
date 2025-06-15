package io.fusion.distributed.transaction.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.fusion.distributed.transaction.entity.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author enhao
 */
@Mapper
public interface AccountMapper extends BaseMapper<Account> {

    Account findByCondition(@Param("userId") String userId);
}
