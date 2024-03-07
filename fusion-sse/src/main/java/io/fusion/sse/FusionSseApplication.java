package io.fusion.sse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author enhao
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class FusionSseApplication {

    public static void main(String[] args) {
        SpringApplication.run(FusionSseApplication.class, args);
    }
}
