package io.fusion.provider.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@RequestMapping("config-import")
public class NacosConfigImportController {

    @Value("${providerCustomKey}")
    private String providerCustomKey;

    @Value("${fusionsphere.env-key}")
    private String envKey;

    @GetMapping("get")
    public String get() {
        return providerCustomKey;
    }

    @GetMapping("envKey")
    public String envKey() {
        return envKey;
    }
}
