package io.fusionsphere.spring.cloud.web.core.exception;

import feign.FeignException;
import feign.Request;
import io.fusionsphere.spring.cloud.web.core.ApiResponse;
import io.fusionsphere.spring.cloud.web.core.ApiStatusCode;
import org.springframework.util.StringUtils;

/**
 * @author enhao
 */
public class FeignRpcBizException extends FeignException {

    private static final long serialVersionUID = -357333188792897085L;

    private final ApiStatusCode statusCode;

    private final String code;

    private final String codeMessage;

    private final String reason;

    public FeignRpcBizException(int status, ApiResponse<?> apiResponse) {
        super(status, apiResponse.getMessage() + (StringUtils.hasText(apiResponse.getReason()) ? "|" + apiResponse.getReason() : ""));
        this.statusCode = ApiStatusCode.getByCode(apiResponse.getCode());
        this.code = apiResponse.getCode();
        this.codeMessage = apiResponse.getMessage();
        this.reason = apiResponse.getReason();
    }

    public FeignRpcBizException(int status, ApiResponse<?> apiResponse, Request request) {
        super(status, apiResponse.getMessage() + (StringUtils.hasText(apiResponse.getReason()) ? "|" + apiResponse.getReason() : ""), request);
        this.statusCode = ApiStatusCode.getByCode(apiResponse.getCode());
        this.code = apiResponse.getCode();
        this.codeMessage = apiResponse.getMessage();
        this.reason = apiResponse.getReason();
    }

    /**
     * @return {@link ApiStatusCode}
     * @deprecated rpc 调用返回的业务状态码枚举值，可能在当前服务中不存在，因此不建议使用
     */
    public ApiStatusCode getStatusCode() {
        return statusCode;
    }

    public String getCode() {
        return code;
    }

    public String getCodeMessage() {
        return codeMessage;
    }

    public String getReason() {
        return reason;
    }
}
