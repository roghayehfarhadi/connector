package com.raha.sample.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "rabbit-config")
public class RabbitMQConfigProps {

    private String serverAddress;
    private int serverPort;
    private String queueName;
    private String exchangeName;
    private String routingKey;
}
