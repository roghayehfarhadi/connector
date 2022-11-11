package com.raha.sample.service.impl;

import com.raha.sample.service.KafkaStreamService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Predicate;
import org.springframework.stereotype.Service;

import java.util.Properties;


@Service
@RequiredArgsConstructor
public class KafkaStreamServiceImpl implements KafkaStreamService {

    private final Properties kafkaStreamsProperties;

    @Override
    public void filter(String readingTopic, Predicate<Object, Object> condition, String writingTopic) {
        StreamsBuilder streamsBuilder = createStreamBuilder();
        streamsBuilder
                .stream(readingTopic, Consumed.with(Serdes.String(), Serdes.String()))
                .filter(condition)
                .to(writingTopic);
        startKafkaStreams(streamsBuilder.build(), kafkaStreamsProperties);
    }

    private static StreamsBuilder createStreamBuilder() {
        return new StreamsBuilder();
    }

    private void startKafkaStreams(Topology topology, Properties properties) {
        KafkaStreams kafkaStreams = new KafkaStreams(topology, properties);
        kafkaStreams.start();
    }
}
