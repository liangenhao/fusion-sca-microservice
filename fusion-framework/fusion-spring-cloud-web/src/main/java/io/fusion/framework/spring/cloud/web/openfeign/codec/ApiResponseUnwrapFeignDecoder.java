package io.fusion.framework.spring.cloud.web.openfeign.codec;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import io.fusion.framework.core.api.ApiResponse;
import io.fusion.framework.core.exception.FeignRpcBizException;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Feign 响应结果 {@link ApiResponse} 拆包成 POJO 对象 {@link Decoder} 实现
 *
 * @author enhao
 */
public class ApiResponseUnwrapFeignDecoder implements Decoder {

    private final Decoder delegate;

    private final ObjectMapper objectMapper;

    public ApiResponseUnwrapFeignDecoder(Decoder delegate, ObjectMapper objectMapper) {
        this.delegate = delegate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        // 需先判断 type 类型，若 type(FeignClient方法返回值类型) 是 ApiResponse 不拆包
        if (type instanceof ParameterizedType
                && ((ParameterizedType) type).getRawType() == ApiResponse.class) {
            Object result = delegate.decode(response, type);
            if (result instanceof ApiResponse) {
                ApiResponse<?> apiResponse = (ApiResponse<?>) result;
                if (!apiResponse.isOk()) {
                    throw new FeignRpcBizException(response.status(), apiResponse, response.request());
                }
            }
            return result;
        }

        // webmvc ApiResponseWrapReturnValueHandler 会将返回结果包装成 ApiResponse
        // 这里直接 decode 为 ApiResponse
        Object result = delegate.decode(response, ApiResponse.class);
        if (result instanceof ApiResponse) {
            ApiResponse<?> apiResponse = (ApiResponse<?>) result;
            if (apiResponse.isOk()) {
                Object body = apiResponse.getBody();
                JavaType javaType = TypeFactory.defaultInstance().constructType(type);
                return objectMapper.convertValue(body, javaType);
            }
            throw new FeignRpcBizException(response.status(), apiResponse, response.request());
        }

        // 不会走到该分支
        return result;
    }

}
