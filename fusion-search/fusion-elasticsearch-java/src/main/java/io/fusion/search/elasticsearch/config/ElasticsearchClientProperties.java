package io.fusion.search.elasticsearch.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * @author enhao
 */
@Data
@ConfigurationProperties(prefix = ElasticsearchClientProperties.PREFIX)
public class ElasticsearchClientProperties {
    public static final String PREFIX = "fusion.elasticsearch";

    private String username;

    private String password;

    private String scheme = "https";

    private List<String> hosts = Collections.singletonList("127.0.0.1:9200");

    private Duration connectionTimeout = Duration.ofSeconds(1);

    private Duration socketTimeout = Duration.ofSeconds(30);

    private String caCertificatePath;
}
