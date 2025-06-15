package io.fusion.search.elasticsearch;

import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.indices.Alias;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.elasticsearch.indices.IndexState;
import co.elastic.clients.elasticsearch.indices.get_alias.IndexAliases;
import co.elastic.clients.elasticsearch.indices.put_index_template.IndexTemplateMapping;
import co.elastic.clients.elasticsearch.indices.update_aliases.Action;
import io.fusion.search.elasticsearch.service.indices.IndicesClientService;
import io.fusion.search.elasticsearch.service.indices.IndicesRestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author enhao
 */
public class IndicesServiceTest extends BaseTest {

    static IndicesClientService indicesClientService = null;
    static IndicesRestService indicesRestService = null;

    @BeforeAll
    protected static void init() {
        indicesClientService = new IndicesClientService(elasticsearchClient, elasticsearchAsyncClient);
        indicesRestService = new IndicesRestService(restHighLevelClient);
    }

    @Test
    public void testDeleteIndex() {
        boolean ack = indicesClientService.deleteIndex(Collections.singletonList("my_api_index_0612"));
        Assertions.assertTrue(ack);
    }

    @Test
    public void testDeleteIndexAsync() throws InterruptedException {
        indicesClientService.deleteIndexAsync(Collections.singletonList("my_api_index_0612"));
        Thread.sleep(10000L);
    }

    @Test
    public void testCreateIndex() {
        /*
        请求:
        PUT /my_api_index_0612
        {
            "aliases": {
                "my_api_index": {

                }
            },
            "mappings": {
                "properties": {
                    "id": {
                        "type": "text",
                        "fields": {
                            "raw": {
                                "type": "keyword",
                                "ignore_above": 256
                            }
                        }
                    }
                }
            },
            "settings": {
                "number_of_shards": "1",
                "number_of_replicas": "0"
            }
        }

        响应:
        {"acknowledged":true,"shards_acknowledged":true,"index":"my_api_index_0612"}
         */
        Map<String, Alias> aliasMap = new HashMap<>();
        aliasMap.put("my_api_index", Alias.of(b -> b));
        TypeMapping mappings = TypeMapping.of(b -> b
                .properties("id", b1 -> b1
                        .text(b2 -> b2
                                .fields("raw", b3 -> b3
                                        .keyword(b4 ->
                                                b4.ignoreAbove(256)
                                        )
                                )
                        )
                )

        );

        boolean ack = indicesClientService.createIndex("my_api_index_0612",
                // IndexSettings.of(builder -> builder.numberOfShards("1").numberOfReplicas("0")),
                IndexSettings.of(b -> b.index(b1 -> b1.numberOfShards("1").numberOfReplicas("0"))),
                aliasMap, mappings);
        Assertions.assertTrue(ack);
    }

    @Test
    public void testGetIndex() {
        String index = "my_api_index_0612";
        indicesClientService.getIndex(index);
    }

    @Test
    public void testGetIndexSettings() {
        IndexState indexState = indicesClientService.getIndexSettings("my_api_index_0612");
        Assertions.assertEquals("0", indexState.settings().index().numberOfReplicas());
    }

    @Test
    public void testUpdateIndexSettings() {
        // Time#time
        // PUT /my_api_index_0612/_settings {"index":{"refresh_interval":"30s"}}
        IndexSettings indexSettings = IndexSettings.of(b ->
                b.index(b1 -> b1.refreshInterval(b2 -> b2.time("30s")))
        );
        boolean ack = indicesClientService.updateIndexSettings("my_api_index_0612", indexSettings);
        Assertions.assertTrue(ack);
    }

    @Test
    public void testAddIndexAlias() {
        // PUT /my_api_index_0612/_alias/my_api_index_write {"is_write_index":true}
        boolean ack = indicesClientService.addIndexAlias(Collections.singletonList("my_api_index_0612"),
                "my_api_index_write", true);
        Assertions.assertTrue(ack);
    }

    @Test
    public void testUpdateIndexAliases() {
        // POST /_aliases {"actions":[{"add":{"alias":"my_api_index_alias","index":"my_api_index_0612"}}]}
        List<Action> actions = Collections.singletonList(
                Action.of(b -> b
                        .add(b1 -> b1
                                .index("my_api_index_0612")
                                .alias("my_api_index_alias")
                        )
                )
        );
        boolean ack = indicesClientService.updateIndexAliases(actions);
        Assertions.assertTrue(ack);
    }

    @Test
    public void testExistsIndexAlias() {
        //   传 index:  HEAD /my_api_index_0612/_alias/my_api_index_alias
        // 不传 index:  HEAD /_alias/my_api_index_alias
        boolean ack = indicesClientService.existsIndexAlias("my_api_index_0612", "my_api_index_alias");
        Assertions.assertTrue(ack);
    }

    @Test
    public void testGetIndexAliases() {
        // GET /my_api_index_0612/_alias/my_api_index_alias,my_api_index_write
        IndexAliases indexAliases = indicesClientService.getIndexAliases("my_api_index_0612",
                Arrays.asList("my_api_index_alias", "my_api_index_write"));
        // {"my_api_index_0612":{"aliases":{"my_api_index_alias":{},"my_api_index_write":{"is_write_index":true}}}}
        Assertions.assertEquals(2, indexAliases.aliases().size());
    }

    @Test
    public void testDeleteIndexTemplate() {
        // DELETE /_index_template/tpl_no_replicas
        String templateName = "tpl_no_replicas";
        boolean ack = indicesClientService.deleteIndexTemplate(Collections.singletonList(templateName));
        Assertions.assertTrue(ack);
    }

    @Test
    public void testCreateIndexTemplateWithNoReplicas() {
        // PUT /_index_template/tpl_no_replicas {"index_patterns":["*"],"priority":1,"template":{"settings":{"index":{"number_of_replicas":"0"}}}}
        String templateName = "tpl_no_replicas";
        List<String> indexPatterns = Collections.singletonList("*");
        int priority = 1;
        IndexTemplateMapping mapping = IndexTemplateMapping.of(b -> b
                .settings(b1 -> b1
                        .index(b2 -> b2
                                .numberOfReplicas("0")
                        )
                )
        );
        boolean ack = indicesClientService.createIndexTemplate(templateName, indexPatterns, priority, mapping, null);
        Assertions.assertTrue(ack);
    }

}
