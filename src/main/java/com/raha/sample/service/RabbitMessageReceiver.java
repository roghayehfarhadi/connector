package com.raha.sample.service;

import com.raha.sample.log.MessageLogger;
import com.raha.sample.log.RabbitMessageLog;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

import static com.raha.sample.log.LogType.CONSUMED;

@Configuration
@RequiredArgsConstructor
public class RabbitMessageReceiver {

    private final MessageLogger messageLogger;

    @RabbitListener(queues = "sample.queue")
    public void receiveSampleData(Message message) {
        String messageBody = new String(message.getBody());
        messageLogger.log(new RabbitMessageLog(CONSUMED, messageBody));
    }
}
