package io.fusion.search.elasticsearch.controller;

import co.elastic.clients.elasticsearch.indices.IndexState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fusion.search.elasticsearch.service.indices.IndicesClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author enhao
 */
@RestController
@RequestMapping("indices")
public class IndicesController {

    private final IndicesClientService indicesClientService;

    public IndicesController(IndicesClientService indicesClientService) {
        this.indicesClientService = indicesClientService;
    }

    @GetMapping("getIndex")
    public Object getIndex(@RequestParam String index) {
        IndexState indexSettings = indicesClientService.getIndexSettings(index);
        return indexSettings.toString();
    }


}
