package io.fusion.search.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.ElasticsearchTransport;
import io.fusion.search.elasticsearch.config.ElasticsearchClientConfig;
import io.fusion.search.elasticsearch.config.ElasticsearchClientProperties;
import lombok.SneakyThrows;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.AfterAll;

/**
 * @author enhao
 */
public abstract class BaseTest {

    protected static RestClient restClient;
    protected static RestHighLevelClient restHighLevelClient;
    protected static ElasticsearchClient elasticsearchClient;
    protected static ElasticsearchAsyncClient elasticsearchAsyncClient;

    static {
        ElasticsearchClientConfig config = new ElasticsearchClientConfig();
        restClient = config.restClient(getProperties());

        restHighLevelClient = config.restHighLevelClient(restClient);

        ElasticsearchTransport elasticsearchTransport = config.elasticsearchTransport(restClient);
        elasticsearchClient = config.elasticsearchClient(elasticsearchTransport);
        elasticsearchAsyncClient = config.elasticsearchAsyncClient(elasticsearchTransport);
    }

    private static ElasticsearchClientProperties getProperties() {
        ElasticsearchClientProperties properties = new ElasticsearchClientProperties();
        properties.setUsername("xxx");
        properties.setPassword("xxx");
        properties.setCaCertificatePath("xxx");
        return properties;
    }

    @AfterAll
    @SneakyThrows
    public static void finish() {
        restClient.close();
    }
}
