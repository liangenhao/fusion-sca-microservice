package io.fusion.framework.rate.limiter.distributed;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author enhao
 */
@Data
public class RateLimiterResponse implements Serializable {

    private static final long serialVersionUID = 4364512834311613450L;

    /**
     * 限流是否通过
     */
    private final boolean allowed;

    /**
     * 剩余 token
     */
    private final long tokensLeft;

    private final List<String> keys;

    public RateLimiterResponse(boolean allowed, long tokensLeft, List<String> keys) {
        this.allowed = allowed;
        this.tokensLeft = tokensLeft;
        this.keys = keys;
    }
}

