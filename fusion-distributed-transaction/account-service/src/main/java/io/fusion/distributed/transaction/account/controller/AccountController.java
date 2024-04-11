package io.fusion.distributed.transaction.account.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.fusion.distributed.transaction.account.entity.Account;
import io.fusion.distributed.transaction.account.service.AccountService;
import io.fusion.distributed.transaction.api.AccountServiceApi;
import io.fusion.distributed.transaction.common.CommonConst;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * @author enhao
 */
@Slf4j
@RestController
public class AccountController implements AccountServiceApi {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public String account(String userId, int money, @RequestHeader String failPos) {
        log.info("Account Service ... xid: " + RootContext.getXID());

        if ("account".equals(failPos)) {
            throw new RuntimeException("account Service Exception");
        }

        // boolean success = accountService.update(Wrappers.<Account>update()
        //         .setSql("money = money - {0}", money)
        //         .eq("user_id", userId));
        boolean success = accountService.update(
                Wrappers.<Account>update().lambda()
                        .setSql("money = money - {0}", money)
                        .eq(Account::getUserId, userId)
        );

        return success ? CommonConst.SUCCESS : CommonConst.FAIL;
    }
}
