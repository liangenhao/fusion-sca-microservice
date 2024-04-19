package io.fusion.distributed.transaction.storage.controller;

import io.fusion.distributed.transaction.api.StorageServiceApi;
import io.fusion.distributed.transaction.common.CommonConst;
import io.fusion.distributed.transaction.storage.entity.Storage;
import io.fusion.framework.core.enums.ApiStatusCode;
import io.fusion.framework.core.exception.BizException;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author enhao
 */
@Slf4j
@RestController
public class StorageController implements StorageServiceApi {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StorageController(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String storage(String commodityCode, Integer count) {
        log.info("Storage Service Begin ... xid: " + RootContext.getXID());

        String updateSql = "update storage_tbl set count = count - :count where commodity_code = :commodityCode";
        BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(
                Storage.builder().commodityCode(commodityCode).count(count).build());
        int updateCount = jdbcTemplate.update(updateSql, paramSource);

        if (updateCount == 0) {
            throw new BizException(ApiStatusCode.SYSTEM_ERROR, "库存扣减失败");
        }

        return CommonConst.SUCCESS;
    }
}
