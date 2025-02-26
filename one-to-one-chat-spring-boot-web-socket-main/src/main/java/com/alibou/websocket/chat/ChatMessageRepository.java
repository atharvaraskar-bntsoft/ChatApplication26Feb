package com.alibou.websocket.chat;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.*;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByChatId(String chatId);

    Optional<ChatMessage> findTopByChatIdOrderByTimestampDesc(String chatId);
}
