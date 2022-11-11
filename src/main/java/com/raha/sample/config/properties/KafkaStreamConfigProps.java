package com.raha.sample.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "kafka-stream-config")
public class KafkaStreamConfigProps {
    private String serverAddress;
    private String applicationIdConfig;
}
