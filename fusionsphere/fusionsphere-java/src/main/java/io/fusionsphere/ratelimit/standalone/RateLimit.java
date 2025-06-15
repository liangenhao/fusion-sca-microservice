package io.fusionsphere.ratelimit.standalone;

/**
 * @author enhao
 */
public interface RateLimit {

    /**
     * 尝试获取许可(默认获取1个令牌)
     *
     * @return 是否申请成功
     */
    boolean tryAcquire();

    /**
     * 尝试获取许可
     *
     * @param permits 申请的许可数
     * @return 是否申请成功
     */
    boolean tryAcquire(int permits);
}
