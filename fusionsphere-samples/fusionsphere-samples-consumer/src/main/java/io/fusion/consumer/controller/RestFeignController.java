package io.fusion.consumer.controller;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import io.fusion.api.vo.UserVo;
import io.fusion.consumer.client.ProviderServiceEchoRpcClient;
import io.fusionsphere.spring.cloud.web.core.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author enhao
 */
@RestController
public class RestFeignController {

    private final ProviderServiceEchoRpcClient echoRpcClient;

    public RestFeignController(ProviderServiceEchoRpcClient echoRpcClient) {
        this.echoRpcClient = echoRpcClient;
    }

    @GetMapping("/rest/feign/echo")
    public String echo() throws BlockException {
        return echoRpcClient.echo("Hello, World");
    }

    @GetMapping("/rest/feign/echoApiResponse")
    public ApiResponse<String> echoApiResponse() {
        return echoRpcClient.echoApiResponse("Hello, World");
    }

    @PostMapping("/rest/feign/echoUserVo")
    public UserVo echoUserVo(@RequestBody UserVo userVo) {
        return echoRpcClient.echoUserVo(userVo);
    }

    @PostMapping("/rest/feign/echoUserVoApiResponse")
    public ApiResponse<UserVo> echoUserVoApiResponse(@RequestBody UserVo userVo) {
        return echoRpcClient.echoUserVoApiResponse(userVo);
    }

    @PostMapping("/rest/feign/echoUserVoList")
    public List<UserVo> echoUserVoList(@RequestBody List<UserVo> userVoList) {
        return echoRpcClient.echoUserVoList(userVoList);
    }

    @PostMapping("/rest/feign/echoUserVoListApiResponse")
    public ApiResponse<List<UserVo>> echoUserVoListApiResponse(@RequestBody List<UserVo> userVoList) {
        return echoRpcClient.echoUserVoListApiResponse(userVoList);
    }

    @GetMapping("/rest/feign/echoVoid")
    public void echoVoid() {
        echoRpcClient.echoVoid();
    }

    @GetMapping("/rest/feign/echoException")
    public void echoException() {
        echoRpcClient.echoException();
    }

    @GetMapping("/rest/feign/echoVoidException")
    public Void echoVoidException() {
        echoRpcClient.echoVoidException();
        return null;
    }


    @GetMapping("/rest/feign/echoUserVoException")
    public UserVo echoUserVoException() {
        echoRpcClient.echoUserVoException();
        return null;
    }

    @GetMapping("/rest/feign/echoVoidApiResponseException")
    public ApiResponse<Void> echoVoidApiResponseException() {
        echoRpcClient.echoVoidApiResponseException();
        return ApiResponse.VOID;
    }

}
