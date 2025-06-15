package io.fusion.consumer.client;

import io.fusion.api.vo.UserVo;
import io.fusionsphere.spring.cloud.web.core.ApiResponse;

import java.util.List;

public class ProviderServiceEchoRpcClientFallback implements ProviderServiceEchoRpcClient {

    @Override
    public String echo(String string) {
        return "echo FeignClientFallback";
    }

    @Override
    public ApiResponse<String> echoApiResponse(String string) {
        return ApiResponse.error("fallback");
    }

    @Override
    public UserVo echoUserVo(UserVo userVo) {
        return null;
    }

    @Override
    public ApiResponse<UserVo> echoUserVoApiResponse(UserVo userVo) {
        return null;
    }

    @Override
    public List<UserVo> echoUserVoList(List<UserVo> userVoList) {
        return null;
    }

    @Override
    public ApiResponse<List<UserVo>> echoUserVoListApiResponse(List<UserVo> userVoList) {
        return null;
    }

    @Override
    public void echoVoid() {

    }

    @Override
    public void echoException() {

    }

    @Override
    public Void echoVoidException() {
        return null;
    }

    @Override
    public UserVo echoUserVoException() {
        return null;
    }

    @Override
    public ApiResponse<Void> echoVoidApiResponseException() {
        return null;
    }
}
