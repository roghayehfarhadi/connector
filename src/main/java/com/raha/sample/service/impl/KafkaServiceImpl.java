package com.raha.sample.service.impl;

import co.elastic.apm.api.CaptureTransaction;
import com.raha.sample.config.properties.ElasticConfigProps;
import com.raha.sample.log.KafkaMessageLog;
import com.raha.sample.log.MessageLogger;
import com.raha.sample.service.ElasticSearchService;
import com.raha.sample.service.KafkaService;
import com.raha.sample.util.MetricUtil;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

import static com.raha.sample.log.LogType.CONSUMED;
import static com.raha.sample.log.LogType.PUBLISHED;

@Service
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaServiceImpl.class);

    private final KafkaProducer<String, String> kafkaProducer;
    private final KafkaConsumer<String, String> kafkaConsumer;
    private final KafkaConsumer<String, String> kafkaSeekConsumer;
    private final MessageLogger messageLogger;
    private final ElasticSearchService elasticSearchService;
    private final ElasticConfigProps elasticConfigProps;

    private final MetricUtil metricUtil;

    @CaptureTransaction("KafkaServiceImpl#publishMessage")
    @Override
    public void publish(String topic, String key, String value) {
        for (int i = 0; i < 10; i++) {
            kafkaProducer.send(new ProducerRecord<>(topic, key + i, value), logPublishedMessage(value));
            metricUtil.incrementPublishedMessage( topic);
        }

    }

    @CaptureTransaction("KafkaServiceImpl#consumeMessage")
    @Override
    public void subscribe(String topic) {
        kafkaConsumer.subscribe(Collections.singletonList(topic));
        pollMessages();
    }

    @CaptureTransaction("KafkaServiceImpl#seekAnConsumeMessage")
    @Override
    public void seekAndConsumeMessage(String topic, int partition, int from, int to) {
        var topicPartition = new TopicPartition(topic, partition);
        kafkaSeekConsumer.assign(Collections.singleton(topicPartition));
        kafkaSeekConsumer.seek(topicPartition, from);
        pollMessages(to);
    }

    private Callback logPublishedMessage(String value) {
        return (record, exception) -> Optional.ofNullable(exception)
                .ifPresentOrElse(e -> logger.error("Error while producing ", e)
                        , () -> logPublishedMessage(record, value)
                );
    }

    private void pollMessages() {
        kafkaConsumer.poll(Duration.ofMillis(10000))
                .forEach(this::logConsumedMessage);
    }

    private void pollMessages(int to) {
        var keepOnReading = true;
        var numberOfReadMessages = 0;
        while (keepOnReading) {
            var consumerRecords = kafkaSeekConsumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                if (numberOfReadMessages < to) {
                    logConsumedMessage(consumerRecord);
                    numberOfReadMessages++;
                } else {
                    keepOnReading = false;
                    break;
                }
            }
        }
    }

    private void logPublishedMessage(RecordMetadata record, String value) {
        var messageLog = new KafkaMessageLog(null, value, PUBLISHED, record.topic(), record.partition(), record.offset());
        log(messageLog);
    }


    private void logConsumedMessage(ConsumerRecord<String, String> record) {
        var messageLog = new KafkaMessageLog(record.key(), record.value(), CONSUMED, record.topic(), record.partition(), record.offset());
        log(messageLog);
        metricUtil.incrementConsumedMessage(record.topic());
    }

    private void log(KafkaMessageLog kafkaMessageLog) {
        messageLogger.log(kafkaMessageLog);
        elasticSearchService.save(elasticConfigProps.getIndexKafkaMessages(), kafkaMessageLog);
    }
}
