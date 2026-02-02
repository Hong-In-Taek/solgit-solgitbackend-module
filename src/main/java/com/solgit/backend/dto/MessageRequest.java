package com.solgit.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {
    
    @NotBlank(message = "routingKey는 필수입니다")
    private String routingKey;
    
    @NotBlank(message = "messageType은 필수입니다")
    private String messageType;
    
    @NotNull(message = "payload는 필수입니다")
    private Object payload;
    
    private String queueName; // 선택적, 지정하면 해당 Queue로 직접 발행
}
