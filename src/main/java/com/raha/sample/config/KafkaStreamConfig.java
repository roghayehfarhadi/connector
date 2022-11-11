package com.raha.sample.config;

import com.raha.sample.config.properties.KafkaStreamConfigProps;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaStreamConfig {

    private final KafkaStreamConfigProps kafkaStreamConfigProps;

    @Bean
    public Properties kafkaStreamsProperties() {
        var properties = new Properties();
        properties.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaStreamConfigProps.getServerAddress());
        properties.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, kafkaStreamConfigProps.getApplicationIdConfig());
        properties.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
        properties.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());

        return properties;
    }
}
