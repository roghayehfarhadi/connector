package com.raha.sample.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raha.sample.serde.UserInfoSerde;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@RequiredArgsConstructor
public class UserProcessor {
    private final Logger logger = LoggerFactory.getLogger(UserProcessor.class);

    private final ObjectMapper objectMapper;
    private final Properties kafkaStreamsProperties;
    private static final String USER_TOPIC = "user";

    @Bean
    public void filterUserInfo() {
        var streamsBuilder = new StreamsBuilder();
        streamsBuilder
                .stream(USER_TOPIC, Consumed.with(Serdes.String(), new UserInfoSerde(objectMapper)))
                .peek((key, userInfo) -> logger.info("Received user with name of: {} and value of: {}", userInfo.getName(), userInfo.getFamily()))
                .filter((key, userInfo) -> userInfo.getName().equals("raha") || userInfo.getName().equals("mina"))
                .peek((key, userInfo) -> logger.info("Filtered user with name of: {} and value of: {}", userInfo.getName(), userInfo.getFamily()));
        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), kafkaStreamsProperties);
        kafkaStreams.start();
    }
}
