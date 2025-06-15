package io.fusion.distributed.transaction.account.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.fusion.common.conditionquery.model.QueryParam;
import io.fusion.common.conditionquery.model.QueryWrapperBuilder;
import io.fusion.common.utils.ExcelTemplateUtils;
import io.fusion.distributed.transaction.account.service.AccountService;
import io.fusion.distributed.transaction.account.tcc.action.AccountDeductMoneyTccAction;
import io.fusion.distributed.transaction.api.AccountServiceApi;
import io.fusion.distributed.transaction.common.CommonConst;
import io.fusion.distributed.transaction.entity.Account;
import io.fusionsphere.spring.cloud.web.core.ApiStatusCode;
import io.fusionsphere.spring.cloud.web.core.exception.BizException;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author enhao
 */
@Slf4j
@RestController
public class AccountController implements AccountServiceApi {

    private final AccountService accountService;

    private final AccountDeductMoneyTccAction accountDeductMoneyTccAction;

    public AccountController(AccountService accountService, AccountDeductMoneyTccAction accountDeductMoneyTccAction) {
        this.accountService = accountService;
        this.accountDeductMoneyTccAction = accountDeductMoneyTccAction;
    }

    @Override
    // @GlobalTransactional(timeoutMills = 300000)
    public String account(String userId, int money, @RequestHeader String failPos) {
        log.info("Account Service ... xid: " + RootContext.getXID());

        // boolean success = accountService.update(Wrappers.<Account>update()
        //         .setSql("money = money - {0}", money)
        //         .eq("user_id", userId));
        boolean success = accountService.update(
                Wrappers.<Account>update().lambda()
                        .setSql("money = money - {0}", money)
                        .eq(Account::getUserId, userId)
        );

        if (!success) {
            throw new BizException(ApiStatusCode.SYSTEM_ERROR, "账户金额扣减失败");
        }
        if ("account".equals(failPos)) {
            throw new BizException(ApiStatusCode.SYSTEM_ERROR, "account Service Exception");
        }

        return CommonConst.SUCCESS;
    }

    @Override
    // @GlobalTransactional(timeoutMills = 300000)
    @GlobalLock
    public Account queryUserAccount(String userId, Boolean forUpdate) {
        return accountService.lambdaQuery()
                .eq(Account::getUserId, userId)
                .last(forUpdate ? "for update" : "")
                .one();
    }

    @Override
    // @GlobalTransactional(timeoutMills = 300000)
    public String deductAccountMoney(String userId, int money, @RequestHeader String failPos) {
        log.info("Account Service ... xid: " + RootContext.getXID());
        accountDeductMoneyTccAction.prepareDeductMoney(null, userId, money);

        if ("account".equals(failPos)) {
            throw new BizException(ApiStatusCode.SYSTEM_ERROR, "account Service Exception");
        }

        return CommonConst.SUCCESS;
    }

    @GetMapping("/queryByPage")
    public List<Account> queryByPage(@RequestBody QueryParam queryParam) {
        QueryWrapper<Account> queryWrapper = QueryWrapperBuilder.build(queryParam, Account.class);

        return accountService.list(queryWrapper);
    }

    @GetMapping("/deleteById")
    public void deleteById(@RequestParam Integer id) {
        accountService.removeById(id);

        LambdaQueryWrapper<Account> updateWrapper = Wrappers.<Account>lambdaQuery()
                .eq(Account::getId, id);
        accountService.remove(updateWrapper);

        accountService.delete(id);

        LambdaUpdateWrapper<Account> wrapper = Wrappers.<Account>lambdaUpdate()
                .eq(Account::getId, id)
                .set(Account::getDeletedBy, 1L)
                .set(Account::getDeletedName, "admin")
                .set(Account::getDeletedAt, LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
        accountService.update(wrapper);
    }

    @GetMapping("/update")
    public void update() {
        Account account = new Account();
        account.setId(2);
        account.setMoney(101);

        // LambdaUpdateWrapper<Account> updateWrapper = Wrappers.<Account>lambdaUpdate()
        //         .eq(Account::getId, 1)
        //         .set(Account::getUserId, "U10011");
        // accountService.update(account, updateWrapper);
        //
        // accountService.update(updateWrapper);

        accountService.update(account);
    }

    @GetMapping("/transaction")
    public void transaction() {
        // 测试事务回滚
        try {
            accountService.transaction();
        } catch (Exception ex) {
            log.error("catch exception");
        }
    }

    @GetMapping("/findByCondition")
    public Account findByCondition(@RequestParam String userId) {
        return accountService.findByCondition(userId);
    }

    @PostMapping("/download")
    public void download(HttpServletResponse response) throws Exception {
        String encodedFilename = URLEncoder.encode("item.xlsx", "UTF-8");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + encodedFilename);
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        List<String> headers = new ArrayList<>();
        headers.add("a");
        headers.add("b");

        Map<String, List<String>> dropDownMap = new HashMap<>();
        ExcelTemplateUtils.generateTemplate(response.getOutputStream(), "Sheet1", headers, dropDownMap);
    }
}
