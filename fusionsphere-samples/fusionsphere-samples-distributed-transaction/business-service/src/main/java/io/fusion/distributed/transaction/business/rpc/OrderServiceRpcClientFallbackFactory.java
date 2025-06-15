package io.fusion.distributed.transaction.business.rpc;

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
public class OrderServiceRpcClientFallbackFactory implements FallbackFactory<OrderServiceRpcClient> {
    @Override
    public OrderServiceRpcClient create(Throwable cause) {
        return new OrderServiceRpcClient() {
            @SneakyThrows
            @Override
            public String order(String userId, String commodityCode, int orderCount, String failPos) {
                log.error("[order fallbackFactory]", cause);
                GlobalTransactionContext.reload(RootContext.getXID()).rollback();
                return "order fallback factory";
            }

            @Override
            public String preOrder(String userId, String commodityCode, int orderCount, String failPos) {
                return null;
            }
        };
    }
}
