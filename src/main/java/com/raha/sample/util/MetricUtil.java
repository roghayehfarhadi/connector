package com.raha.sample.util;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MetricUtil {

    private static final String TOPIC = "topic";
    private static final String TYPE = "type";
    private static final String PUBLISHED_TYPE = "published";
    private static final String CONSUMED_TYPE = "consumed";
    private static final String MOON_METRIC_NAME = "moon_message_counter";
    private static final String SUN_METRIC_NAME = "sun_message_counter";
    private final String moonTopic;
    private final String sunTopic;
    private final Counter moonPublishedMessage;
    private final Counter moonConsumedMessage;
    private final Counter sunPublishedMessage;
    private final Counter sunConsumedMessage;


    public MetricUtil(MeterRegistry meterRegistry, String moonTopic, String sunTopic) {
        this.moonTopic = moonTopic;
        this.sunTopic = sunTopic;

        this.moonPublishedMessage = Counter.builder(MOON_METRIC_NAME)
                .tag(TYPE, PUBLISHED_TYPE)
                .tag(TOPIC, moonTopic)
                .register(meterRegistry);

        this.moonConsumedMessage = Counter.builder(MOON_METRIC_NAME)
                .tag(TYPE, CONSUMED_TYPE)
                .tag(TOPIC, moonTopic)
                .register(meterRegistry);


        this.sunPublishedMessage = Counter.builder(SUN_METRIC_NAME)
                .tag(TYPE, PUBLISHED_TYPE)
                .tag(TOPIC, sunTopic)
                .register(meterRegistry);

        this.sunConsumedMessage = Counter.builder(SUN_METRIC_NAME)
                .tag(TYPE, CONSUMED_TYPE)
                .tag(TOPIC, sunTopic)
                .register(meterRegistry);
    }

    public void incrementPublishedMessage(String topic) {
        if (topic.equals(sunTopic)) {
            this.sunPublishedMessage.increment();
        } else if (topic.equals(moonTopic)) {
            this.moonPublishedMessage.increment();
        }
    }


    public void incrementConsumedMessage(String topic) {
        if (topic.equals(sunTopic)) {
            this.sunConsumedMessage.increment();
        } else if (topic.equals(moonTopic)) {
            this.moonConsumedMessage.increment();
        }
    }
}
