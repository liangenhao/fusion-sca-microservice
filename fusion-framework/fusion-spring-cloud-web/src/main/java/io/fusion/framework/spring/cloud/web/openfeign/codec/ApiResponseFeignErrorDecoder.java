package io.fusion.framework.spring.cloud.web.openfeign.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import io.fusion.framework.core.api.ApiResponse;
import io.fusion.framework.core.exception.FeignRpcBizException;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.cloud.openfeign.loadbalancer.FeignBlockingLoadBalancerClient;

import java.nio.ByteBuffer;
import java.util.Optional;

/**
 * NOTE:
 * feign 整合 LoadBalancer 的负载均衡客户端 {@link FeignBlockingLoadBalancerClient}，当在注册中心找不到服务时，会返回 Http Status
 * 为 50x 的 Response，从而进入 {@link ErrorDecoder}.
 * 而 feign 整合 ribbon 的负载均衡客户端 {@code LoadBalancerFeignClient}，当在注册中心找不到服务时，会抛出 {@code ClientException}，
 * 不会进入 {@link ErrorDecoder}.
 * <p>
 * 因此在对 {@link ErrorDecoder} 的实现中，需要考虑响应结果不是 JSON 格式 或不是 {@link ApiResponse} 的场景。
 *
 * @author enhao
 * @see io.fusion.framework.spring.cloud.web.servlet.mvc.ApiResponseWrapReturnValueHandler
 * @see io.fusion.framework.spring.cloud.web.servlet.mvc.ApiResponseGlobalExceptionHandler
 */
public class ApiResponseFeignErrorDecoder extends ErrorDecoder.Default {

    private final ObjectMapper objectMapper;

    public ApiResponseFeignErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    @Override
    public Exception decode(String methodKey, Response response) {
        Exception exception = super.decode(methodKey, response);
        if (exception instanceof RetryableException) {
            return exception;
        } else if (exception instanceof FeignException) {
            return ((FeignException) exception).responseBody().<Exception>map(byteBuffer -> {
                String body = response.charset().decode(byteBuffer.asReadOnlyBuffer()).toString();
                try {
                    // 一般情况，webmvc 统一封装了返回值和异常为 ApiResponse 类型的 JSON 字符串
                    // 但还需要考虑，当返回值不是 JSON 格式 或不是 ApiResponse 类型时，需要特殊处理
                    // todo tips: 从某种程度上说，这里的实现方式并不通用，只支持返回值 JSON 字符串格式的数据，
                    //   或许可以通过 HttpMessageConverter 进行通用化转换
                    ApiResponse<?> apiResponse = objectMapper.readValue(body, ApiResponse.class);
                    if (isApiResponse(apiResponse)) {
                        return new FeignRpcBizException(response.status(), apiResponse, response.request());
                    }
                    return new FeignRpcBizException(response.status(), ApiResponse.error(body), response.request());
                } catch (JsonProcessingException e) {
                    // body 不是 json 格式
                    return new FeignRpcBizException(response.status(), ApiResponse.error(body), response.request());
                }
            }).orElseGet(() -> {
                String rootCauseMsg = ExceptionUtils.getRootCauseMessage(exception);
                return new FeignRpcBizException(response.status(), ApiResponse.error(rootCauseMsg), response.request());
            });
        }
        // 不会走到该分支
        return exception;
    }

    private boolean isApiResponse(ApiResponse<?> apiResponse) {
        if (null == apiResponse) {
            return false;
        }
        return StringUtils.isNotBlank(apiResponse.getCode()) || StringUtils.isNotBlank(apiResponse.getMessage());
    }
}
