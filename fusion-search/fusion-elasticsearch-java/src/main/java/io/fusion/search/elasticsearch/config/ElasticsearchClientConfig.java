package io.fusion.search.elasticsearch.config;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestHighLevelClientBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author enhao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ElasticsearchClientProperties.class)
public class ElasticsearchClientConfig {

    @Bean(destroyMethod = "close")
    @SneakyThrows
    public RestClient restClient(ElasticsearchClientProperties properties) {
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/8.2/java-rest-low-config.html
        // 登录凭证
        CredentialsProvider credentialsProvider = getCredentialsProvider(properties);

        // SSL
        SSLContext sslContext = getSslContext(properties);

        HttpHost[] httpHosts = properties.getHosts().stream().map(host -> {
            String[] hosts = host.split(":");
            return new HttpHost(hosts[0], Integer.parseInt(hosts[1]), properties.getScheme());
        }).toArray(HttpHost[]::new);

        return RestClient.builder(httpHosts)
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider)
                        .setSSLContext(sslContext))
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                        .setConnectTimeout(Math.toIntExact(properties.getConnectionTimeout().toMillis()))
                        .setSocketTimeout(Math.toIntExact(properties.getSocketTimeout().toMillis())))
                .setFailureListener(new RestClient.FailureListener() {
                    @Override
                    public void onFailure(Node node) {
                        log.error("[NodeFailed] node: {}", node);
                    }
                })
                .build();
    }

    @SneakyThrows
    private SSLContext getSslContext(ElasticsearchClientProperties properties) {
        Path caCertificatePath = Paths.get(properties.getCaCertificatePath());
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        Certificate trustedCa;
        try (InputStream is = Files.newInputStream(caCertificatePath)) {
            trustedCa = factory.generateCertificate(is);
        }
        KeyStore trustStore = KeyStore.getInstance("pkcs12");
        trustStore.load(null, null);
        trustStore.setCertificateEntry("ca", trustedCa);
        SSLContextBuilder sslContextBuilder = SSLContexts.custom().loadTrustMaterial(trustStore, null);
        return sslContextBuilder.build();
    }

    private CredentialsProvider getCredentialsProvider(ElasticsearchClientProperties properties) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(properties.getUsername(), properties.getPassword()));
        return credentialsProvider;
    }

    @Bean
    public RestHighLevelClient restHighLevelClient(RestClient restClient) {
        return new RestHighLevelClientBuilder(restClient)
                .setApiCompatibilityMode(true)
                .build();
    }

    @Bean
    public ElasticsearchTransport elasticsearchTransport(RestClient restClient) {
        return new RestClientTransport(restClient, new JacksonJsonpMapper());
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(ElasticsearchTransport transport) {
        return new ElasticsearchClient(transport);
    }

    @Bean
    public ElasticsearchAsyncClient elasticsearchAsyncClient(ElasticsearchTransport transport) {
        return new ElasticsearchAsyncClient(transport);
    }
}
