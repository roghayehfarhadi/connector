package com.raha.sample.service;


import org.apache.kafka.streams.kstream.Predicate;

public interface KafkaStreamService {

    void filter(String readingTopic, Predicate<Object, Object> condition, String writingTopic);
}
