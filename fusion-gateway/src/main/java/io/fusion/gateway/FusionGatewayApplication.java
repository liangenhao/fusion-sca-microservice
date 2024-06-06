package io.fusion.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author enhao
 */
@EnableDiscoveryClient
@SpringBootApplication
public class FusionGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(FusionGatewayApplication.class, args);
    }
}
