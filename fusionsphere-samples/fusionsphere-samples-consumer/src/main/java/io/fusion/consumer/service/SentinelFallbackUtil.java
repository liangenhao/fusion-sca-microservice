package io.fusion.consumer.service;

import com.alibaba.csp.sentinel.slots.block.BlockException;

public class SentinelFallbackUtil {

    private SentinelFallbackUtil() {
    }

    public static String defaultFallback(Throwable th) {
        th.printStackTrace();
        return "default fallback";
    }

    public static String annotationWithBlockClass(String name, BlockException ex) {
        ex.printStackTrace();
        return "annotation with block class fallback " + name;
    }

    public static String annotationWithFallbackClass(String name, Throwable th) {
        th.printStackTrace();
        return "annotation with fallback class fallback " + name;
    }

}
