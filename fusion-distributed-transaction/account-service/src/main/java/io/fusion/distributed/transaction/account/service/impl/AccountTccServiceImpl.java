package io.fusion.distributed.transaction.account.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.fusion.distributed.transaction.account.mapper.AccountTccMapper;
import io.fusion.distributed.transaction.account.service.AccountTccService;
import io.fusion.distributed.transaction.entity.AccountTcc;
import org.springframework.stereotype.Service;

/**
 * @author enhao
 */
@Service
public class AccountTccServiceImpl extends ServiceImpl<AccountTccMapper, AccountTcc> implements AccountTccService {
}
