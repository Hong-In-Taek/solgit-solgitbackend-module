package com.solgit.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    
    @JsonProperty("header")
    private MessageHeader header;
    
    @JsonProperty("body")
    private MessageBody body;
    
    /**
     * 새로운 메시지 생성
     */
    public static Message newMessage(String messageType, Object payload, String source, String correlationId) {
        MessageHeader header = new MessageHeader();
        header.setMessageId(UUID.randomUUID().toString());
        header.setMessageType(messageType);
        header.setVersion("v1");
        header.setTimestamp(Instant.now());
        header.setCorrelationId(correlationId);
        header.setSource(source);
        
        MessageBody body = new MessageBody();
        body.setPayload(payload);
        
        Message message = new Message();
        message.setHeader(header);
        message.setBody(body);
        
        return message;
    }
}
