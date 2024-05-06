package io.fusion.distributed.transaction.order.tcc.action;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

/**
 * @author enhao
 */
@LocalTCC
public interface PreOrderTccAction {

    @TwoPhaseBusinessAction(name = "preOrder", commitMethod = "commitOrder", rollbackMethod = "rollbackOrder")
    boolean prepareOrder(BusinessActionContext actionContext,
                         @BusinessActionContextParameter("userId") String userId,
                         @BusinessActionContextParameter("commodityCode") String commodityCode,
                         @BusinessActionContextParameter("count") Integer count,
                         @BusinessActionContextParameter("orderMoney") Integer orderMoney,
                         String failPos);

    boolean commitOrder(BusinessActionContext actionContext);

    boolean rollbackOrder(BusinessActionContext actionContext);
}
