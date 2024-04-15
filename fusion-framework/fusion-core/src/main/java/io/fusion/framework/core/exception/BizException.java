package io.fusion.framework.core.exception;

import feign.codec.DecodeException;
import io.fusion.framework.core.enums.ApiStatusCode;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * @author enhao
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = -7513016438758276633L;

    private final ApiStatusCode statusCode;

    private final String code;

    private final String codeMessage;

    private String reason;

    /**
     * 在 {@link feign.codec.Decoder} 中抛出的 {@link BizException} 会被包装成 {@link DecodeException}
     * 通过此方法拆包出 {@link BizException}
     *
     * @param throwable {@link Throwable}
     * @return {@link Optional}
     * @deprecated feign 调用业务异常已通过 {@link FeignRpcBizException} 代替
     */
    public static Optional<BizException> unpack(Throwable throwable) {
        if (throwable instanceof DecodeException) {
            Throwable cause = throwable.getCause();
            if (isBizException(cause)) {
                return Optional.of((BizException) cause);
            }
        } else if (isBizException(throwable)) {
            return Optional.of((BizException) throwable);
        }
        return Optional.empty();
    }

    public static boolean isBizException(Throwable throwable) {
        return throwable instanceof BizException;
    }

    public BizException(ApiStatusCode statusCode) {
        super(statusCode.getMessage());
        this.statusCode = statusCode;
        this.code = statusCode.getCode();
        this.codeMessage = statusCode.getMessage();
    }

    public BizException(ApiStatusCode statusCode, String reason) {
        super(statusCode.getMessage() + (StringUtils.hasText(reason) ? "|" + reason : ""));
        this.statusCode = statusCode;
        this.code = statusCode.getCode();
        this.codeMessage = statusCode.getMessage();
        this.reason = reason; // todo 国际化
    }

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
