package io.fusion.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FusionProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(FusionProviderApplication.class, args);
    }
}
