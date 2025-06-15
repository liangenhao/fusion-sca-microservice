package io.fusion.consumer.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
@RefreshScope
public class NacosConfigController {

    @Value("${useLocalCache:false}")
    private boolean useLocalCache;

    @Value("${fusionsphere.env-key}")
    private String envKey;

    @RequestMapping("/get")
    public boolean get() {
        return useLocalCache;
    }

    @GetMapping("envKey")
    public String envKey() {
        return envKey;
    }
}
