package com.raha.sample.config;

import com.raha.sample.config.properties.ElasticConfigProps;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ElasticConfig {
    private final ElasticConfigProps elasticConfigProps;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(RestClient.builder(getHttpHost()));
    }

    private HttpHost getHttpHost() {
        return HttpHost.create(elasticConfigProps.getUrl());
    }
}
