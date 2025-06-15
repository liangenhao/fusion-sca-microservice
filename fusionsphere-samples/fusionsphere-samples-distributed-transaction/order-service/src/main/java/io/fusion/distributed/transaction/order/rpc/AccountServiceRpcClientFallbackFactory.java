package io.fusion.distributed.transaction.order.rpc;

import io.fusion.distributed.transaction.entity.Account;
import io.seata.core.context.RootContext;
import io.seata.tm.api.GlobalTransactionContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author enhao
 */
@Slf4j
@Component
public class AccountServiceRpcClientFallbackFactory implements FallbackFactory<AccountServiceRpcClient> {
    @Override
    public AccountServiceRpcClient create(Throwable cause) {
        return new AccountServiceRpcClient() {
            @SneakyThrows
            @Override
            public String account(String userId, int money, String failPos) {
                log.error("[account fallbackFactory]", cause);
                GlobalTransactionContext.reload(RootContext.getXID()).rollback();
                return "account fallback factory";
            }

            @Override
            public Account queryUserAccount(String userId, Boolean forUpdate) {
                return null;
            }

            @Override
            public String deductAccountMoney(String userId, int money, String failPos) {
                return null;
            }
        };
    }
}
