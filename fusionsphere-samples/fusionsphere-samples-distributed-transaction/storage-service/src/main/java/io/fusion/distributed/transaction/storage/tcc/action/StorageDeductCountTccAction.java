package io.fusion.distributed.transaction.storage.tcc.action;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

/**
 * @author enhao
 */
@LocalTCC
public interface StorageDeductCountTccAction {

    @TwoPhaseBusinessAction(name = "prepareDeductSStorage", commitMethod = "commitDeductStorage", rollbackMethod = "rollbackDeductStorage", useTCCFence = true)
    boolean prepareDeductSStorage(BusinessActionContext actionContext,
                                  @BusinessActionContextParameter(value = "commodityCode") String commodityCode,
                                  @BusinessActionContextParameter(value = "count") Integer count);

    boolean commitDeductStorage(BusinessActionContext actionContext);

    boolean rollbackDeductStorage(BusinessActionContext actionContext);
}
