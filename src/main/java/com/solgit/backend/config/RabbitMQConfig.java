package com.solgit.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class RabbitMQConfig {

    private final QueueConfigProperties queueConfigProperties;

    public RabbitMQConfig(QueueConfigProperties queueConfigProperties) {
        this.queueConfigProperties = queueConfigProperties;
    }

    // Exchange와 Routing Key 매핑 설정
    // queueType에 따라 다른 exchange와 routing key를 사용할 수 있도록 설정
    @Bean
    public Map<String, QueueConfig> queueConfigMap() {
        if (queueConfigProperties.getQueues() == null) {
            return new HashMap<>();
        }
        
        return queueConfigProperties.getQueues().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new QueueConfig(
                                entry.getValue().getExchange(),
                                entry.getValue().getRoutingKey()
                        )
                ));
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    // QueueConfig 내부 클래스
    public static class QueueConfig {
        private final String exchange;
        private final String routingKey;

        public QueueConfig(String exchange, String routingKey) {
            this.exchange = exchange;
            this.routingKey = routingKey;
        }

        public String getExchange() {
            return exchange;
        }

        public String getRoutingKey() {
            return routingKey;
        }
    }
}
