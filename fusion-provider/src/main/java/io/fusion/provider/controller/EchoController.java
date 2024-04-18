package io.fusion.provider.controller;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import io.fusion.api.interfaces.ProviderServiceEchoApi;
import io.fusion.api.vo.UserVo;
import io.fusion.framework.core.api.ApiResponse;
import io.fusion.framework.core.enums.ApiStatusCode;
import io.fusion.framework.core.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.InvalidParameterException;
import java.util.List;

@Slf4j
@RestController
public class EchoController implements ProviderServiceEchoApi {

    public String echo(@PathVariable String string) throws BlockException {
        log.error("echo execute {}", string);
        if ("error".equals(string)) {
            throw new BizException(ApiStatusCode.UNKNOWN, "echo pathVariable is error");
        }
        return "Hello Nacos Discovery " + string;
    }

    @Override
    public ApiResponse<String> echoApiResponse(@PathVariable String string) {
        return ApiResponse.ok("echoApiResponse" + string);
    }

    @Override
    public UserVo echoUserVo(@RequestBody UserVo userVo) {
        userVo.setUsername("echoUserVo " + userVo.getUsername());
        return userVo;
    }

    @Override
    public ApiResponse<UserVo> echoUserVoApiResponse(@RequestBody UserVo userVo) {
        userVo.setUsername("echoUserVo " + userVo.getUsername());
        return ApiResponse.ok(userVo);
    }

    @Override
    public List<UserVo> echoUserVoList(@RequestBody List<UserVo> userVoList) {
        return userVoList;
    }

    @Override
    public ApiResponse<List<UserVo>> echoUserVoListApiResponse(@RequestBody List<UserVo> userVoList) {
        return ApiResponse.ok(userVoList);
    }

    @Override
    public void echoVoid() {
        // do nothing
    }

    @Override
    public void echoException() {
        throw new InvalidParameterException("echoException");
    }

    @Override
    public Void echoVoidException() {
        throw new InvalidParameterException("echoVoidException");
    }

    @Override
    public UserVo echoUserVoException() {
        throw new InvalidParameterException("echoUserVoException");
    }

    @Override
    public ApiResponse<Void> echoVoidApiResponseException() {
        throw new InvalidParameterException("echoVoidApiResponseException");
        // return ApiResponse.VOID;
    }
}
