package io.fusion.api.interfaces;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import io.fusion.api.vo.UserVo;
import io.fusionsphere.spring.cloud.web.core.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author enhao
 */
public interface ProviderServiceEchoApi {

    @GetMapping(value = "/echo/{string}")
    String echo(@PathVariable String string) throws BlockException;

    @GetMapping(value = "/echoApiResponse/{string}")
    ApiResponse<String> echoApiResponse(@PathVariable String string);

    @PostMapping(value = "/echoUserVo")
    UserVo echoUserVo(@RequestBody UserVo userVo);

    @PostMapping(value = "/echoUserVoApiResponse")
    ApiResponse<UserVo> echoUserVoApiResponse(@RequestBody UserVo userVo);

    @PostMapping(value = "/echoUserVoList")
    List<UserVo> echoUserVoList(@RequestBody List<UserVo> userVoList);

    @PostMapping(value = "/echoUserVoListApiResponse")
    ApiResponse<List<UserVo>> echoUserVoListApiResponse(@RequestBody List<UserVo> userVoList);

    @PostMapping(value = "/echoVoid")
    void echoVoid();

    @PostMapping(value = "/echoException")
    void echoException();

    @PostMapping(value = "/echoVoidException")
    Void echoVoidException();

    @PostMapping(value = "/echoUserVoException")
    UserVo echoUserVoException();

    @PostMapping(value = "/echoVoidApiResponseException")
    ApiResponse<Void> echoVoidApiResponseException();
}
