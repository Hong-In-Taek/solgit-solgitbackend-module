package com.solgit.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageHeader {
    
    @JsonProperty("messageId")
    private String messageId;
    
    @JsonProperty("messageType")
    private String messageType;
    
    @JsonProperty("version")
    private String version;
    
    @JsonProperty("timestamp")
    private Instant timestamp;
    
    @JsonProperty("correlationId")
    private String correlationId;
    
    @JsonProperty("source")
    private String source;
}
