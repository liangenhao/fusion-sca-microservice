package io.fusion.distributed.transaction.storage;

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
public class DisTransactionStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(DisTransactionStorageApplication.class, args);
    }
}
