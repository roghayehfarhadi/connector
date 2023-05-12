package com.raha.sample.config;

import com.raha.sample.Application;
import com.raha.sample.config.properties.KafkaConfigProps;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaConfigProps kafkaConfigProps;
    private final Logger logger = LoggerFactory.getLogger(Application.class);

    @Bean
    public Admin admin() {
        var properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigProps.getServerAddress());
        return Admin.create(properties);
    }

    @Bean
    public String moonTopic() throws ExecutionException, InterruptedException {
        return createTopic("moon");
    }

    @Bean
    public String sunTopic() throws ExecutionException, InterruptedException {
        return createTopic("sun");
    }


    @Bean
    public KafkaProducer<String, String> kafkaProducer() {
        var properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigProps.getServerAddress());
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return new KafkaProducer<>(properties);
    }

    @Bean
    public KafkaConsumer<String, String> kafkaConsumer() {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigProps.getServerAddress());
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfigProps.getGroupId());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConfigProps.getOffsetReset());

        return new KafkaConsumer<>(properties);
    }

    @Bean
    public KafkaConsumer<String, String> kafkaSeekConsumer() {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigProps.getServerAddress());
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConfigProps.getOffsetReset());

        return new KafkaConsumer<>(properties);
    }

    private String createTopic(String topicName) throws InterruptedException, ExecutionException {
        if (!isTopicExist(topicName)) {
            var newTopic = new NewTopic(topicName, 2, (short) 2);
            admin().createTopics(Collections.singleton(newTopic));
            logger.info("The '{}' topic was created.", topicName);
        }

        return topicName;
    }

    private boolean isTopicExist(String topicName) throws InterruptedException, ExecutionException {
        return admin().listTopics().names().get()
                .stream()
                .anyMatch(name -> name.equalsIgnoreCase(topicName));
    }
}
