package com.raha.sample.service;

public interface KafkaService {
    void publish(String topic, String key, String value);

    void subscribe(String topic);

    void seekAndConsumeMessage(String topic, int partition, int from, int to);
}
