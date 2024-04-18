package io.fusion.consumer.client;

import com.alibaba.cloud.sentinel.feign.SentinelInvocationHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import io.fusion.api.vo.UserVo;
import io.fusion.framework.core.api.ApiResponse;
import io.fusion.framework.core.exception.FeignRpcBizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;

/**
 * 开启 feign.sentinel.enabled 且 设置了 fallback（包括 fallback 和 fallbackFactory）后，{@link SentinelInvocationHandler}
 * 会将所有 FeignClient 的所有异常都交给 fallback 处理，包括业务异常，这就会导致业务异常会的丢失。
 *
 * @author enhao
 * @see SentinelInvocationHandler
 */
@Slf4j
public class ProviderServiceEchoRpcClientFallbackFactory implements FallbackFactory<ProviderServiceEchoRpcClient> {
    @Override
    public ProviderServiceEchoRpcClient create(Throwable cause) {
        return new ProviderServiceEchoRpcClient() {
            @Override
            public String echo(String string) throws BlockException {
                log.error("fallback factory cause ", cause);
                // if (cause instanceof FeignRpcBizException) {
                //     throw (FeignRpcBizException) cause; // fallback 中是不是不应该抛出异常？
                // }
                return "echo fallback factory";
            }

            @Override
            public ApiResponse<String> echoApiResponse(String string) {
                return null;
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
        };
    }
}
