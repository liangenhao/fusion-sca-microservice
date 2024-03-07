package io.fusion.framework.spring.cloud.alibaba.sentinel.autoconfigure;

import com.alibaba.cloud.sentinel.custom.SentinelAutoConfiguration;
import com.alibaba.csp.sentinel.cluster.ClusterStateManager;
import com.alibaba.csp.sentinel.cluster.client.NettyTransportClient;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientAssignConfig;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientConfig;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientConfigManager;
import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterParamFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.registry.ConfigSupplierRegistry;
import com.alibaba.csp.sentinel.cluster.server.NettyTransportServer;
import com.alibaba.csp.sentinel.cluster.server.ServerConstants;
import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.cluster.server.config.ServerFlowConfig;
import com.alibaba.csp.sentinel.cluster.server.config.ServerTransportConfig;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.transport.config.TransportConfig;
import com.alibaba.csp.sentinel.util.HostNameUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.PropertyKeyConst;
import io.fusion.framework.spring.cloud.alibaba.sentinel.properties.ClusterAppAssignMap;
import io.fusion.framework.spring.cloud.alibaba.sentinel.properties.SentinelClusterEmbeddedProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.*;


/**
 * Sentinel 集群流控嵌入模式配置
 * <p>
 * Sentinel 的 {@link com.alibaba.csp.sentinel.init.InitFunc} SPI 扩展方式无法和 Spring 容器进行集成，因此无法读取到外部化配置。
 * 因此不使用 Sentinel SPI 扩展，通过 Spring 配置方式进行加载。
 * <p>
 * 由于 Sentinel SPI 是在 {@link SentinelAutoConfiguration} 初始化时加载，
 * 因此当前配置类在 {@link SentinelAutoConfiguration} 之后加载。
 *
 * @author enhao
 */
@Configuration
// 配置类不能放在包扫描路径下，且配置 spring.factories, 自动配置 @AutoConfigureAfter 才会生效
@AutoConfigureAfter(SentinelAutoConfiguration.class)
@ConditionalOnClass({NacosDataSource.class, NettyTransportClient.class, NettyTransportServer.class})
@ConditionalOnProperty(name = "fusion.sentinel-cluster.embedded.enabled", havingValue = "true")
@EnableConfigurationProperties(SentinelClusterEmbeddedProperties.class)
public class SentinelClusterEmbeddedAutoConfiguration {

    private final SentinelClusterEmbeddedProperties properties;

    public SentinelClusterEmbeddedAutoConfiguration(SentinelClusterEmbeddedProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        // 初始化客户端流控规则
        // 已默认通过 com.alibaba.cloud.sentinel.datasource.config.AbstractDataSourceProperties.postRegister 进行初始化

        // 初始化客户端配置
        initClientConfigProperty();
        // 初始化客户端和服务端分配映射
        initClientServerAssignProperty();

        // 服务端注册集群流控规则
        registerClusterRuleSupplier();
        // 初始化服务端传输配置
        initServerTransportConfigProperty();
        // 初始化全局流控配置
        initGlobalServerFlowConfigProperty();

        // 初始化客户端/服务端模式
        initStateProperty();
    }

    private void initStateProperty() {
        NacosDataSource<Integer> modeDs = newNacosDataSource(properties.getClusterMapDataId(), source -> {
            List<ClusterAppAssignMap> assignMaps = JSON.parseArray(source, ClusterAppAssignMap.class);
            return Optional.ofNullable(assignMaps)
                    .map(this::extractMode)
                    .orElse(ClusterStateManager.CLUSTER_NOT_STARTED);
        });
        ClusterStateManager.registerProperty(modeDs.getProperty());
    }

    private void initClientConfigProperty() {
        NacosDataSource<ClusterClientConfig> clientConfigDataSource = newNacosDataSource(
                properties.getClusterClientConfigDataId(),
                source -> JSON.parseObject(source, ClusterClientConfig.class));

        ClusterClientConfigManager.registerClientConfigProperty(clientConfigDataSource.getProperty());
    }

    private void initClientServerAssignProperty() {
        NacosDataSource<ClusterClientAssignConfig> datasource = newNacosDataSource(
                properties.getClusterMapDataId(), source -> {
                    List<ClusterAppAssignMap> assignMaps = JSON.parseArray(source, ClusterAppAssignMap.class);
                    return Optional.ofNullable(assignMaps)
                            .flatMap(this::extractClientAssignment)
                            .orElse(new ClusterClientAssignConfig());
                });
        ClusterClientConfigManager.registerServerAssignProperty(datasource.getProperty());
    }

    private void registerClusterRuleSupplier() {
        ClusterFlowRuleManager.setPropertySupplier(namespace -> {
            ReadableDataSource<String, List<FlowRule>> ds = newNacosDataSource(
                    namespace + "-flow-rules",
                    source -> JSON.parseArray(source, FlowRule.class));
            return ds.getProperty();
        });

        ClusterParamFlowRuleManager.setPropertySupplier(namespace -> {
            ReadableDataSource<String, List<ParamFlowRule>> ds = newNacosDataSource(
                    namespace + "-param-flow-rules",
                    source -> JSON.parseArray(source, ParamFlowRule.class));
            return ds.getProperty();
        });

        // 嵌入模式可以不注册 namespace 属性
        // 因为嵌入模式的 Token Server 在启动 netty 服务时会加载当前 appName 到 namespace 属性中，且嵌入模式 namespace 不存在变化的情况
        // 如果在嵌入模式下注册了 namespace 属性，一定要添加 default namespace，否认如果 namespaceSet 为空，会导致 namespace 被清空
        //   当添加了 default namespace，ServerNamespaceSetPropertyListener 会自动将当前 appName 添加入 namespace 中。
        // 如果是独立模式需要注册
        NacosDataSource<Set<String>> namespaceDs = newNacosDataSource(
                properties.getClusterMapDataId(),
                source -> {
                    List<ClusterAppAssignMap> assignMaps = JSON.parseArray(source, ClusterAppAssignMap.class);

                    return Optional.ofNullable(assignMaps)
                            .flatMap(assignMapList -> assignMapList.stream()
                                    .filter(this::machineEqual)
                                    .findAny()
                                    .map(assignMap -> {
                                        Set<String> namespaceSet = Optional.ofNullable(assignMap.getNamespaceSet())
                                                .orElseGet(HashSet::new);
                                        if (namespaceSet.isEmpty()) {
                                            namespaceSet.add(ServerConstants.DEFAULT_NAMESPACE);
                                        }
                                        return namespaceSet;
                                    }))
                            .orElse(new HashSet<>());
                });
        ClusterServerConfigManager.registerNamespaceSetProperty(namespaceDs.getProperty());
    }

    private void initServerTransportConfigProperty() {
        NacosDataSource<ServerTransportConfig> transportDs = newNacosDataSource(
                properties.getClusterMapDataId(),
                source -> {
                    List<ClusterAppAssignMap> assignMaps = JSON.parseArray(source, ClusterAppAssignMap.class);
                    return Optional.ofNullable(assignMaps)
                            .flatMap(this::extractServerTransportConfig)
                            .orElse(new ServerTransportConfig());
                });
        ClusterServerConfigManager.registerServerTransportProperty(transportDs.getProperty());
    }

    private void initGlobalServerFlowConfigProperty() {
        NacosDataSource<ServerFlowConfig> globalFlowDs = newNacosDataSource(
                properties.getClusterMapDataId(),
                source -> {
                    List<ClusterAppAssignMap> assignMaps = JSON.parseArray(source, ClusterAppAssignMap.class);
                    return Optional.ofNullable(assignMaps)
                            .flatMap(this::extractServerFlowConfig)
                            .orElse(new ServerFlowConfig(ConfigSupplierRegistry.getNamespaceSupplier().get()));
                });
        ClusterServerConfigManager.registerGlobalServerFlowProperty(globalFlowDs.getProperty());
    }

    private Optional<ServerFlowConfig> extractServerFlowConfig(List<ClusterAppAssignMap> groupList) {
        return groupList.stream()
                .filter(this::machineEqual)
                .findAny()
                .map(e -> new ServerFlowConfig(ConfigSupplierRegistry.getNamespaceSupplier().get())
                        .setMaxAllowedQps(e.getMaxAllowedQps()));
    }

    private Optional<ClusterClientAssignConfig> extractClientAssignment(List<ClusterAppAssignMap> groupList) {
        if (groupList.stream().anyMatch(this::machineEqual)) {
            return Optional.empty();
        }
        // Build client assign config from the client set of target server group.
        for (ClusterAppAssignMap group : groupList) {
            if (group.getClientSet().contains(getCurrentMachineId())) {
                String ip = group.getIp();
                Integer port = group.getPort();
                return Optional.of(new ClusterClientAssignConfig(ip, port));
            }
        }
        return Optional.empty();
    }

    private Optional<ServerTransportConfig> extractServerTransportConfig(List<ClusterAppAssignMap> groupList) {
        return groupList.stream()
                .filter(this::machineEqual)
                .findAny()
                .map(e -> new ServerTransportConfig().setPort(e.getPort()).setIdleSeconds(600));
    }

    private int extractMode(List<ClusterAppAssignMap> groupList) {
        // If any server group machineId matches current, then it's token server.
        if (groupList.stream().anyMatch(this::machineEqual)) {
            return ClusterStateManager.CLUSTER_SERVER;
        }
        // If current machine belongs to any of the token server group, then it's token client.
        // Otherwise it's unassigned, should be set to NOT_STARTED.
        boolean canBeClient = groupList.stream()
                .flatMap(e -> e.getClientSet().stream())
                .filter(Objects::nonNull)
                .anyMatch(e -> e.equals(getCurrentMachineId()));
        return canBeClient ? ClusterStateManager.CLUSTER_CLIENT : ClusterStateManager.CLUSTER_NOT_STARTED;
    }

    private boolean machineEqual(/*@Valid*/ ClusterAppAssignMap group) {
        return getCurrentMachineId().equals(group.getMachineId());
    }

    private String getCurrentMachineId() {
        // Note: this may not work well for container-based env.
        return HostNameUtil.getIp() + SEPARATOR + TransportConfig.getRuntimePort();
    }

    private static final String SEPARATOR = "@";

    private <T> NacosDataSource<T> newNacosDataSource(String dataId, Converter<String, T> converter) {
        Properties prop = new Properties();
        prop.setProperty(PropertyKeyConst.SERVER_ADDR, properties.getNacosServerAddr());
        prop.setProperty(PropertyKeyConst.USERNAME, properties.getUsername());
        prop.setProperty(PropertyKeyConst.PASSWORD, properties.getPassword());
        prop.setProperty(PropertyKeyConst.ENCODE, properties.getEncode());
        prop.setProperty(PropertyKeyConst.NAMESPACE, properties.getNamespace());

        return new NacosDataSource<>(prop, properties.getGroupId(), dataId, converter);
    }
}

