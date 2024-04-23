package io.fusion.framework.core.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.fusion.framework.core.enums.ApiStatusCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author enhao
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    public static final ApiResponse<Void> VOID = ApiResponse.<Void>builder()
            .code(ApiStatusCode.OK.getCode()).message(ApiStatusCode.OK.getMessage()).build();

    public static final ApiResponse<Boolean> TRUE = ApiResponse.<Boolean>builder()
            .code(ApiStatusCode.OK.getCode()).message(ApiStatusCode.OK.getMessage()).body(true).build();

    public static final ApiResponse<Boolean> FALSE = ApiResponse.<Boolean>builder()
            .code(ApiStatusCode.OK.getCode()).message(ApiStatusCode.OK.getMessage()).body(false).build();

    private String code;

    private String message;

    private String reason;

    private T body;

    public static ApiResponse<Void> ok() {
        return VOID;
    }

    public static <T> ApiResponse<T> ok(T body) {
        return of(ApiStatusCode.OK, body);
    }

    public static <T> ApiResponse<T> fail(ApiStatusCode statusCode) {
        return of(statusCode, null);
    }

    public static <T> ApiResponse<T> fail(ApiStatusCode statusCode, String reason) {
        return of(statusCode, reason);
    }

    public static <T> ApiResponse<T> fail(String code, String message, String reason) {
        return of(code, message, reason, null);
    }

    public static <T> ApiResponse<T> error() {
        return of(ApiStatusCode.SYSTEM_ERROR, null);
    }

    public static <T> ApiResponse<T> error(String reason) {
        return of(ApiStatusCode.SYSTEM_ERROR, reason);
    }

    public static <T> ApiResponse<T> of(ApiStatusCode statusCode, String reason) {
        return of(statusCode, reason, null);
    }

    public static <T> ApiResponse<T> of(ApiStatusCode statusCode, T body) {
        return of(statusCode, null, body);
    }

    public static <T> ApiResponse<T> of(ApiStatusCode statusCode, String reason, T body) {
        return of(statusCode.getCode(), statusCode.getMessage(), reason, body);
    }

    private static <T> ApiResponse<T> of(String code, String message, String reason, T body) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .reason(reason) // todo 国际化
                .body(body)
                .build();
    }

    @JsonIgnore
    public boolean isOk() {
        return ApiStatusCode.OK.getCode().equals(this.getCode());
    }
}
