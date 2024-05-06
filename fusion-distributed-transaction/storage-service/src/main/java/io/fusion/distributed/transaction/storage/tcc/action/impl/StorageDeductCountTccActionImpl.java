package io.fusion.distributed.transaction.storage.tcc.action.impl;

import io.fusion.distributed.transaction.storage.tcc.action.StorageDeductCountTccAction;
import io.fusion.framework.core.enums.ApiStatusCode;
import io.fusion.framework.core.exception.BizException;
import io.seata.rm.tcc.api.BusinessActionContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author enhao
 */
@Service
public class StorageDeductCountTccActionImpl implements StorageDeductCountTccAction {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StorageDeductCountTccActionImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean prepareDeductSStorage(BusinessActionContext actionContext, String commodityCode, Integer count) {
        String updateSql = "update storage_tcc_tbl set count = count - :count, pre_count = pre_count + :count " +
                "where commodity_code = :commodityCode and count >= :count";

        Map<String, Object> condition = new HashMap<>();
        condition.put("commodityCode", commodityCode);
        condition.put("count", count);
        int updateCount = jdbcTemplate.update(updateSql, condition);

        if (updateCount == 0) {
            throw new BizException(ApiStatusCode.SYSTEM_ERROR, "库存扣减失败");
        }

        return true;
    }

    @Override
    public boolean commitDeductStorage(BusinessActionContext actionContext) {
        String commodityCode = actionContext.getActionContext("commodityCode", String.class);
        Integer count = actionContext.getActionContext("count", Integer.class);

        String updateSql = "update storage_tcc_tbl set pre_count = pre_count - :count " +
                "where commodity_code = :commodityCode and pre_count >= :count";

        Map<String, Object> condition = new HashMap<>();
        condition.put("commodityCode", commodityCode);
        condition.put("count", count);
        int updateCount = jdbcTemplate.update(updateSql, condition);

        return updateCount > 0;
    }

    @Override
    public boolean rollbackDeductStorage(BusinessActionContext actionContext) {
        String commodityCode = actionContext.getActionContext("commodityCode", String.class);
        Integer count = actionContext.getActionContext("count", Integer.class);

        String updateSql = "update storage_tcc_tbl set count = count + :count, pre_count = pre_count - :count " +
                "where commodity_code = :commodityCode and pre_count >= :count";

        Map<String, Object> condition = new HashMap<>();
        condition.put("commodityCode", commodityCode);
        condition.put("count", count);
        int updateCount = jdbcTemplate.update(updateSql, condition);

        return updateCount > 0;
    }
}
