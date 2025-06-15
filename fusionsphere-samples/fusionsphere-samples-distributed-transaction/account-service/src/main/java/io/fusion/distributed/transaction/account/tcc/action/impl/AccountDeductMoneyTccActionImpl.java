package io.fusion.distributed.transaction.account.tcc.action.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.fusion.distributed.transaction.account.service.AccountTccService;
import io.fusion.distributed.transaction.account.tcc.action.AccountDeductMoneyTccAction;
import io.fusion.distributed.transaction.entity.AccountTcc;
import io.fusionsphere.spring.cloud.web.core.ApiStatusCode;
import io.fusionsphere.spring.cloud.web.core.exception.BizException;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author enhao
 */
@Slf4j
@Service
public class AccountDeductMoneyTccActionImpl implements AccountDeductMoneyTccAction {

    private final AccountTccService accountTccService;

    public AccountDeductMoneyTccActionImpl(AccountTccService accountTccService) {
        this.accountTccService = accountTccService;
    }

    @Override
    public boolean prepareDeductMoney(BusinessActionContext actionContext, String userId, Integer money) {
        log.error("prepareDeductMoney: userId:{} money:{}", userId, money);
        boolean success = accountTccService.update(
                Wrappers.<AccountTcc>update().lambda()
                        .setSql("money = money - {0}", money)
                        .setSql("pre_money = pre_money + {0}", money)
                        .eq(AccountTcc::getUserId, userId)
                        .ge(AccountTcc::getMoney, money)
        );

        if (!success) {
            throw new BizException(ApiStatusCode.SYSTEM_ERROR, "账号金额预扣减失败");
        }

        return true;
    }

    @Override
    public boolean commitDeductMoney(BusinessActionContext actionContext) {
        String userId = actionContext.getActionContext("userId", String.class);
        Integer money = actionContext.getActionContext("money", Integer.class);
        log.error("commitDeductMoney: userId:{} money:{}", userId, money);
        return accountTccService.update(
                Wrappers.<AccountTcc>update().lambda()
                        .setSql("pre_money = pre_money - {0}", money)
                        .eq(AccountTcc::getUserId, userId)
                        .ge(AccountTcc::getPreMoney, money)
        );
    }

    @Override
    public boolean rollbackDeductMoney(BusinessActionContext actionContext) {
        String userId = actionContext.getActionContext("userId", String.class);
        Integer money = actionContext.getActionContext("money", Integer.class);
        log.error("rollbackDeductMoney: userId:{} money:{}", userId, money);
        return accountTccService.update(
                Wrappers.<AccountTcc>update().lambda()
                        .setSql("money = money + {0}", money)
                        .setSql("pre_money = pre_money - {0}", money)
                        .eq(AccountTcc::getUserId, userId)
                        .ge(AccountTcc::getPreMoney, money)
        );
    }
}
