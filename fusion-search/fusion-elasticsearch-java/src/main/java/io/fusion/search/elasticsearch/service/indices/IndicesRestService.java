package io.fusion.search.elasticsearch.service.indices;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

/**
 * @author enhao
 */
@Service
public class IndicesRestService {

    private final RestHighLevelClient restHighLevelClient;

    public IndicesRestService(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }
}
