package io.fusion.framework.core.enums;

/**
 * @author enhao
 */
public enum ApiStatusCode {
    OK("0", "{api.statusCode.ok}"),
    SYSTEM_ERROR("-1", "{api.statusCode.systemError}"),
    UNKNOWN("-2", "{api.statusCode.unknown}"),
    FEIGN_DECODER_EXCEPTION("-3", "{api.statusCode.feignDecoderException}"),
    ;

    private final String code;

    private final String message;

    ApiStatusCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return getLocalizedMessage();
    }

    private String getLocalizedMessage() {
        // todo 国际化
        return message;
    }

    public static ApiStatusCode getByCode(String code) {
        for (ApiStatusCode apiStatusCode : values()) {
            if (apiStatusCode.getCode().equals(code)) {
                return apiStatusCode;
            }
        }
        return UNKNOWN;
    }
}
