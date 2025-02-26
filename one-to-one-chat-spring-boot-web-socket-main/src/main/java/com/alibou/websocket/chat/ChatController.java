package com.alibou.websocket.chat;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.alibou.websocket.dto.ChatMessageDTO;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        chatMessageService.processMessage(chatMessage);
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable String senderId,
                                                              @PathVariable String recipientId) {
        return ResponseEntity.ok(chatMessageService.findChatMessages(senderId, recipientId));
    }

    // @GetMapping("/messages/latest/{senderId}/{recipientId}")
    // public ResponseEntity<ChatMessage> findLatestChatMessage(@PathVariable String senderId,
    //                                                         @PathVariable String recipientId) {
    //     return ResponseEntity.of(chatMessageService.findLatestChatMessage(senderId, recipientId));
    // }

        @GetMapping("/messages/latest/{senderId}/{recipientId}")
    public ResponseEntity<ChatMessageDTO> getLatestMessage(@PathVariable String senderId,
                                                        @PathVariable String recipientId) {
        return chatMessageService.findLatestChatMessage(senderId, recipientId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

}
