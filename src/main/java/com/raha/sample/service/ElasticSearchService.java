package com.raha.sample.service;

public interface ElasticSearchService {
    boolean save(String index, Object object);
}
