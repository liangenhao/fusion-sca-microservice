package io.fusion.framework.spring.cloud.web.openfeign.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import io.fusion.framework.core.api.ApiResponse;
import io.fusion.framework.core.exception.FeignRpcBizException;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.util.Optional;

/**
 * @author enhao
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
            Optional<ByteBuffer> byteBuffer = ((FeignException) exception).responseBody();
            if (byteBuffer.isPresent()) {
                String body = response.charset().decode(byteBuffer.get().asReadOnlyBuffer()).toString();
                ApiResponse<?> apiResponse = objectMapper.readValue(body, ApiResponse.class);
                return new FeignRpcBizException(response.status(), apiResponse, response.request());
            }
        }
        // 不会走到该分支
        return exception;
    }
}
