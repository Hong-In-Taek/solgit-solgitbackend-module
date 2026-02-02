package com.solgit.backend.service;

import com.solgit.backend.config.RabbitMQConfig;
import com.solgit.backend.dto.MessageResponse;
import com.solgit.backend.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final RabbitTemplate rabbitTemplate;
    private final Map<String, RabbitMQConfig.QueueConfig> queueConfigMap;
    
    @Value("${spring.application.name:solgit-backend-module}")
    private String source;

    public MessageResponse publishMessage(String routingKey, String messageType, Object payload, String queueName) {
        try {
            // 표준 메시지 포맷으로 변환
            Message message = Message.newMessage(messageType, payload, source, null);
            
            // queueType에 해당하는 설정 조회 (routingKey를 queueType으로 사용, 대소문자 구분 없이)
            RabbitMQConfig.QueueConfig config = findQueueConfig(routingKey);
            
            String exchange;
            String finalRoutingKey;
            
            if (config != null) {
                // queueType 매핑이 있으면 사용
                exchange = config.getExchange();
                finalRoutingKey = config.getRoutingKey();
            } else {
                // queueType 매핑이 없으면 기본 exchange 사용 (routingKey를 그대로 사용)
                exchange = getDefaultExchange();
                finalRoutingKey = routingKey;
            }
            
            // 메시지 발행 (표준 포맷 사용)
            rabbitTemplate.convertAndSend(exchange, finalRoutingKey, message, messagePostProcessor -> {
                MessageProperties props = messagePostProcessor.getMessageProperties();
                
                // DeliveryMode를 Persistent로 설정
                props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                
                // MessageId 설정
                props.setMessageId(message.getHeader().getMessageId());
                
                // Type 설정
                props.setType(message.getHeader().getMessageType());
                
                // Timestamp 설정
                if (message.getHeader().getTimestamp() != null) {
                    props.setTimestamp(java.util.Date.from(message.getHeader().getTimestamp()));
                }
                
                // Headers 설정
                props.setHeader("correlationId", message.getHeader().getCorrelationId());
                props.setHeader("source", message.getHeader().getSource());
                props.setHeader("version", message.getHeader().getVersion());
                
                return messagePostProcessor;
            });
            
            log.info("메시지 발행 성공 - exchange: {}, routingKey: {}, messageId: {}, messageType: {}", 
                    exchange, finalRoutingKey, message.getHeader().getMessageId(), message.getHeader().getMessageType());

            return new MessageResponse(
                true,
                message.getHeader().getMessageId(),
                null
            );

        } catch (Exception e) {
            log.error("메시지 발행 실패 - routingKey: {}, messageType: {}", routingKey, messageType, e);
            return new MessageResponse(
                false,
                null,
                "메시지 발행 중 오류가 발생했습니다: " + e.getMessage()
            );
        }
    }
    
    /**
     * 대소문자 구분 없이 QueueConfig 찾기
     */
    private RabbitMQConfig.QueueConfig findQueueConfig(String routingKey) {
        // 정확한 매칭 먼저 시도
        RabbitMQConfig.QueueConfig config = queueConfigMap.get(routingKey);
        if (config != null) {
            return config;
        }
        
        // 대소문자 구분 없이 매칭 시도
        for (Map.Entry<String, RabbitMQConfig.QueueConfig> entry : queueConfigMap.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(routingKey)) {
                return entry.getValue();
            }
        }
        
        return null;
    }
    
    private String getDefaultExchange() {
        // 기본 exchange 이름 (설정 파일에서 가져올 수도 있음)
        return "publish.exchange";
    }
}
