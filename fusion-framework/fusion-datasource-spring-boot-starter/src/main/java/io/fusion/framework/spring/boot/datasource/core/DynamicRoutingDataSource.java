package io.fusion.framework.spring.boot.datasource.core;

import io.fusion.framework.core.tools.lang.ReflectionUtil;
import io.fusion.framework.spring.boot.datasource.properties.DynamicDataSourceProperties;
import io.fusion.framework.spring.boot.datasource.properties.ExtensionDataSourceProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.Closeable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * 动态数据源
 *
 * @author enhao
 */
@Slf4j
public class DynamicRoutingDataSource extends AbstractRoutingDataSource implements DisposableBean {

    private final DynamicDataSourceProperties properties;

    public DynamicRoutingDataSource(DynamicDataSourceProperties properties) {
        this.properties = properties;
        initDataSources();
    }

    private void initDataSources() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        this.properties.getDatasource().forEach((key, property) -> {
            DataSource dataSource = createDataSource(property, this.properties.getConfiguration());
            targetDataSources.put(key, dataSource);
        });

        if (!StringUtils.hasText(this.properties.getPrimary())) {
            throw new IllegalArgumentException(String.format("Property '%s.primary' is required",
                    DynamicDataSourceProperties.PREFIX));
        }
        Object defaultDataSource = targetDataSources.get(this.properties.getPrimary());
        if (null == defaultDataSource) {
            throw new IllegalArgumentException(String.format("Property '%s.primary' value is invalid",
                    DynamicDataSourceProperties.PREFIX));
        }

        super.setDefaultTargetDataSource(defaultDataSource);
        super.setTargetDataSources(targetDataSources);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String currentDataSource = DynamicDataSourceContextHolder.peek();
        log.info("currentDataSource:{}", currentDataSource);
        return currentDataSource;
    }

    @Override
    public void destroy() throws Exception {
        for (Map.Entry<Object, DataSource> entry : getResolvedDataSources().entrySet()) {
            DataSource dataSource = entry.getValue();
            if (dataSource instanceof Closeable) {
                ((Closeable) dataSource).close();
            }
        }
    }

    // ============================== private methods ==============================

    /**
     * 创建数据源
     *
     * @param property              数据源属性配置 {@link ExtensionDataSourceProperties}
     * @param defaultConfigurations 连接池默认配置
     * @return 数据源 {@link DataSource} 实现
     */
    @SneakyThrows
    private DataSource createDataSource(ExtensionDataSourceProperties property, Map<String, Object> defaultConfigurations) {
        DataSource dataSource = property.initializeDataSourceBuilder()
                .type(property.getType())
                .build();
        setPoolConfiguration(dataSource, property, defaultConfigurations);
        return dataSource;
    }

    /**
     * 数据源连接池属性配置赋值
     *
     * @param dataSource            数据源
     * @param property              数据源配置
     * @param defaultConfigurations 数据库连接池全局配置
     * @throws IllegalAccessException    当通过字段或方法反射赋值失败时抛出异常
     * @throws InvocationTargetException 当通过方法反射赋值失败时抛出异常
     */
    private void setPoolConfiguration(DataSource dataSource, ExtensionDataSourceProperties property,
                                      @Nullable Map<String, Object> defaultConfigurations)
            throws IllegalAccessException, InvocationTargetException {
        Class<? extends DataSource> dataSourceClass = dataSource.getClass();

        // 连接池默认配置，即全局配置
        if (!CollectionUtils.isEmpty(defaultConfigurations)) {
            for (Map.Entry<String, Object> entry : defaultConfigurations.entrySet()) {
                try {
                    setProperties(dataSource, dataSourceClass, entry.getKey(), entry.getValue());
                } catch (Exception e) {
                    log.warn("Failed to set property '{}.configuration.{}' in {} error:{}",
                            DynamicDataSourceProperties.PREFIX, entry.getKey(), dataSourceClass, e.getMessage());
                }
            }
        }

        // 连接池数据源级别配置（覆盖默认配置）
        Map<String, Object> configuration = property.getConfiguration();
        if (!CollectionUtils.isEmpty(configuration)) {
            for (Map.Entry<String, Object> entry : configuration.entrySet()) {
                setProperties(dataSource, dataSourceClass, entry.getKey(), entry.getValue());
            }
        }
    }

    private void setProperties(DataSource dataSource, Class<? extends DataSource> dataSourceClass, String attr, Object value)
            throws IllegalAccessException, InvocationTargetException {
        Field field = ReflectionUtils.findField(dataSourceClass, attr);
        if (Objects.nonNull(field)) {
            field.setAccessible(true);
            field.set(dataSource, value);
        } else {
            Method setterMethod = findSetterMethod(attr, dataSourceClass, value);
            setterMethod.setAccessible(true);
            setterMethod.invoke(dataSource, value);
        }
    }

    private Method findSetterMethod(String attr, Class<? extends DataSource> dataSourceClass, Object value) {
        String setterMethodName = "set" + StringUtils.capitalize(attr);
        Method setterMethod = ReflectionUtil.findMethod(dataSourceClass, setterMethodName, value.getClass());
        if (Objects.isNull(setterMethod)) {
            throw new NoSuchElementException(
                    String.format("Field '%s' and Method '%s' is not found in %s with Super Class",
                            attr, setterMethodName, dataSourceClass)
            );
        }
        return setterMethod;
    }
}
