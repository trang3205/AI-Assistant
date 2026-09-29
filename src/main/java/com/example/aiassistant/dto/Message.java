package com.example.aiassistant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// DTO đại diện cho một tin nhắn đơn lẻ trong lịch sử hội thoại
public class Message {
    private String role; // "system" | "user" | "assistant"
    private String content;
}