package enhao.consumer.controller;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import enhao.consumer.service.SentinelSimpleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sentinel-simple/")
public class SentinelSimpleController {

    private final SentinelSimpleService sentinelSimpleService;

    public SentinelSimpleController(SentinelSimpleService sentinelSimpleService) {
        this.sentinelSimpleService = sentinelSimpleService;
    }

    @GetMapping("/annotation/{name}")
    public String sentinelAnnotation(@PathVariable String name) {
        return sentinelSimpleService.sentinelAnnotation(name);
    }

    @GetMapping("/annotationWithFallbackClass/{name}")
    public String annotationWithFallbackClass(@PathVariable String name) {
        return sentinelSimpleService.annotationWithFallbackClass(name);
    }

    @GetMapping("/annotationWithoutFallback/{name}")
    public String annotationWithoutFallback(@PathVariable String name) {
        return sentinelSimpleService.annotationWithoutFallback(name);
    }

    @GetMapping("echoFeign/{str}")
    public String echoFeign(@PathVariable String str) throws DegradeException {
        return sentinelSimpleService.echoFeign(str);
    }
}
