package io.fusionsphere.spring.boot.okhttp3.autoconfigure;

import io.fusionsphere.spring.boot.okhttp3.core.TimeoutInterceptor;
import io.fusionsphere.spring.boot.okhttp3.properties.OkHttp3Properties;
import io.fusionsphere.spring.boot.okhttp3.utils.OkHttp3Utils;
import okhttp3.Cache;
import okhttp3.CertificatePinner;
import okhttp3.ConnectionPool;
import okhttp3.CookieJar;
import okhttp3.Dns;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp3 自动装配
 *
 * @author enhao
 */
@Configuration
@ConditionalOnClass({OkHttpClient.class})
@ConditionalOnProperty(name = OkHttp3Properties.PREFIX + ".enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(OkHttp3Properties.class)
public class OkHttp3AutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public X509TrustManager okHttp3trustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public SSLSocketFactory okHttp3SSLSocketFactory(X509TrustManager trustManager) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc.getSocketFactory();
    }


    @Bean
    @ConditionalOnMissingBean
    public ConnectionPool okHttp3ConnectionPool(OkHttp3Properties properties) {
        OkHttp3Properties.PoolProperties pool = properties.getPool();
        return new ConnectionPool(pool.getMaxIdleConnections(), pool.getKeepAliveDuration().toNanos(), TimeUnit.NANOSECONDS);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = OkHttp3Properties.PREFIX + ".cache.enabled", havingValue = "true")
    public Cache okHttp3Cache(OkHttp3Properties properties) throws IOException {
        OkHttp3Properties.CacheProperties cache = properties.getCache();
        File directory = cache.getDirectory();
        if (null == directory) {
            directory = Files.createTempDirectory("okhttp3-cache").toFile();
        }
        return new Cache(directory, cache.getMaxSize().toBytes());
    }

    @Bean
    // @ConditionalOnMissingBean
    public OkHttpClient okhttp3Client(OkHttp3Properties properties,
                                      X509TrustManager trustManager,
                                      SSLSocketFactory sslSocketFactory,
                                      ObjectProvider<CookieJar> cookieJar,
                                      ObjectProvider<Cache> cache,
                                      ObjectProvider<Dns> dns,
                                      ObjectProvider<HostnameVerifier> hostnameVerifier,
                                      ObjectProvider<CertificatePinner> certificatePinner,
                                      ConnectionPool connectionPool,
                                      ObjectProvider<EventListener> eventListener) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.sslSocketFactory(sslSocketFactory, trustManager);

        builder.connectTimeout(properties.getConnectTimeout())
                .readTimeout(properties.getReadTimeout())
                .writeTimeout(properties.getWriteTimeout());

        builder.pingInterval(properties.getPingInterval());

        builder.followRedirects(properties.isFollowRedirects())
                .followSslRedirects(properties.isFollowSslRedirects())
                .retryOnConnectionFailure(properties.isRetryOnConnectionFailure());

        cookieJar.ifUnique(builder::cookieJar);

        cache.ifUnique(builder::cache);

        dns.ifUnique(builder::dns);
        certificatePinner.ifUnique(builder::certificatePinner);

        HostnameVerifier verifier = hostnameVerifier.getIfUnique(() -> (s, sslSession) -> true);
        builder.hostnameVerifier(verifier);

        builder.connectionPool(connectionPool);

        builder.addInterceptor(new TimeoutInterceptor());

        eventListener.ifUnique(builder::eventListener);

        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public OkHttp3Utils okHttp3Utils(OkHttpClient okHttpClient) {
        return new OkHttp3Utils(okHttpClient);
    }
}
