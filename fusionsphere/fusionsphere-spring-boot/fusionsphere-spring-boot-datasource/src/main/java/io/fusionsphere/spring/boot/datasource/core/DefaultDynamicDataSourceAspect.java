package io.fusionsphere.spring.boot.datasource.core;

import io.fusionsphere.spring.boot.datasource.annotation.TargetDataSource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

/**
 * 动态数据源切面
 *
 * @author enhao
 */
@Aspect
@Slf4j
public class DefaultDynamicDataSourceAspect implements DynamicDataSourceAspect {

    @Pointcut("@annotation(io.fusionsphere.spring.boot.datasource.annotation.TargetDataSource) " +
            "|| @within(io.fusionsphere.spring.boot.datasource.annotation.TargetDataSource)")
    public void switchDataSourcePointCut() {
    }

    @Around("switchDataSourcePointCut()")
    public Object invoke(ProceedingJoinPoint joinPoint) throws Throwable {
        TargetDataSource targetDataSource = resolveTargetDataSource(joinPoint);
        try {
            DynamicDataSourceContextHolder.push(targetDataSource.value());
            return joinPoint.proceed();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    /**
     * 解析当前目标数据源
     *
     * @return 当前目标数据源名称
     */
    private TargetDataSource resolveTargetDataSource(ProceedingJoinPoint joinPoint) {
        Method targetMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        TargetDataSource targetDataSource = AnnotationUtils.findAnnotation(targetMethod, TargetDataSource.class);
        if (null != targetDataSource) {
            return targetDataSource;
        }
        Class<?> targetClass = targetMethod.getDeclaringClass();
        return AnnotationUtils.findAnnotation(targetClass, TargetDataSource.class);
    }
}
