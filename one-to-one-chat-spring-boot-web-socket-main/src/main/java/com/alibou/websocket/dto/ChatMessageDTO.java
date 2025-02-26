package com.alibou.websocket.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Setter
@Getter
public class ChatMessageDTO {

    private String content;
    private LocalDateTime timestamp;
}
