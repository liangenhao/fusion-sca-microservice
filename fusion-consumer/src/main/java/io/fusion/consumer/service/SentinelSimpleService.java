package io.fusion.consumer.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import io.fusion.consumer.client.ProviderServiceEchoRpcClient;
import org.springframework.stereotype.Service;

@Service
public class SentinelSimpleService {

    private final ProviderServiceEchoRpcClient echoRpcClient;

    public SentinelSimpleService(ProviderServiceEchoRpcClient echoRpcClient) {
        this.echoRpcClient = echoRpcClient;
    }

    /**
     * {@link SentinelResource} 属性：注解方式埋点不支持 private 方法。
     * value：资源名称，必需项（不能为空）
     * entryType：entry 类型，可选项（默认为 EntryType.OUT）
     * <p>
     * blockHandler / blockHandlerClass：blockHandler 对应处理 BlockException 的函数名称，可选项。
     * blockHandler 函数访问范围需要是 public，返回类型需要与原方法相匹配，参数类型需要和原方法相匹配并且最后加一个额外的参数，类型为 BlockException。
     * blockHandler 函数默认需要和原方法在同一个类中。若希望使用其他类的函数，则可以指定 blockHandlerClass 为对应的类的 Class 对象，注意对应的函数必需为 static 函数，否则无法解析。
     * <p>
     * fallback / fallbackClass：fallback 函数名称，可选项，用于在抛出异常的时候提供 fallback 处理逻辑。
     * fallback 函数可以针对所有类型的异常（除了 exceptionsToIgnore 里面排除掉的异常类型）进行处理。
     * fallback 函数签名和位置要求：
     * - 返回值类型必须与原函数返回值类型一致；
     * - 方法参数列表需要和原函数一致，或者可以额外多一个 Throwable 类型的参数用于接收对应的异常。
     * - fallback 函数默认需要和原方法在同一个类中。若希望使用其他类的函数，则可以指定 fallbackClass 为对应的类的 Class 对象，注意对应的函数必需为 static 函数，否则无法解析。
     * <p>
     * defaultFallback（since 1.6.0）：默认的 fallback 函数名称，可选项，通常用于通用的 fallback 逻辑即可以用于很多服务或方法）。
     * 默认 fallback 函数可以针对所有类型的异常（除了 exceptionsToIgnore 里面排除掉的异常类型）进行处理。
     * 若同时配置了 fallback 和 defaultFallback，则只有 fallback 会生效。
     * defaultFallback 函数签名要求：
     * - 返回值类型必须与原函数返回值类型一致；
     * - 方法参数列表需要为空，或者可以额外多一个 Throwable 类型的参数用于接收对应的异常。
     * - defaultFallback 函数默认需要和原方法在同一个类中。若希望使用其他类的函数，则可以指定 fallbackClass 为对应的类的 Class 对象，注意对应的函数必需为 static 函数，否则无法解析。
     * <p>
     * exceptionsToIgnore（since 1.6.0）：用于指定哪些异常被排除掉，不会计入异常统计中，也不会进入 fallback 逻辑中，而是会原样抛出。
     * <p>
     * 特别地，若 blockHandler 和 fallback 都进行了配置，则被限流降级而抛出 BlockException 时只会进入 blockHandler 处理逻辑。
     * 若未配置 blockHandler、fallback 和 defaultFallback，则被限流降级时会将 BlockException 直接抛出
     * （若方法本身未定义 throws BlockException 则会被 JVM 包装一层 UndeclaredThrowableException）。
     * <p>
     * 从 1.4.0 版本开始，注解方式定义资源支持自动统计业务异常，无需手动调用 Tracer.trace(ex) 来记录业务异常。
     * Sentinel 1.4.0 以前的版本需要自行调用 Tracer.trace(ex) 来记录业务异常。（之前如果要记录业务异常，不能使用注解）
     * <p>
     * blockHandler 是触发了流控/熔断等规则后触发
     * fallback 是出现了业务异常就会触发的降级策略
     *
     * @param name 参数
     * @return {@link String}
     */
    @SentinelResource(value = "sentinelSimple", blockHandler = "blockHandler", fallback = "sentinelFallback")
    public String sentinelAnnotation(String name) {
        if ("error".equals(name)) {
            throw new IllegalArgumentException("name");
        }
        return "Hello, " + name;
    }

    @SentinelResource(value = "sentinelWithFallbackClass",
            blockHandler = "annotationWithBlockClass", blockHandlerClass = SentinelFallbackUtil.class,
            fallback = "annotationWithFallbackClass", fallbackClass = SentinelFallbackUtil.class)
    public String annotationWithFallbackClass(String name) {
        if ("error".equals(name)) {
            throw new IllegalArgumentException("name");
        }
        return "Hello, fallbackClass " + name;
    }

    @SentinelResource(value = "sentinelWithoutFallback")
    public String annotationWithoutFallback(String name) {
        if ("error".equals(name)) {
            throw new IllegalArgumentException("name");
        }
        return "Hello, without fallback " + name;
    }

    /**
     * 当 feign 调用异常后，会执行 fallback 降级方法。
     * <p>
     * 若触发了熔断规则，在熔断时长内则不会进行远程调用。若 @FeignClient 还配置了 fallback，直接执行 fallback 方法。
     * 若 @FeignClient 未配置 fallback，则会抛出 DegradeException，
     * （若方法本身未定义 throws BlockException 则会被 JVM 包装一层 UndeclaredThrowableException）
     *
     * @param str 参数
     * @return {@link String}
     */
    public String echoFeign(String str) throws BlockException {
        return echoRpcClient.echo(str);
    }

    // sentinel fallback
    public String blockHandler(String name, BlockException ex) {
        ex.printStackTrace();
        return "Hello, " + name + " block handler";
    }

    public String sentinelFallback(String name, Throwable th) {
        th.printStackTrace();
        return "Hello, " + name + " sentinel fallback";
    }

}
