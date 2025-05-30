package io.fusion.distributed.transaction.account.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.fusion.distributed.transaction.entity.Account;
import io.fusion.distributed.transaction.account.mapper.AccountMapper;
import io.fusion.distributed.transaction.account.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author enhao
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    public void delete(Integer id) {
        this.lambdaUpdate()
                .eq(Account::getId, id)
                .set(Account::getDeletedBy, 1L)
                .set(Account::getDeletedName, "admin")
                .set(Account::getDeletedAt, LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond())
                .update();
    }

    @Override
    public void update(Account account) {
        this.lambdaUpdate()
                .eq(Account::getId, 1L)
                .set(Account::getUserId, "U10011")
                .setSql("extend = JSON_SET(extend, '$.needSync', 1)")
                .update(account);
    }

    @Override
    @Transactional
    public void transaction() {
        this.lambdaUpdate()
                .eq(Account::getId, 1L)
                .setSql("money = money + {0}", 1)
               .update();
        throw new RuntimeException("123");
    }

    @Override
    public Account findByCondition(String userId) {
        return baseMapper.findByCondition(userId);
    }
}
