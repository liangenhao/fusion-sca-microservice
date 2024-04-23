package io.fusion.distributed.transaction.api;

import io.fusion.distributed.transaction.entity.Account;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author enhao
 */
// @RequestMapping("/distributed-transaction")
public interface AccountServiceApi {

    @PostMapping(value = "/distributed-transaction/account", produces = "application/json")
    String account(@RequestParam String userId, @RequestParam int money, @RequestHeader String failPos);

    @PostMapping(value = "/distributed-transaction/queryUserAccount", produces = "application/json")
    Account queryUserAccount(@RequestParam String userId, @RequestParam Boolean forUpdate);
}
