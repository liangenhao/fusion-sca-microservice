package enhao.autoconfigure;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = SentinelClusterProperties.PREFIX)
public class SentinelClusterProperties {

    public static final String PREFIX = "sentinel-cluster";

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
