package com.raha.sample;

import com.raha.sample.config.properties.RabbitMQConfigProps;
import com.raha.sample.service.KafkaService;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.io.IOException;

@RequiredArgsConstructor
@ConfigurationPropertiesScan
@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private final String moonTopic;
    private final String sunTopic;
    private final KafkaService kafkaService;
    private final RestHighLevelClient restHighLevelClient;
    private final RabbitMQConfigProps rabbitMQConfigProps;
    private final RabbitTemplate rabbitTemplate;


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws InterruptedException {
        pingElasticsearch();
        kafkaService.publish(moonTopic, "id_", "hello moon");
        kafkaService.subscribe(moonTopic);
        kafkaService.publish(sunTopic, "id_", "hello sun");
        kafkaService.subscribe(sunTopic);
        kafkaService.seekAndConsumeMessage(moonTopic, 0, 0, 5);
        rabbitTemplate.convertAndSend(rabbitMQConfigProps.getExchangeName(), rabbitMQConfigProps.getRoutingKey(), "herro");
    }

    private void pingElasticsearch() throws InterruptedException {
        boolean isConnected = false;
        while (!isConnected) {
            try {
                isConnected = restHighLevelClient.ping(RequestOptions.DEFAULT);
            } catch (IOException e) {
                logger.info("connection to elasticsearch is n't established");
                Thread.sleep(6000);
            }
        }
    }
}
