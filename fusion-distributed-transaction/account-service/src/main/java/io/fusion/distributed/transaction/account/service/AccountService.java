package io.fusion.distributed.transaction.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.fusion.distributed.transaction.entity.Account;

/**
 * @author enhao
 */
public interface AccountService extends IService<Account> {

    void delete(Integer id);

    void update(Account account);

    void transaction();

    Account findByCondition(String userId);
}
