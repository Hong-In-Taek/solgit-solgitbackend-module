package com.solgit.backend.controller;

import com.solgit.backend.dto.MessageRequest;
import com.solgit.backend.dto.MessageResponse;
import com.solgit.backend.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/publish")
    public ResponseEntity<MessageResponse> publishMessage(@Valid @RequestBody MessageRequest request) {
        MessageResponse response = messageService.publishMessage(
            request.getRoutingKey(),
            request.getMessageType(),
            request.getPayload(),
            request.getQueueName()
        );
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
