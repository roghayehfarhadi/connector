package com.raha.sample.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raha.sample.service.ElasticSearchService;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ElasticServiceImpl implements ElasticSearchService {

    private static final Logger log = LoggerFactory.getLogger(ElasticServiceImpl.class);
    private static final int CREATE_INDEX_MAX_NUMBER_OF_TRIES = 10;

    private final Map<String, Integer> indexTries = new HashMap<>();
    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;

    @Override
    public boolean save(String index, Object object) {
        if (!createIndexIfNeeded(index)) return false;

        try {
            IndexResponse response = restHighLevelClient.index(
                    new IndexRequest(index)
                            .id(UUID.randomUUID().toString())
                            .source(objectMapper.convertValue(object, Map.class), XContentType.JSON)
                    , RequestOptions.DEFAULT
            );

            boolean isSuccess = HttpStatus.valueOf(response.status().getStatus()).is2xxSuccessful();
            if (isSuccess)
                log.info("........................ object created successfully ...........................");

            return isSuccess;
        } catch (IOException | ElasticsearchException e) {
            log.error("Unable to save object", e);
            return false;
        }
    }

    private synchronized boolean createIndexIfNeeded(String index) {
        int numTries = getTriesNumber(index);
        if (numTries == -1) return true;
        if (numTries >= CREATE_INDEX_MAX_NUMBER_OF_TRIES) return false;

        boolean indexExists;
        try {
            indexExists = restHighLevelClient.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("Unable to create index, {}", e.getMessage());
            return false;
        }

        boolean isSuccessful = indexExists || createIndex(index);
        indexTries.put(index, isSuccessful ? -1 : ++numTries);

        return isSuccessful;
    }

    private int getTriesNumber(String index) {
        if (!indexTries.containsKey(index)) {
            indexTries.put(index, 0);
            return 0;
        } else
            return indexTries.get(index);
    }

    private boolean createIndex(String index) {
        try {
            CreateIndexRequest request = new CreateIndexRequest(index)
                    .settings(Settings.builder()
                            .put("index.number_of_shards", 1)
                            .put("index.number_of_replicas", 0)
                    );

            CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
            log.debug("Elastic index '{}' creation result: {}", index, response.isAcknowledged());
            return response.isAcknowledged();
        } catch (IOException | ElasticsearchException e) {
            log.error(String.format("Error creating Elastic search index '%s'", index), e);
            return false;
        }
    }

}
