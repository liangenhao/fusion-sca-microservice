package io.fusion.framework.spring.cloud.alibaba.sentinel.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = SentinelClusterEmbeddedProperties.PREFIX)
public class SentinelClusterEmbeddedProperties {

    public static final String PREFIX = "fusion.sentinel-cluster.embedded";

    public static final String CLUSTER_CLIENT_CONFIG_POSTFIX = "-cluster-client-config";
    public static final String CLUSTER_MAP_POSTFIX = "-cluster-map";


    private boolean enabled = false;

    private String username = "nacos";

    private String password = "nacos";

    private String nacosServerAddr = "127.0.0.1:8848";

    private String groupId = "SENTINEL_RULES_CONFIG_GROUP";

    private String namespace = "";

    private String encode = "UTF-8";

    private String clusterClientConfigDataId;

    private String clusterMapDataId;

}
