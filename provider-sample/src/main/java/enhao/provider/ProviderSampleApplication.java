package enhao.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ProviderSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProviderSampleApplication.class, args);
    }
}
