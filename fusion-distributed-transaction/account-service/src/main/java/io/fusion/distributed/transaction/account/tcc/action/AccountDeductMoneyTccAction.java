package io.fusion.distributed.transaction.account.tcc.action;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

/**
 * @author enhao
 */
@LocalTCC
public interface AccountDeductMoneyTccAction {

    /**
     * 预扣款
     *
     * @param actionContext {@link BusinessActionContext}
     * @param userId        用户ID
     * @param money         扣款金额
     * @return {@link Boolean}
     */
    @TwoPhaseBusinessAction(name = "prepareAccountDeductMoney", commitMethod = "commitDeductMoney", rollbackMethod = "rollbackDeductMoney", useTCCFence = true)
    boolean prepareDeductMoney(BusinessActionContext actionContext,
                               @BusinessActionContextParameter(value = "userId") String userId,
                               @BusinessActionContextParameter(value = "money") Integer money);

    /**
     * 提交扣款
     *
     * @param actionContext {@link BusinessActionContext}
     * @return {@link Boolean}
     */
    boolean commitDeductMoney(BusinessActionContext actionContext);

    /**
     * 回滚扣款
     *
     * @param actionContext {@link BusinessActionContext}
     * @return {@link Boolean}
     */
    boolean rollbackDeductMoney(BusinessActionContext actionContext);
}
