package com.solgit.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "rabbitmq")
@Getter
@Setter
public class QueueConfigProperties {
    
    private Map<String, QueueConfig> queues;
    
    @Getter
    @Setter
    public static class QueueConfig {
        private String exchange;
        private String routingKey;
    }
}
