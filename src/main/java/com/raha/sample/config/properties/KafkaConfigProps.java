package com.raha.sample.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "kafka-config")
public class KafkaConfigProps {
    private String serverAddress;
    private String groupId;
    private String offsetReset;
}
