package com.raha.sample.config;

import com.raha.sample.config.properties.RabbitMQConfigProps;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    private final RabbitMQConfigProps rabbitConfigProps;

    @Bean
    Queue queue() {
        return new Queue(rabbitConfigProps.getQueueName(), true);
    }

    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(rabbitConfigProps.getExchangeName());
    }

    @Bean
    Binding binding(Queue queue, DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(rabbitConfigProps.getRoutingKey());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public ConnectionFactory internalConnectionFactory() {
        return new CachingConnectionFactory(rabbitConfigProps.getServerAddress(), rabbitConfigProps.getServerPort());
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory internalConnectionFactory, MessageConverter jsonMessageConverter) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(internalConnectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }

}
