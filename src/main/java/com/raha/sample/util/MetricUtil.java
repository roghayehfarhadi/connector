package com.raha.sample.util;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MetricUtil {
    private final Map<String, Counter> counterMap = new ConcurrentHashMap<>();
    private final MeterRegistry meterRegistry;
    private static final String TOPIC = "topic";
    private static final String TYPE = "type";
    private static final String PUBLISHED_TYPE = "published";
    private static final String CONSUMED_TYPE = "consumed";
    private static final String METRIC_NAME = "kafka_messages";

    public MetricUtil(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void incrementPublishedMessage(String topic) {
        countEvent(topic, PUBLISHED_TYPE);
    }


    public void incrementConsumedMessage(String topic) {
        countEvent(topic, CONSUMED_TYPE);
    }

    public void countEvent(String topic, String type) {
        String key = topic + "-" + type;
        var counter = counterMap.get(key);
        if (counter == null) {
            counter = Counter.builder(METRIC_NAME)
                    .tag(TYPE, type)
                    .tag(TOPIC, topic)
                    .register(meterRegistry);
            counterMap.put(key, counter);
        }
        counter.increment();
    }
}
