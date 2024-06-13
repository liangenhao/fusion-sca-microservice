package io.fusion.search.elasticsearch.service.indices;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.indices.*;
import co.elastic.clients.elasticsearch.indices.get_alias.IndexAliases;
import co.elastic.clients.elasticsearch.indices.update_aliases.Action;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author enhao
 */
@Slf4j
@Service
public class IndicesClientService {

    private final ElasticsearchClient client;

    private final ElasticsearchAsyncClient asyncClient;

    public IndicesClientService(ElasticsearchClient client, ElasticsearchAsyncClient asyncClient) {
        this.client = client;
        this.asyncClient = asyncClient;
    }


    @SneakyThrows
    public boolean deleteIndex(List<String> index) {
        DeleteIndexResponse response = client.indices().delete(builder -> builder.index(index));
        return response.acknowledged();
    }

    @SneakyThrows
    public void deleteIndexAsync(List<String> index) {
        asyncClient.indices()
                .delete(builder -> builder.index(index))
                .whenComplete((response, throwable) -> {
                    if (null != throwable) {
                        log.error("[deleteIndexAsync] failed", throwable);
                    } else {
                        log.info("[deleteIndexAsync] success, response:{}", response);
                    }
                });
    }

    /**
     * 创建索引
     */
    @SneakyThrows
    public boolean createIndex(String index, IndexSettings settings, Map<String, Alias> aliases, TypeMapping mappings) {
        CreateIndexRequest createIndexRequest = CreateIndexRequest.of(builder -> {
            builder.index(index);
            if (settings != null) {
                builder.settings(settings);
            }
            if (aliases != null) {
                builder.aliases(aliases);
            }
            if (mappings != null) {
                builder.mappings(mappings);
            }
            return builder;
        });
        printRequest(createIndexRequest.toString());
        CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest);
        printResponse(createIndexResponse.toString());
        return createIndexResponse.acknowledged();
    }

    @SneakyThrows
    public IndexState getIndex(String index) {
        GetIndexRequest request = GetIndexRequest.of(b -> b.index(index));
        printRequest(request.toString());
        GetIndexResponse response = client.indices().get(request);
        printResponse(response.toString());
        return response.get(index);
    }

    @SneakyThrows
    public IndexState getIndexSettings(String index) {
        GetIndicesSettingsRequest request = GetIndicesSettingsRequest.of(b ->
                b.index(Collections.singletonList(index)));
        printRequest(request.toString());
        GetIndicesSettingsResponse settings = client.indices().getSettings(request);
        printResponse(settings.toString());
        return settings.get(index);
    }

    @SneakyThrows
    public boolean updateIndexSettings(String index, IndexSettings settings) {
        PutIndicesSettingsRequest request = PutIndicesSettingsRequest.of(b -> {
            b.index(Collections.singletonList(index));
            if (null != settings) {
                b.settings(settings);
            }
            return b;
        });
        printRequest(request.toString());
        PutIndicesSettingsResponse response = client.indices().putSettings(request);
        printResponse(response.toString());
        return response.acknowledged();
    }

    @SneakyThrows
    public boolean addIndexAlias(List<String> index, String alias, boolean isWriteIndex) {
        PutAliasRequest request = PutAliasRequest.of(b -> b
                .index(index)
                .name(alias)
                .isWriteIndex(isWriteIndex)
        );
        printRequest(request.toString());
        PutAliasResponse response = client.indices().putAlias(request);
        printResponse(response.toString());
        return response.acknowledged();

    }

    @SneakyThrows
    public boolean updateIndexAliases(List<Action> actions) {
        UpdateAliasesRequest request = UpdateAliasesRequest.of(b -> b.actions(actions));
        printRequest(request.toString());
        UpdateAliasesResponse response = client.indices().updateAliases(request);
        printResponse(response.toString());
        return response.acknowledged();
    }

    @SneakyThrows
    public boolean existsIndexAlias(String index, String alias) {
        ExistsAliasRequest request = ExistsAliasRequest.of(b -> {
            b.name(alias);
            if (StringUtils.hasText(index)) {
                b.index(index);
            }
            return b;
        });
        printRequest(request.toString());
        BooleanResponse response = client.indices().existsAlias(request);
        printResponse(response.toString());
        return response.value();
    }

    @SneakyThrows
    public IndexAliases getIndexAliases(String index, List<String> aliases) {
        GetAliasRequest request = GetAliasRequest.of(b -> {
            b.index(index);
            if (!CollectionUtils.isEmpty(aliases)) {
                b.name(aliases);
            }
            return b;
        });
        printRequest(request.toString());
        GetAliasResponse response = client.indices().getAlias(request);
        printResponse(response.toString());
        return response.get(index);
    }

    // ========== private 方法 ==========

    private void printRequest(String request) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        log.error("[{}] -> req: {}", methodName, request);
    }

    private void printResponse(String response) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        log.error("[{}] <- res: {}", methodName, response);
    }

}
