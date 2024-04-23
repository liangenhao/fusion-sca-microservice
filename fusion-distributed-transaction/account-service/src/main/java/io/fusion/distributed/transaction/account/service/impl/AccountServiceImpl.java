package io.fusion.distributed.transaction.account.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.fusion.distributed.transaction.entity.Account;
import io.fusion.distributed.transaction.account.mapper.AccountMapper;
import io.fusion.distributed.transaction.account.service.AccountService;
import org.springframework.stereotype.Service;

/**
 * @author enhao
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {
}
